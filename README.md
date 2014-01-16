# Elasticsearch Maven Plugin

A Maven plugin to run a single node Elasticsearch cluster during the integration test phase of a build.
Although it is not a local Elasticsearch node per se
(for it must be able to communicate to other nodes outside the JVM),
it is as lightweight as possible (1 shard, 0 replicas, multicast discovery disabled and zen ping timeout set to 3ms).

## Usage
The following Elasticsearch properties can be configured through the plugin configuration section:

*   **clusterName** [required]:
    > the name of the cluster to create;

*   **tcpPort** [required]
    > the port for the node to node communication;

*   **httpPort** [required]
    > the port to listen to HTTP traffic;

*   **outputDirectory** [optional]
    > the directory where the Elasticsearch data directory will be created (defaults to the project build directory);

*   **dataDirname** [optional]
    > the name of the directory within the *outputDirectory* (see the property above) where the Elasticsearch data will be stored;

*   **logsDirname** [optional]
    > the name of the directory within the *outputDirectory* (see the property above) where the Elasticsearch logs will be created;

*   **scriptFile** [required by the *load* goal]
    > a list of commands to be executed to provision the Elasticsearch cluster. See the [load.script](#load.script) section for details.

Include the following in the pom.xml file and modify the configuration as needed:

        <plugin>
    	    <groupId>com.pingconnect</groupId>
    	    <artifactId>elasticsearch-maven-plugin</artifactId>
    	    <version>1.0</version>
    	    <configuration>
    			<clusterName>test</clusterName>
    			<tcpPort>9300</tcpPort>
    			<httpPort>9200</httpPort>
    	    </configuration>
    	    <executions>
    	        <!-- The elasticsearch plugin is by default bound to the
    	        pre-integration-test and post-integration-test phases -->
    	        <execution>
    	            <id>start-elasticsearch</id>
    	            <phase>pre-integration-test</phase>
    	            <goals>
    	                <goal>start</goal>
    	            </goals>
    	        </execution>
    	        <execution>
    	            <id>deploy-json</id>
    	            <phase>pre-integration-test</phase>
    	            <goals>
    	                <goal>load</goal>
    	            </goals>
    	            <configuration>
                        <scriptFile>src/test/resources/elasticsearch.script</scriptFile>
    	            </configuration>
    	        </execution>
    	        <execution>
    	            <id>stop-elasticsearch</id>
    	            <phase>post-integration-test</phase>
    	            <goals>
    	                <goal>stop</goal>
    	            </goals>
    	        </execution>
    	    </executions>
    	    <dependencies>
            	<dependency>
        			<groupId>org.apache.commons</groupId>
        			<artifactId>commons-io</artifactId>
        			<version>1.3.2</version>
        		</dependency>
        		<dependency>
        			<groupId>org.elasticsearch</groupId>
        			<artifactId>elasticsearch</artifactId>
        			<version>0.90.7</version>
        		</dependency>
        		<dependency>
        		    <groupId>org.apache.httpcomponents</groupId>
        		    <artifactId>httpclient</artifactId>
        		    <version>4.3.1</version>
        		</dependency>
    	    </dependencies>
    	</plugin>

## <a name="load.script"></a>Load script
An load script file can be provided to the *load* goal of the plugin, in which case it will be executed on the local Elasticsearch cluster.

The empty lines are ignored, as well as lines starting with the '#' sign.

Each command has three parts, separated by colon.

* the request method: one of *PUT*, *POST*, *DELETE*;
    > this is the name (uppercase) of the request method to be used for the current command;

* the path part of the URL (should not start with a slash);
    > will be appended to the protocol, hostname and port parts when the full URL is constructed;

* the JSON to send to Elasticsearch; it should be empty for a DELETE command.


**Examples** (see the *src/test/resources/goals/load/load.script* file for more examples)
:
> POST:test\_index/test\_type/\_mapping:{ "test\_type" : { "properties" : { "name" : { "type" : "string" }, "lastModified" : { "type" : "date" } } } }
>> A *POST* request will be send to *http://localhost:9200/test\_index/test\_type/\_mapping*

> DELETE:test\_index/test\_type/1:
>> A *DELETE* request will be send to *http://localhost:9200/test\_index/test\_type/1* with no content. Note the colon at the end, for there is no JSON data in case of a DELETE.
