# Elasticsearch Maven Plugin [![Build Status](https://travis-ci.org/alexcojocaru/elasticsearch-maven-plugin.png?branch=master)](https://travis-ci.org/alexcojocaru/elasticsearch-maven-plugin)

A Maven plugin to run Elasticsearch instances during the integration test phase of a build.
Although it is not a local Elasticsearch node per se (for it is not able to communicate to other nodes outside the JVM),
it is as lightweight as possible (1 shard, 0 replicas, multicast discovery disabled and zen ping timeout set to 3ms).

Another way to run a single node Elasticsearch cluster (providing a scriptFile is optional) is through the **run** goal,
which keeps the process running until it is terminated with CTRL+C.

The current plugin version supports Elasticsearch v2.x.x. For Elasticsearch v1.x.x support, see version 1.x of the plugin.

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

*   **keepData** [optional]
    > whether to keep the data and log directories if they already exist (defaults to _false_);

*   **configPath** [optional]
    > the path of the config directory to be used by the Elasticsearch instance.
    > It is required for scripting support, in which case the directory referred by this path should contain only a *scripts* directory, with the required scripts.
    > It is also required for logging support, in which case the directory referred by this path should contain a *logging.yml* file to define the log4j configuration (<https://www.elastic.co/guide/en/elasticsearch/reference/current/setup-configuration.html#logging>).
    > See the *src/test/resources/example/logging/config* for an example.
    > If the *${path.logs}* parameter is used in *logging.yml*, the *logsDirname* property has to be also defined, for it translates to *${path.logs}* in the elasticsearch configuration.
    > Note that you may also have to add log4j v1.x.x as dependency to the elasticsearch maven plugin configuration in your pom.xml;

*   **pluginsPath** [optional]
    > the path of the plugins directory to be used by the Elasticsearch instance.
    > It is needed for providing custom plugins to the ES instance, each plugin will be contained in a subdirectory;

*   **logsDirname** [optional]
    > the name of the directory within the *outputDirectory* (see the property above) where the Elasticsearch log files will be created. See the notes on *configPath* for details on logging;

*   **scriptFile** [required by the *load* goal]
    > a list of commands to be executed to provision the Elasticsearch cluster. See the [load.script](#load.script) section for details.

*   **autoCreateIndex** [optional]
    > configuration of automatic index creation represented by _action.auto\_create\_index_ setting (defaults to _false_).

Include the following in the pom.xml file and modify the configuration as needed:

        <plugin>
    	    <groupId>com.github.alexcojocaru</groupId>
    	    <artifactId>elasticsearch-maven-plugin</artifactId>
			<!-- REPLACE THE FOLLOWING WITH THE PLUGIN VERSION YOU REQUIRE -->
    	    <version>2.0</version>
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
					<groupId>org.elasticsearch</groupId>
					<artifactId>elasticsearch</artifactId>
					<!-- REPLACE THE FOLLOWING WITH THE VERSION OF YOUR DESIRE -->
					<version>2.0.0</version>
				</dependency>
			</dependencies>
    	</plugin>

## <a name="load.script"></a>Load script
A load script file can be provided to the *load* goal of the plugin, in which case it will be executed on the local Elasticsearch cluster.

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

## Run Elasticsearch node
*run* goal allows you to start the local Elastisearch cluster and keep it running until the process is killed.
An load script file can be provided to the *run* goal of the plugin, in which case it will be executed on the local Elasticsearch cluster.

## Multiple Instances
The plugin can support multiple instances of elastic search. This will require configuring executions for each cluster instance.
To ensure that each instance of an execution is refering to a specific cluster instance, it is required that the cluster name is the same for each instance.

         <plugin>
             <groupId>com.github.alexcojocaru</groupId>
             <artifactId>elasticsearch-maven-plugin</artifactId>
             <version>1.12</version>
             <executions>
                 <!-- Manage Cluster 1 -->
                 <execution>
                     <id>start-elasticsearch</id>
                     <phase>pre-integration-test</phase>
                     <goals>
                         <goal>start</goal>
                     </goals>
                     <configuration>
                         <clusterName>test</clusterName>
                         <tcpPort>9300</tcpPort>
                         <httpPort>9200</httpPort>
                         <configPath>${basedir}/../api/src/test/resources/elasticsearch/config</configPath>
                         <outputDirectory>${project.build.directory}/esrch1</outputDirectory>
                     </configuration>
                 </execution>
                 <execution>
                     <id>deploy-json</id>
                     <phase>pre-integration-test</phase>
                     <goals>
                         <goal>load</goal>
                     </goals>
                     <configuration>
                         <clusterName>test</clusterName>
                         <httpPort>9200</httpPort>
                         <scriptFile>src/test/resources/elasticsearch.script</scriptFile>
                     </configuration>
                 </execution>
                 <execution>
                     <id>stop-elasticsearch</id>
                     <phase>post-integration-test</phase>
                     <goals>
                         <goal>stop</goal>
                     </goals>
                     <configuration>
                         <clusterName>test</clusterName>
                         <httpPort>9200</httpPort>
                     </configuration>
                 </execution>
                 <!-- Manage Cluster #2 -->
                 <execution>
                     <id>start-elasticsearch-2</id>
                     <phase>pre-integration-test</phase>
                     <goals>
                         <goal>start</goal>
                     </goals>
                     <configuration>
                         <clusterName>test2</clusterName>
                         <tcpPort>9600</tcpPort>
                         <httpPort>9500</httpPort>
                         <configPath>${basedir}/../api/src/test/resources/elasticsearch/config</configPath>
                         <outputDirectory>${project.build.directory}/esrch2</outputDirectory>
                     </configuration>
                 </execution>
                 <execution>
                     <id>deploy-json-2</id>
                     <phase>pre-integration-test</phase>
                     <goals>
                         <goal>load</goal>
                     </goals>
                     <configuration>
                         <clusterName>test2</clusterName>
                         <httpPort>9500</httpPort>
                         <scriptFile>src/test/resources/elasticsearch.script</scriptFile>
                     </configuration>
                 </execution>
                 <execution>
                     <id>stop-elasticsearch-2</id>
                     <phase>post-integration-test</phase>
                     <goals>
                         <goal>stop</goal>
                     </goals>
                     <configuration>
                         <clusterName>test2</clusterName>
                         <httpPort>9500</httpPort>
                     </configuration>
                 </execution>
             </executions>
            <executions>
        </plugin>
