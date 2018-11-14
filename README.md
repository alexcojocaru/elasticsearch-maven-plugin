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

*   **flavour** [defaultValue="oss"]
    > the flavour of Elasticsearch to install (`oss`, `default`); the `default` is not supported currently, due to x-pack issues; applicable only to Elasticsearch 6.3.0+

*   **version** [defaultValue="5.0.0"]
    > the version of Elasticsearch to install

*   **downloadUrl** [defaultValue=""]
    > the Elasticsearch download URL (eg. https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-oss-6.3.0.zip); if provided, it overrides the default download URL (https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-${flavour}-${version}.zip); the flavour and version plugin properties are still required for building the coordinates of the maven artifact to install in the local repository

*   **httpPort** [defaultValue=9200]
    > the port to configure Elasticsearch to listen to HTTP traffic to; when configuring multiple instances, they will be assigned subsequent HTTP ports starting with this value (mind the conflicts with the transport ports)

*   **transportPort** [defaultValue=9300]
    > the port for the Elasticsearch node to node communication; when configuring multiple instances, they will be assigned subsequent transport ports starting with this value (mind the conflicts with the HTTP ports)

*   **pathConf** [defaultValue=""] (note: common to all instances !!!)
    > the absolute path (or relative to the maven project) of the custom directory containing configuration files, to be copied to Elasticsearch instances

*   **pathData** [defaultValue=""] - work in progress (note: per instance !!!); while support for this is being implemented, use `pathConf` to configure this option
    > the custom data directory to configure in Elasticsearch

*   **pathLogs** [defaultValue=""] - work in progress (note: per instance !!!); while support for this is being implemented, use `pathConf` to configure this option
    > the custom logs directory to configure in Elasticsearch

