package it.achdjian.plugin.ros.settings

import it.achdjian.plugin.ros.utils.getEnvironment
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermissions
import java.util.*
import kotlin.collections.HashMap

fun findInitCmd(rosVersion: Path):InitWorkspaceCmd?{
    if (File(rosVersion.toFile(), "/bin/catkin_init_workspace").exists()){
        return InitWorkspaceCmd(File(rosVersion.toFile(), "/bin/catkin_init_workspace"), "")
    }
    return null
}

fun diffEnvironment(rosVersion: Path): Map<String, String> {

    val actualEnv = System.getenv()
    val newEnv = getEnvironment(rosVersion.toAbsolutePath().toString())
    val env  = HashMap(diff(newEnv, actualEnv))
    if (!env.containsKey("ROS_PACKAGE_PATH"))
        env["ROS_PACKAGE_PATH"] = actualEnv["ROS_PACKAGE_PATH"]
    return env
}

fun diff(newEnv: Map<String, String>, actualEnv: Map<String, String>) =
        newEnv.filter { (key, value) ->
            !actualEnv.containsKey(key) || !actualEnv[key].equals(value)
        }

fun isExport(line: String): Boolean = line.startsWith("export")
