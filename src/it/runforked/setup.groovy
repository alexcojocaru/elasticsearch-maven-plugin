import com.github.alexcojocaru.mojo.elasticsearch.SetupUtil

// since I cannot pass the props to the maven process directly,
// save them to the props file, which is loaded by the properties plugin in pom.xml
// http://maven.apache.org/plugins/maven-invoker-plugin/integration-test-mojo.html#testPropertiesFile
def props = SetupUtil.generateProperties()
SetupUtil.saveProperties(basedir, "test.properties", props)

println("Running plugin with properties ${props}")
