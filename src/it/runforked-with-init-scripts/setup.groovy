import com.github.alexcojocaru.mojo.elasticsearch.v2.ItSetup

def instanceCount = 1

// since I cannot pass the props to the maven process directly, save them to the props file;
// the file is then loaded by the invoker plugin and all props defined in it are set as system props

def setup = new ItSetup(basedir)
def props = setup.generateProperties(instanceCount)
props.put("es.pathInitScript", "init.script,otherInit.script");
setup.saveProperties("test.properties", props)
context.putAll(props);

println("Running plugin with properties ${props}")