*   **plugins** [defaultValue=""]
    > the list of plugins to install in each Elasticsearch instance before starting it (see the [Plugins](#plugins) section for details)

*   **instanceSettings** [defaultValue=""]
    > the list of settings to apply to corresponding Elasticsearch instances (see the [InstanceSettings](#instanceSettings) section for details)

*   **pathInitScript** [defaultValue=""]
    > the path of the initialization script (see the [Initialization script](#initScript) section for details)

*   **keepExistingData** [defaultValue=false] - work in progress
    > whether to keep the data and log directories if they already exist

*   **timeout** [defaultValue=30]
    > how long to wait (in seconds) for each Elasticsearch instance to start up

*   **clientSocketTimeout** [defaultValue=5000]
    > the default socket timeout (in milliseconds) for requests sent to the Elasticsearch server

*   **setAwait** [defaultValue=false]
    > whether to block the execution once all Elasticsearch instances have started, so that the maven build will not proceed to the next step; use CTRL+C to abort the process

*   **autoCreateIndex** [defaultValue=true]
    > configuration of automatic index creation represented by _action.auto\_create\_index_ setting

*   **logLevel** [defaultValue=INFO]
    > the log level to be used by the console logger; the valid values are defined in AbstractElasticsearchBaseMojo.getMavenLogLevel() and they are: DEBUG, INFO, WARN, ERROR, FATAL, DISABLED.


To use the plugin, include the following in your _pom.xml_ file and modify the configuration as needed:

```xml
<plugin>
    <groupId>com.github.alexcojocaru</groupId>
    <artifactId>elasticsearch-maven-plugin</artifactId>
    <!-- REPLACE THE FOLLOWING WITH THE PLUGIN VERSION YOU NEED -->
    <version>6.0</version>
    <configuration>
        <!-- REPLACE THE FOLLOWING WITH THE ELASTICSEARCH VERSION YOU NEED -->
        <version>6.0.0</version>
        <clusterName>test</clusterName>
        <transportPort>9300</transportPort>
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
```


## <a name="plugins"></a>Plugins
A list of Elasticsearch plugins can be provided to the elasticsearch-maven-plugin.
They will be installed into [each Elasticsearch instance](https://www.elastic.co/guide/en/elasticsearch/plugins/5.2/intro.html)
inside the [plugins directory](https://www.elastic.co/guide/en/elasticsearch/reference/5.2/zip-targz.html#zip-targz-layout)
using the [--batch option](https://www.elastic.co/guide/en/elasticsearch/plugins/5.2/_other_command_line_parameters.html#_batch_mode),
before the instance gets started.

The way to enable plugins is as follows:

```xml
<plugin>
    <groupId>com.github.alexcojocaru</groupId>
    <artifactId>elasticsearch-maven-plugin</artifactId>
    <version>6.0</version>
    <configuration>
        <clusterName>test</clusterName>
        <transportPort>9300</transportPort>
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
```

The plugin tag takes 2 parameters:

*   **uri**
    > the [name](https://www.elastic.co/guide/en/elasticsearch/plugins/5.2/installation.html),
    [url](https://www.elastic.co/guide/en/elasticsearch/plugins/5.2/plugin-management-custom-url.html)
    or [file location](https://www.elastic.co/guide/en/elasticsearch/plugins/5.2/plugin-management-custom-url.html)
    of the plugin

*   **esJavaOpts** [defaultValue=""]
    > [additional Elasticsearch Java options](https://www.elastic.co/guide/en/elasticsearch/plugins/5.2/plugin-management-custom-url.html)
    to be passed to the plugin installation tool when installing the plugin

## <a name="instanceSettings"></a>Instance settings

Instance settings are applied to each corresponding elasticsearch instance (via [-E on the commandline](https://www.elastic.co/guide/en/elasticsearch/reference/6.1/windows.html#msi-installer-command-line-configuration))
during startup. If the list is smaller then `instanceCount` no extra settings
are applied to the remaining instances. If it's larger, the extra items are ignored.

Example:
```xml
<plugin>
    <groupId>com.github.alexcojocaru</groupId>
    <artifactId>elasticsearch-maven-plugin</artifactId>
    <version>6.1</version>
    <configuration>
        ...
        <instanceCount>2</clusterName>
        <instanceSettings>
            <properties>
                <node.name>First</node.name>
                <node.attr.data_type>ingest</node.attr.data_type>
            </properties>
            <properties>
                <node.name>Second</node.name>
                <node.attr.data_type>search</node.attr.data_type>
            </properties>
        </instanceSettings>
        ...
    </configuration>
    <executions>
        ...
    </executions>
</plugin>
```

## <a name="initScript"></a>Initialization script
An initialization script file can be provided using the **pathInitScript** parameter of the plugin, in which case it will be executed against the local Elasticsearch cluster.
The file extension defines the file format: *json* for JSON format, anything else for custom format.

#### JSON format

The provided JSON file should contain a list of requests to be sent, one by one, to the Elasticsearch cluster.
Each request definition has three properties:

* the request **method**: one of *PUT*, *POST*, *DELETE*
    > the name (in uppercase) of the request method to be used for the current request

* the **path** part of the URL (should not start with slash)
    > will be appended to the protocol, hostname and port parts when the full URL is constructed

* the **payload**
    > it should not be defined for *DELETE* requests; some Elasticsearch requests do not require a payload (eg. POST *index/_refresh*), in which case define the payload as `{}`


**Example** (see the *src/main/test/resources/init.json* file for a more complete example):

To send a *POST* request to *http://localhost:9200/test_index/test_type/_mapping*,
followed by a *DELETE* request to *http://localhost:9200/test_index/test_type/1*:

```json
[
    {
        "method": "POST",
        "path": "test_index/test_type/_mapping",
        "payload": {
            "test_type": {
                "properties": {
                    "name": {
                        "type": "keyword"
                    },
                    "lastModified": {
                        "type": "date"
                    }
                }
            }
        }
    },
    {
        "method": "DELETE",
        "path": "test_index/test_type/1"
    }
]
```

#### Custom format

Each line defines a request to be sent to the Elasticsearch cluster, and it has three parts separated by colon:

* the request method: one of *PUT*, *POST*, *DELETE*
    > the name (in uppercase) of the request method to be used for the current request

* the path part of the URL (should not start with slash)
    > will be appended to the protocol, hostname and port parts when the full URL is constructed

* the JSON to send to Elasticsearch as payload
    > it should be empty for *DELETE* requests

Note: Empty lines are ignored, as well as lines starting with the '#' sign.


**Examples** (see the *src/it/runforked-with-init-script/init.script* file for a more complete example):

* To send a *POST* request to *http://localhost:9200/test_index/test_type/_mapping*:
    > `POST:test_index/test_type/_mapping:{ "test_type" : { "properties" : { "name" : { "type" : "keyword" }, "lastModified" : { "type" : "date" } } } }`

* To send a *DELETE* request to *http://localhost:9200/test_index/test_type/1* without content; note the colon at the end, for there is no JSON data in case of a DELETE.
    > `DELETE:test_index/test_type/1:`

## FAQ

#### Error: Could not find or load main class (on OSX 10.13.6)
There seems to be an issue when starting certain versions of Elasticsearch (eg. 5.6.8) on OSX 10.13.6,
directly or via the plugin. The issue is caused by the incorrect quoting of the *-cp* argument
on the Java command built by the `bin/elasticsearch` script inside the Elasticsearch package.
A workaround is described [here](https://github.com/alexcojocaru/elasticsearch-maven-plugin/issues/69).
In summary, set the *ES_JVM_OPTIONS* environment variable to `-cp "./target/elasticsearch0/lib/*"`
in the IDE's run configuration or on the shell environment where maven/Elasticsearch is executed.


#### Node is killed when running in TravisCI
When running your build job in [TravisCI](https://travis-ci.org/), it can happen that your node is being killed without any notice.
To fix that you may have to modify the `.travis.yml` file as follows:

```yml
sudo: true
before_script:
  - sudo sysctl -w vm.max_map_count=262144
```

#### Avoid downloading a plugin from the Internet repeatedly
When you want to run integration tests with a given plugin (eg. reindex-client),
elasticsearch-maven-plugin will run behind the scene a command like
`bin/elasticsearch-plugin install reindex-client` which will download the plugin
from the Internet at every execution.

You can use some Maven magic to avoid the download by first using
`maven-dependency-plugin` to download the plugin as an artifact which will be
stored in your local `.m2` directory, then copy from there to your project
target directory, eg.
```
mvn org.apache.maven.plugins:maven-dependency-plugin:2.1:get \
    -DrepoUrl=https://repo1.maven.org/maven2 \
    -Dartifact=org.elasticsearch.plugin:reindex-client:5.4.2
```

Then just tell the elasticsearch-maven-plugin to use the local URI.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>3.0.0</version>
    <executions>
        <execution>
            <id>integ-setup-dependencies-plugins</id>
            <phase>pre-integration-test</phase>
            <goals>
                <goal>copy</goal>
            </goals>
            <configuration>
                <artifactItems>
                    <artifactItem>
                        <groupId>org.elasticsearch.plugin</groupId>
                        <artifactId>reindex-client</artifactId>
                        <version>5.4.2</version>
                        <type>zip</type>
                    </artifactItem>
                </artifactItems>
                <useBaseVersion>true</useBaseVersion>
                <outputDirectory>${project.build.directory}/integration-tests/plugins</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
<plugin>
    <groupId>com.github.alexcojocaru</groupId>
    <artifactId>elasticsearch-maven-plugin</artifactId>
    <version>5.7</version>
    <configuration>
        <version>5.4.2</version>
        <plugins>
            <plugin>
                <uri>file://${project.build.directory}/integration-tests/plugins/reindex-client-5.4.2.zip</uri>
            </plugin>
        </plugins>
    </configuration>
</plugin>
```

## Integration Tests
The integration tests exist as maven plugins in the src/it directory and are executed via the maven invoker plugin
(see the pom.xml for details on its configuration).

Each test has a maven like structure, with a [pom.xml](#it/pom.xml)
and a [src/test/java](#it/java) directory containing the test sources.

The properties common between all tests are defined in the invoker plugin config under the "testProperties" tag.

During the integration test phase, the invoker plugin copies the tests to target/it and executes
the "clean" and "verify" goals on each them. They are executed in a separate process
with a brand new maven config, defined using the following two invoker plugin properties:
localRepositoryPath (set to target/local-repo) and settingsFile (set to src/it/settings.xml).
The invoker configuration also defines a [pre-build hook script](#it/setup.groovy)
and a [post-build hook script](#it/verify.groovy)
to run before and after executing the test. These are groovy scripts which each test directory must contain.

#### The structure of an integration test
Because the integration tests are executed as maven projects, they have a maven-like file structure.

###### <a name="it/pom.xml"></a>pom.xml
The pom.xml is generic and does not contain anything specific to any test - it defines the test project
dependencies and which goals to execute on the elasticsearch maven plugin.

###### <a name="it/setup.groovy"></a>setup.groovy
The pre-build hook script (setup.groovy) does the plugin and context configuration,
by using the ItSetup util to create a map of plugin properties and to save them to the
test.properties file in the test directory (to be picked up by the Java tests via the methods in ItBase).
The properties are also set on the context, for some are needed by the [post-build hook script](#it/verify.groovy).
Defining the number of ES instances is required in the groovy script.
The ES cluster name and the HTTP and transport protocol ports
are randomly generated by ItSetup.generateProperties to avoid clashes between integration tests.
Any other properties to be passed over to the plugin can be added to the props map
(see src/it/runforked-auto-create-index-false/setup.groovy for an example).

###### <a name="it/verify.groovy"></a>verify.groovy
The standard verification done here is that the ES base directory(ies) were created
and that the ES instance(s) are not running (via the ItVerification util).
This file uses some of the plugin properties set on the context by the [pre-build hook script](#it/setup.groovy).

###### <a name="it/java"></a>Java test file(s)
The actual tests are defined in java files in the src/test/java directory under each integration test directory
(eg. src/it/runforked-auto-create-index-false/src/test/java/com/github/alexcojocaru/mojo/elasticsearch/v2/AutoCreateIndexTest.java).
They will be compiled and executed during the maven invoker plugin execution of the integration test maven project.
All java tests should extend com.github.alexcojocaru.mojo.elasticsearch.v2.ItBase
to get the clusterName and httpPort read from the context (ie. the "test.properties" file created by
the [pre-build hook script](#it/setup.groovy)) and the ES client set up.

*NOTE: It is not possible to execute such a test case in an IDE, due to the lack of context
(the test properties must be set in the props file by executing the groovy script,
the elasticsearch maven plugin must be running, etc).*


#### How to write new tests
Copy one of the existing integration tests and modify as needed. It will be picked up
by the invoker plugin due to the wildcard definition in the plugin config in pom.xml.

#### How to run single integration test
Set the `integrationTest` env variable to the integration test name when running maven, eg:
```
$ mvn clean verify -DintegrationTest=runforked-defaults-es6
```

#### How to debug an integration test
There are two ways to obtain more information during the execution of an integration test:

###### Debugging the maven execution
To have the invoker plugin output detailed information about the integration test execution,
change the `debug` attribute of the invoker plugin configuration (in the pom.xml) to `true`.

###### Debugging the elasticsearch maven plugin execution during the integration test
Set the es.logLevel property of the plugin to `DEBUG`, by adding
```java
props.put("es.logLevel", "DEBUG");
```
to the [setup.groovy](#it/setup.groovy) file for the integration test you want to debug.
