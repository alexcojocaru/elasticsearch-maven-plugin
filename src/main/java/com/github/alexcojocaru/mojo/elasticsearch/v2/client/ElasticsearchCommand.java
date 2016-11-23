package com.github.alexcojocaru.mojo.elasticsearch.v2.client;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Alex Cojocaru
 *
 */
public class ElasticsearchCommand
{
    public enum RequestMethod
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
    private boolean skip;

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
    public boolean isSkip()
    {
        return skip;
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
    public void setSkip(boolean skip)
    {
        this.skip = skip;
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("requestMethod", requestMethod)
                .append("relativeUrl", relativeUrl)
                .append("json", json)
                .append("skip", skip)
                .toString();
    }
}
