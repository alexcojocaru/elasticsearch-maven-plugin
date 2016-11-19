package com.github.alexcojocaru.mojo.elasticsearch.v2;

/**
 * @author Alex Cojocaru
 */
public class ElasticsearchSetupException
        extends RuntimeException
{
    private static final long serialVersionUID = 7187554107621129031L;

    /**
     * @param message
     * @param cause
     */
    public ElasticsearchSetupException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ElasticsearchSetupException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public ElasticsearchSetupException(Throwable cause)
    {
        super(cause);
    }

}
