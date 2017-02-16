# Elasticsearch Maven Plugin [![Build Status](https://travis-ci.org/alexcojocaru/elasticsearch-maven-plugin.png?branch=master)](https://travis-ci.org/alexcojocaru/elasticsearch-maven-plugin)

A Maven 3.1+ plugin to run instances of Elasticsearch version 5+ during the integration test phase of a build.
Instances are started in forked processes using the **runforked** goal.
They are terminated using the **stop** goal and, for extra peace of mind, using a JVM shutdown hook.

Each instance is installed in _${project.build.directory}/elasticsearch${instanceIndex}_.

For Elasticsearch version 1.x.x and 2.x.x support, see version 1.x and 2.x of this plugin.

Because the plugin uses the new [Eclipse based Aether framework](https://www.eclipse.org/aether/), it only works with Maven 3.1.0 and above.
See [this discussion](https://github.com/alexcojocaru/elasticsearch-maven-plugin/issues/28) for details.

## Usage
The Elasticsearch behaviour and properties can be configured through the following plugin configuration parameters:

*   **instanceCount** [defaultValue=1]
    > how many Elasticsearch instances to start (all within the same cluster)

*   **skip** [defaultValue=false]
    > whether to skip the plugin execution or not

*   **clusterName** [defaultValue="test"]
    > the name of the cluster to create

*   **version** [defaultValue="5.0.0"]
    > the version of Elasticsearch to install

*   **httpPort** [defaultValue=9200]
    > the port to configure Elasticsearch to listen to HTTP traffic to; when configuring multiple instances, they will be assigned subsequent HTTP ports starting with this value (mind the conflicts with the transport ports)

*   **transportPort** [defaultValue=9300]
    > the port for the Elasticsearch node to node communication; when configuring multiple instances, they will be assigned subsequent transport ports starting with this value (mind the conflicts with the HTTP ports)

*   **pathConf** [defaultValue=""] (note: common to all instances !!!)
    > the absolute path (or relative to the maven project) of the custom directory containing configuration files, to be copied to Elasticsearch instances

*   **pathData** [defaultValue=""] - work in progress (note: per instance !!!)
    > the custom data directory to configure in Elasticsearch

*   **pathLogs** [defaultValue=""] - work in progress (note: per instance !!!)
    > the custom logs directory to configure in Elasticsearch

*   **pathScripts** [defaultValue=""]
    > the absolute path (or relative to the maven project) of the custom directory containing file-based scripts, to be used in Elasticsearch

*   **plugins** [defaultValue=""]
    > the list of plugins to install in each Elasticsearch instance before starting it (see the [Plugins](#plugins) section for details)

*   **pathInitScript** [defaultValue=""]
    > the path of the initialization script (see the [Initialization script](#initScript) section for details)

*   **keepExistingData** [defaultValue=false] - work in progress
    > whether to keep the data and log directories if they already exist

*   **timeout** [defaultValue=30]
    > how long to wait (in seconds) for each Elasticsearch instance to start up

*   **setAwait** [defaultValue=false]
    > whether to block the execution once all Elasticsearch instances have started, so that the maven build will not proceed to the next step; use CTRL+C to abort the process

*   **autoCreateIndex** [defaultValue=true]
    > configuration of automatic index creation represented by _action.auto\_create\_index_ setting

*   **logLevel** [defaultValue=INFO]
    > the log level to be used by the console logger; the valid values are defined in AbstractElasticsearchBaseMojo.getMavenLogLevel() and they are: DEBUG, INFO, WARN, ERROR, FATAL, DISABLED.


To use the plugin, include the following in your _pom.xml_ file and modify the configuration as needed:

        <plugin>
    	    <groupId>com.github.alexcojocaru</groupId>
    	    <artifactId>elasticsearch-maven-plugin</artifactId>
			<!-- REPLACE THE FOLLOWING WITH THE PLUGIN VERSION YOU NEED -->
    	    <version>5.0</version>
    	    <configuration>
    			<clusterName>test</clusterName>
    			<tcpPort>9300</tcpPort>
    			<httpPort>9200</httpPort>
    	    </configuration>
    	    <executions>
    	        <!--
					The elasticsearch maven plugin goals are by default bound to the
					pre-integration-test and post-integration-test phases
				-->
    	        <execution>
    	            <id>start-elasticsearch</id>
    	            <phase>pre-integration-test</phase>
    	            <goals>
    	                <goal>runforked</goal>
    	            </goals>
    	        </execution>
    	        <execution>
    	            <id>stop-elasticsearch</id>
    	            <phase>post-integration-test</phase>
    	            <goals>
    	                <goal>stop</goal>
    	            </goals>
    	        </execution>
    	    </executions>
    	</plugin>


## <a name="plugins"></a>Plugins
A list of Elasticsearch plugins can be provided to the elasticsearch-maven-plugin.
They will be installed into [each Elasticsearch instance](https://www.elastic.co/guide/en/elasticsearch/plugins/5.2/intro.html)
inside the [plugins directory](https://www.elastic.co/guide/en/elasticsearch/reference/5.2/zip-targz.html#zip-targz-layout)
using the [--batch option](https://www.elastic.co/guide/en/elasticsearch/plugins/5.2/_other_command_line_parameters.html#_batch_mode),
before the instance gets started.

The way to enable plugins is as follows:

        <plugin>
            <groupId>com.github.alexcojocaru</groupId>
            <artifactId>elasticsearch-maven-plugin</artifactId>
            <version>5.5</version
            <configuration>
                <clusterName>test</clusterName>
                <tcpPort>9300</tcpPort>
                <httpPort>9200</httpPort>
                ...
                <plugins>
                    <plugin>
                        <uri>analysis-icu</uri>
                    <plugin>
                    <plugin>
                        <uri>https://github.com/alexcojocaru/plugin.zip</uri>
                        <esJavaOpts>-Djavax.net.ssl.trustStore=/home/alex/trustStore.jks</esJavaOpts>
                    <plugin>
                    <plugin>
                        <uri>file:///home/alex/foo.zip</uri>
                    <plugin>
                </plugins>
                ...
            </configuration>
            <executions>
                ...
            </executions>
        </plugin>

The plugin tag takes 2 parameters:

*   **uri**
    > the [name](https://www.elastic.co/guide/en/elasticsearch/plugins/5.2/installation.html),
    [url](https://www.elastic.co/guide/en/elasticsearch/plugins/5.2/plugin-management-custom-url.html)
    or [file location](https://www.elastic.co/guide/en/elasticsearch/plugins/5.2/plugin-management-custom-url.html)
    of the plugin

*   **esJavaOpts** [defaultValue=""]
    > [additional Elasticsearch Java options](https://www.elastic.co/guide/en/elasticsearch/plugins/5.2/plugin-management-custom-url.html)
    to be passed to the plugin installation tool when installing the plugin


## <a name="initScript"></a>Initialization script
An initialization script file can be provided using the **pathInitScript** parameter of the plugin, in which case it will be executed against the local Elasticsearch cluster.

Empty lines are ignored, as well as lines starting with the '#' sign.

Each command has three parts, separated by colon.

* the request method: one of *PUT*, *POST*, *DELETE*
    > the name (in uppercase) of the request method to be used for the current command

* the path part of the URL (should not start with slash)
    > will be appended to the protocol, hostname and port parts when the full URL is constructed

* the JSON to send to Elasticsearch; for DELETE commands it should be empty for a DELETE


**Examples** (see the *src/it/runforked-with-init-script/init.script* file for a more complete example):

* To send a *POST* request to *http://localhost:9200/test\_index/test\_type/\_mapping*:
> POST:test\_index/test\_type/\_mapping:{ "test\_type" : { "properties" : { "name" : { "type" : "string" }, "lastModified" : { "type" : "date" } } } }

* To send a *DELETE* request to *http://localhost:9200/test\_index/test\_type/1* without content; note the colon at the end, for there is no JSON data in case of a DELETE.
> DELETE:test\_index/test\_type/1:
