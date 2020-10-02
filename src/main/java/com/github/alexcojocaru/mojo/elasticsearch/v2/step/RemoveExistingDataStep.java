package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.FilesystemUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class RemoveExistingDataStep
        implements InstanceStep
{
    @Override
    public void execute(InstanceConfiguration config)
    {
        if (Boolean.FALSE.equals(config.getClusterConfiguration().isKeepExistingData()))
        {
            File baseDir = new File(config.getBaseDir());

            try
            {
                File dataDir = FilesystemUtil.getDataDirectory(baseDir);
                File logsDir = FilesystemUtil.getLogsDirectory(baseDir);

                FileUtils.deleteDirectory(dataDir);
                FileUtils.deleteDirectory(logsDir);
            }
            catch (IOException e)
            {
                throw new ElasticsearchSetupException(
                        "Failed to delete the existing Elasticsearch data from "
                                + baseDir.getAbsolutePath(),
                        e);
            }
        }
    }
}
