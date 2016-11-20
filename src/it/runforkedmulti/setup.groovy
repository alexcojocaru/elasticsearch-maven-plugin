import com.github.alexcojocaru.mojo.elasticsearch.v2.ItSetup

// since I cannot pass the props to the maven process directly,
// save them to the props file, which is loaded by the properties plugin in pom.xml
// http://maven.apache.org/plugins/maven-invoker-plugin/integration-test-mojo.html#testPropertiesFile
def props = ItSetup.generateProperties(2)
ItSetup.saveProperties(basedir, "test.properties", props)

println("Running plugin with properties ${props}")
