package com.github.alexcojocaru.mojo.elasticsearch.v2;

/**
 * @author Alex Cojocaru
 */
public class ElasticsearchSetupException
        extends RuntimeException
{
    private static final long serialVersionUID = 7187554107621129031L;

    /**
     * @param message The exception message
     * @param cause The cause
     */
    public ElasticsearchSetupException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param message The message
     */
    public ElasticsearchSetupException(String message)
    {
        super(message);
    }

    /**
     * @param cause The cause
     */
    public ElasticsearchSetupException(Throwable cause)
    {
        super(cause);
    }

}
