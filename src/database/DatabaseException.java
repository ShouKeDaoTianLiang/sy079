package database;

public class DatabaseException extends RuntimeException
{
    private static final long serialVersionUID = -420103154764822555L;
    
    public DatabaseException(final String msg) {
        super(msg);
    }
    
    public DatabaseException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    DatabaseException(final Exception e) {
        throw new UnsupportedOperationException(e);
    }
}
