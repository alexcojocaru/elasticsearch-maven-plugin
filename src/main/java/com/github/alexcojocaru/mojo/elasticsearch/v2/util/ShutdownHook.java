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
package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

/**
 * @author acojocaru@pingidentity.com
 *
 */
public interface ShutdownHook
{
    /**
     * Build a shutdown hook around the given process.
     * @param process
     */
    void attachShutdownHook(Process process);

}
