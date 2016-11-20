import java.io.File

import com.github.alexcojocaru.mojo.elasticsearch.v2.ItVerification

// there is a single instance in this test

def esBaseDir = new File(new File(basedir, "target"), "elasticsearch0")

ItVerification.verifyBaseDirectory(esBaseDir)
ItVerification.verifyInstanceIsNotRunning(esBaseDir)

return true