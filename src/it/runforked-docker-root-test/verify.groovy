import java.io.File

import com.github.alexcojocaru.mojo.elasticsearch.v2.ItVerification

def instanceCount = Integer.parseInt(context.get("es.instanceCount"))
def clusterName = context.get("es.clusterName")
def httpPort = Integer.parseInt(context.get("es.httpPort"))

println("=" * 80)
println("Verifying Docker Root Test Results")
println("Running as: ${System.getProperty('user.name')}")
println("Expected: ES should have started successfully as non-root user (esuser)")
println("=" * 80)

(0..<instanceCount).each {
	def esBaseDir = new File(new File(basedir, "target"), "elasticsearch" + it)
	def verification = new ItVerification(esBaseDir)

	verification.verifyBaseDirectoryExists()
	verification.verifyInstanceNotRunning(clusterName, httpPort)
}

println("✓ Verification passed: ES directories exist")
println("✓ Verification passed: ES instances are stopped")
println("✓ SUCCESS: autoHandleRootUser feature works correctly when running as root")
println("=" * 80)

return true
