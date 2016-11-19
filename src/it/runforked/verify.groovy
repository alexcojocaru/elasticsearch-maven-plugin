import java.io.File

import com.github.alexcojocaru.mojo.elasticsearch.VerificationUtil

// there is a single instance in this test

def esBaseDir = new File(new File(basedir, "target"), "elasticsearch0")

VerificationUtil.verifyBaseDirectory(esBaseDir)
VerificationUtil.verifyInstanceIsNotRunning(esBaseDir)