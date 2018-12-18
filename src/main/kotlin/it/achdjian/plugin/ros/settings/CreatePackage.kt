package it.achdjian.plugin.ros.settings

import com.intellij.openapi.vfs.VirtualFile
import it.achdjian.plugin.ros.data.RosVersion
import java.io.File

fun createPackageFactory(rosVersion: RosVersion): CreatePackage {
    rosVersion.envPath.forEach {
        if (File(it, "catkin_create_pkg").exists()) {
            return CatkinCreatePackage(it, rosVersion)
        }
    }
    return NullCreatePackage()
}

interface CreatePackage {
    fun createPackage(path: VirtualFile, name: String, dependencies: List<String>)
}

class NullCreatePackage : CreatePackage {
    override fun createPackage(path: VirtualFile, name: String, dependencies: List<String>) {

    }

}

class CatkinCreatePackage(path:String, val rosVersion: RosVersion) : CreatePackage {
    private val executable = File(path, "catkin_create_pkg")

    override fun createPackage(path: VirtualFile, name: String, dependencies: List<String>) {

        var cmd = executable.absolutePath + " " + name
        dependencies.forEach { cmd += " $it" }
        val envList = rosVersion.env.map { it.key + "=" + it.value }.toTypedArray()
        val workspace = File(path.path, "src")

        val process = Runtime.getRuntime().exec(cmd, envList, workspace)
        process.waitFor()
    }
}
