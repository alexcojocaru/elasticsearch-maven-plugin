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
     * @param message
     */
    public ElasticsearchClientException(String message)
    {
        super(message);
    }

    /**
     * @param method 
     * @param statusCode
     * @param responseContent
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
     * @param cause
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
