package lk.icbt.dentalclinic.dao;

/**
 * Unchecked exception wrapping a real SQLException message, so calling
 * layers (service, REST, UI) can surface the actual database error
 * instead of a generic "operation failed" message.
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}