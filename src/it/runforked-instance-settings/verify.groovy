import java.io.File

import com.github.alexcojocaru.mojo.elasticsearch.v2.ItVerification

def instanceCount = Integer.parseInt(context.get("es.instanceCount"))
def clusterName = context.get("es.clusterName")
def httpPort = Integer.parseInt(context.get("es.httpPort"))

(0..<instanceCount).each {
    def esBaseDir = new File(new File(basedir, "target"), "elasticsearch" + it)
    def verification = new ItVerification(esBaseDir)

	verification.verifyBaseDirectoryExists()
	verification.verifyInstanceNotRunning(clusterName, httpPort)
}

return true
