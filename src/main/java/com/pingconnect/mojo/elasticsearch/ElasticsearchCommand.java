package com.pingconnect.mojo.elasticsearch;

/**
 * @author alexcojocaru
 *
 */
public class ElasticsearchCommand
{
    enum RequestMethod
    {
        PUT,
        POST,
        DELETE;
        
        public static RequestMethod fromName(String name)
        {
            for (RequestMethod method : values())
            {
                if (method.name().equalsIgnoreCase(name.trim()))
                {
                    return method;
                }
            }
            
            throw new IllegalArgumentException("Unknown request method name: " + name);
        }
    }
    
    private RequestMethod requestMethod;
    private String relativeUrl;
    private String json;

    /**
     * @return the requestMethod
     */
    public RequestMethod getRequestMethod()
    {
        return requestMethod;
    }
    /**
     * @return the relativeUrl
     */
    public String getRelativeUrl()
    {
        return relativeUrl;
    }
    /**
     * @return the json
     */
    public String getJson()
    {
        return json;
    }
    /**
     * @param requestMethod the requestMethod to set
     */
    public void setRequestMethod(RequestMethod requestMethod)
    {
        this.requestMethod = requestMethod;
    }
    /**
     * @param relativeUrl the relativeUrl to set
     */
    public void setRelativeUrl(String relativeUrl)
    {
        this.relativeUrl = relativeUrl;
    }
    /**
     * @param json the json to set
     */
    public void setJson(String json)
    {
        this.json = json;
    }
    
    @Override
    public String toString()
    {
        return "ElasticsearchCommand ["
                + "requestMethod=" + requestMethod
                + ", relativeUrl=" + relativeUrl
                + ", json=" + json
                + "]";
    }

}
