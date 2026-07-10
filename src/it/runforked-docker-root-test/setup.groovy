import com.github.alexcojocaru.mojo.elasticsearch.v2.ItSetup

def instanceCount = 1

// since I cannot pass the props to the maven process directly, save them to the props file;
// the file is then loaded by the invoker plugin and all props defined in it are set as system props

def setup = new ItSetup(basedir)
def props = setup.generateProperties(instanceCount)

// Enable autoHandleRootUser - this is critical for the Docker root test
props.put("es.autoHandleRootUser", "true")

// Use Elasticsearch 9.1.3 as specified
props.put("es.version", "9.1.3")

// This test verifies that when running as root in Docker with autoHandleRootUser enabled,
// the plugin automatically creates esuser and runs ES as that user

setup.saveProperties("test.properties", props)
context.putAll(props)

println("=" * 80)
println("Docker Root Test - Running as: ${System.getProperty('user.name')}")
println("Elasticsearch version: 9.1.3")
println("autoHandleRootUser: true")
println("Properties: ${props}")
println("=" * 80)
