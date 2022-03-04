package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

import org.junit.Assert;
import org.junit.Test;

public class FilesystemUtilTest
{
    
    @Test
    public void test_fixFileUrl()
    {
        String url;
        
        url = null;
        Assert.assertEquals(url, FilesystemUtil.fixFileUrl(url));

        url = "something_something";
        Assert.assertEquals(url, FilesystemUtil.fixFileUrl(url));

        url = "http://elastic.com/file.zip";
        Assert.assertEquals(url, FilesystemUtil.fixFileUrl(url));

        url = "file:///home/user/file.zip";
        Assert.assertEquals(url, FilesystemUtil.fixFileUrl(url));

        url = "file://C:Users/user/file.zip";
        Assert.assertEquals(url, FilesystemUtil.fixFileUrl(url));

        url = "file://C/Users/user/file.zip";
        Assert.assertEquals(url, FilesystemUtil.fixFileUrl(url));

        url = "file://C:\\Users\\user\\file.zip";
        Assert.assertEquals("file:///C:\\Users\\user\\file.zip", FilesystemUtil.fixFileUrl(url));

        url = "file://C:/Users/user/file.zip";
        Assert.assertEquals("file:///C:/Users/user/file.zip", FilesystemUtil.fixFileUrl(url));
    }

}
