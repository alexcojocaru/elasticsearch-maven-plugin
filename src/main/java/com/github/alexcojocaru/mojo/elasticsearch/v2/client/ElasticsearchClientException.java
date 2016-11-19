package com.github.alexcojocaru.mojo.elasticsearch.v2.client;

/**
 * 
 * @author Alex Cojocaru
 *
 */
public class ElasticsearchClientException
        extends Exception
{
    private static final long serialVersionUID = 8572343147542928247L;

    /**
     * @param message
     * @param cause
     */
    public ElasticsearchClientException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ElasticsearchClientException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public ElasticsearchClientException(Throwable cause)
    {
        super(cause);
    }

}
