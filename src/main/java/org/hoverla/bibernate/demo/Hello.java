package org.hoverla.bibernate.demo;

import lombok.extern.slf4j.Slf4j;
import org.hoverla.bibernate.configuration.Configuration;
import org.hoverla.bibernate.configuration.PropertiesConfiguration;
import org.hoverla.bibernate.connectionpool.util.BibariDataSource;
import org.hoverla.bibernate.exception.datasource.JDBCConnectionException;
import org.hoverla.bibernate.session.factory.SessionFactoryImpl;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Slf4j
public class Hello {
    public static void main(String[] args) {
        Configuration configuration = new PropertiesConfiguration();
        DataSource bibariDataSource = new BibariDataSource(configuration);
        var sessionFactory = new SessionFactoryImpl(bibariDataSource, configuration);
        var session = sessionFactory.openSession();
        var transaction = session.getTransaction();

        try (sessionFactory; session) {
            transaction.begin();

            Customer customer = session.find(Customer.class, 1L);

            Customer newCustomer = new Customer();
            newCustomer.setFirstName("Anton");
            newCustomer.setLastName("Prudyus");
            newCustomer.setEmail("antonprudyus8978@gmail.com");
            newCustomer.setCreatedAt(LocalDateTime.now());
            session.persist(newCustomer);
            log.info("Found customer: {}, ", customer);
            Customer sameCustomer = session.find(Customer.class, 1L);

            //should be true, 1st level cache
            log.info("customer: {} is equal to customer {} : {} ",customer, sameCustomer, customer == sameCustomer);

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new JDBCConnectionException(e);
        }
    }
}
