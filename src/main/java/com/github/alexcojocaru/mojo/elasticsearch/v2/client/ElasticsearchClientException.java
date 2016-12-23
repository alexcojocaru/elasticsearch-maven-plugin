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
    
    private String method = "";
    private int statusCode = -1;
    private String responseContent = "";
    

    /**
     * @param message The exception message
     */
    public ElasticsearchClientException(String message)
    {
        super(message);
    }

    /**
     * @param method The HTTP method which triggered the exception
     * @param statusCode The status code on the response which triggered the exception
     * @param responseContent The content in the response which triggered the exception
     */
    public ElasticsearchClientException(String method, int statusCode, String responseContent)
    {
        super(String.format(
                "%s failed with HTTP status code %d; content: %s",
                method,
                statusCode,
                responseContent));
        
        this.method = method;
        this.statusCode = statusCode;
        this.responseContent = responseContent;
    }

    /**
     * @param cause The exception cause
     */
    public ElasticsearchClientException(Throwable cause)
    {
        super(cause);
    }
    
    
    public String getMethod()
    {
        return method;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public String getResponseContent()
    {
        return responseContent;
    }

}
