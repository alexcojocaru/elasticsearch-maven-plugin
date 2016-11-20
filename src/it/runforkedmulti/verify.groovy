import java.io.File

import com.github.alexcojocaru.mojo.elasticsearch.v2.ItVerification

// there are two instances in this test

(0..1).each {
    def esBaseDir = new File(new File(basedir, "target"), "elasticsearch" + it)

	ItVerification.verifyBaseDirectory(esBaseDir)
	ItVerification.verifyInstanceIsNotRunning(esBaseDir)
}

return true
