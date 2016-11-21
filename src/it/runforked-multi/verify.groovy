import java.io.File

import com.github.alexcojocaru.mojo.elasticsearch.v2.ItVerification

def instanceCount = Integer.parseInt(context.get("instanceCount"))

(0..<instanceCount).each {
    def esBaseDir = new File(new File(basedir, "target"), "elasticsearch" + it)

	ItVerification.verifyBaseDirectory(esBaseDir)
	ItVerification.verifyInstanceIsNotRunning(esBaseDir)
}

return true
