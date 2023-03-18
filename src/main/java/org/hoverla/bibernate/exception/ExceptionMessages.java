package org.hoverla.bibernate.exception;

public interface ExceptionMessages {
    // datasource
    String DATA_SOURCE_NOT_FOUND_MSG = "Cannot configure appropriate datasource. " +
            "Please, recheck your configuration. Provider that is specified in your configuration is %s";

    // transaction
    String TRANSACTION_ALREADY_BEGUN = "Transaction has already begun";
    String TRANSACTION_BEGIN_ERROR = "Could not begin transaction";
    String TRANSACTION_ROLLBACK_ERROR = "Could not rollback transaction";
    String TRANSACTION_COMMIT_ERROR = "Could not commit transaction";
    String TRANSACTION_CLOSE_ERROR = "Could not close the connection in transaction";
}
