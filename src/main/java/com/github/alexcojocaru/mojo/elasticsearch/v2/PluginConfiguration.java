/***************************************************************************
 * Copyright (C) 2017 Ping Identity Corporation
 * All rights reserved.
 * 
 * The contents of this file are the property of Ping Identity Corporation.
 * You may not copy or use this file, in either source code or executable
 * form, except in compliance with terms set by Ping Identity Corporation.
 * For further information please contact:
 * 
 *     Ping Identity Corporation
 *     1001 17th Street Suite 100
 *     Denver, CO 80202
 *     303.468.2900
 *     http://www.pingidentity.com
 * 
 **************************************************************************/
package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author acojocaru@pingidentity.com
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
