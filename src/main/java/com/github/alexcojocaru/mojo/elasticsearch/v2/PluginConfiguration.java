package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Alex Cojocaru
 *
 */
public class PluginConfiguration
{
    private String uri;
    private String esJavaOpts;
    
    
    public String getUri()
    {
        return uri;
    }

    public String getEsJavaOpts()
    {
        return esJavaOpts;
    }
    
    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public void setEsJavaOpts(String esJavaOpts)
    {
        this.esJavaOpts = esJavaOpts;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("uri", uri)
                .append("esJavaOpts", esJavaOpts)
                .toString();
    }

}
