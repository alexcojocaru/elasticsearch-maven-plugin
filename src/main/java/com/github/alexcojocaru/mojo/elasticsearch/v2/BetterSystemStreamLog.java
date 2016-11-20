package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.maven.plugin.logging.SystemStreamLog;

/**
 * A better simple log implementation, with support for enforcing the log level at log time. 
 * 
 * @author Alex Cojocaru
 *
 */
public class BetterSystemStreamLog extends SystemStreamLog
{
    enum LogLevel {DEBUG, INFO, WARN, ERROR};

    
    private LogLevel logLevel;
    
    
    public BetterSystemStreamLog(LogLevel logLevel)
    {
        this.logLevel = logLevel;
    }


    @Override
    public void debug(CharSequence content)
    {
        if (isDebugEnabled())
        {
            super.debug(content);
        }
    }

    @Override
    public void debug(CharSequence content, Throwable error)
    {
        if (isDebugEnabled())
        {
            super.debug(content, error);
        }
    }

    @Override
    public void debug(Throwable error)
    {
        if (isDebugEnabled())
        {
            super.debug(error);
        }
    }

    @Override
    public void info(CharSequence content)
    {
        if (isInfoEnabled())
        {
            super.info(content);
        }
    }

    @Override
    public void info(CharSequence content, Throwable error)
    {
        if (isInfoEnabled())
        {
            super.info(content, error);
        }
    }

    @Override
    public void info(Throwable error)
    {
        if (isInfoEnabled())
        {
            super.info(error);
        }
    }

    @Override
    public void warn(CharSequence content)
    {
        if (isWarnEnabled())
        {
            super.warn(content);
        }
    }

    @Override
    public void warn(CharSequence content, Throwable error)
    {
        if (isWarnEnabled())
        {
            super.warn(content, error);
        }
    }

    @Override
    public void warn(Throwable error)
    {
        if (isWarnEnabled())
        {
            super.warn(error);
        }
    }

    @Override
    public void error(CharSequence content)
    {
        if (isErrorEnabled())
        {
            super.error(content);
        }
    }

    @Override
    public void error(CharSequence content, Throwable error)
    {
        if (isErrorEnabled())
        {
            super.error(content, error);
        }
    }

    @Override
    public void error(Throwable error)
    {
        if (isErrorEnabled())
        {
            super.error(error);
        }
    }
    
    @Override
    public boolean isDebugEnabled()
    {
        return logLevel == LogLevel.DEBUG;
    }

    @Override
    public boolean isInfoEnabled()
    {
        return logLevel == LogLevel.INFO
                || logLevel == LogLevel.DEBUG;
    }

    @Override
    public boolean isWarnEnabled()
    {
        return logLevel == LogLevel.WARN
                || logLevel == LogLevel.INFO
                || logLevel == LogLevel.DEBUG;
    }

    @Override
    public boolean isErrorEnabled()
    {
        return logLevel == LogLevel.ERROR
                || logLevel == LogLevel.WARN
                || logLevel == LogLevel.INFO
                || logLevel == LogLevel.DEBUG;
    }
    
}
