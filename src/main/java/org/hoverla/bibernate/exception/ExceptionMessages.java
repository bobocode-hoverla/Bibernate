package org.hoverla.bibernate.exception;

public interface ExceptionMessages {
    // datasource
    String DATA_SOURCE_NOT_FOUND_MSG = "Cannot configure appropriate datasource. " +
            "Please, recheck your configuration. Provider that is specified in your configuration is %s";

    // properties
    String USE_STATIC_METHOD_TO_CREATE_PROPS = "Private access to this constructor. Please, use public static method to create properties";
    String CANNOT_LOAD_PROPERTIES = "Private access to this constructor. Please, use public static method to create properties";

    // pool
    String CANNOT_TAKE_CONNECTION_FROM_POOL = "Unable to take connection from pool";
    String CANNOT_GET_PHYSICAL_CONNECTION = "Could not retrieve physical connection from underlying driver data source";

    // transaction
    String TRANSACTION_ALREADY_BEGUN = "Transaction has already begun";
    String TRANSACTION_BEGIN_ERROR = "Could not begin transaction";
    String TRANSACTION_ROLLBACK_ERROR = "Could not rollback transaction";
    String TRANSACTION_COMMIT_ERROR = "Could not commit transaction";
    String TRANSACTION_CLOSE_ERROR = "Could not close the connection in transaction";
}
