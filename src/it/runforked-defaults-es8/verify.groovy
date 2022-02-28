import java.io.File

import com.github.alexcojocaru.mojo.elasticsearch.v2.ItVerification

def instanceCount = Integer.parseInt(context.get("es.instanceCount"))
def clusterName = context.get("es.clusterName")
def httpPort = Integer.parseInt(context.get("es.httpPort"))
def bootstrapPassword = context.get("es.bootstrapPassword")

(0..<instanceCount).each {
    def esBaseDir = new File(new File(basedir, "target"), "elasticsearch" + it)
    def verification = new ItVerification(esBaseDir)

	verification.verifyBaseDirectoryExists()
	verification.verifyInstanceNotRunning(clusterName, httpPort, bootstrapPassword)
}

return true
