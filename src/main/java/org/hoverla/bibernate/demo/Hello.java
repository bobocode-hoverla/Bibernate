package org.hoverla.bibernate.demo;

import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.configuration.PropertiesConfiguration;
import org.hoverla.bibernate.connectionpool.util.BibariDataSource;
import org.hoverla.bibernate.exception.session.transaction.TransactionalOperationException;
import org.hoverla.bibernate.session.Session;
import org.hoverla.bibernate.session.factory.SessionFactory;
import org.hoverla.bibernate.session.factory.SessionFactoryImpl;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class Hello {
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        Configuration configuration = new PropertiesConfiguration();
        DataSource bibariDataSource = new BibariDataSource(configuration);
        sessionFactory = new SessionFactoryImpl(bibariDataSource);

        List<Customer> customers;
        try (Session session1 = sessionFactory.openSession()) {
            customers = session1.findAllBy(Customer.class, "firstName", "Anton");
        }
        log.info("Customers found: {}",  customers);

        doInTx(session -> {
            Customer customer = session.find(Customer.class, 45L);

            Customer newCustomer = new Customer();
            newCustomer.setFirstName("Anton");
            newCustomer.setLastName("Prudyus");
            newCustomer.setEmail("antonprudyus9747r424@gmail.com");
            newCustomer.setCreatedAt(LocalDateTime.now());
            session.persist(newCustomer);
            log.info("Found customer: {}, ", customer);
            Customer sameCustomer = session.find(Customer.class, 45L);
            //should be true, 1st level cache
            log.info("customer: {} is equal to customer {} : {} ", customer, sameCustomer, customer == sameCustomer);

            sameCustomer.setFirstName("DIRTY_CHECK_999");
            session.merge(sameCustomer);
        });

    }

    private static <T> T readInTx(Function<Session, T> sessionFunction) {
        var session = sessionFactory.openSession();
        var transactionManager = session.getTransactionManager();
        try {
            transactionManager.begin();
            T result = sessionFunction.apply(session);
            transactionManager.commit();
            return result;
        } catch (Exception e) {
            transactionManager.rollback();
            throw new TransactionalOperationException("Could not complete transaction", e);
        } finally {
            session.close();
        }
    }

    private static void doInTx(Consumer<Session> sessionConsumer) {
        readInTx(session -> {
            sessionConsumer.accept(session);
            return null;
        });
    }
}
