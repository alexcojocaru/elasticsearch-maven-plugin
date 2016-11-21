import com.github.alexcojocaru.mojo.elasticsearch.v2.ItSetup

def instanceCount = 1

// since I cannot pass the props to the maven process directly, save them to the props file;
// the file is then loaded by the invoker plugin and all props defined in it are set as system props
def props = ItSetup.generateProperties(instanceCount)
props.put("es.pathInitScript", "init.script");
ItSetup.saveProperties(basedir, "test.properties", props)
context.put("instanceCount", String.valueOf(instanceCount));

println("Running plugin with properties ${props}")
