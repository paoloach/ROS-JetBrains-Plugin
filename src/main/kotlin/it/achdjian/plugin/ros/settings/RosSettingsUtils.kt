package it.achdjian.plugin.ros.settings

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

    val perms = EnumSet.of<PosixFilePermission>(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ)
    val tempFile = Files.createTempFile("checkSetup", ".bash", PosixFilePermissions.asFileAttribute(perms))
    val checkEnvFile = "source " + rosVersion.toAbsolutePath().toString() + "/setup.bash\nexport"
    Files.write(tempFile, checkEnvFile.toByteArray())

    val process = Runtime.getRuntime().exec(tempFile.toString())

    val scanner = Scanner(process.inputStream)
    val newEnv = HashMap<String, String>()
    while (scanner.hasNextLine()) {
        val line = scanner.nextLine()
        if (isExport(line)) {
            val assignment = line.indexOfFirst { it == '=' }
            if (assignment > 0) {
                var value = line.substring(assignment + 1)
                val key = line.substring(7, assignment)
                if (value[0] == '\"'){
                    value = value.drop(1)
                }
                if (value.last() == '\"'){
                    value = value.dropLast(1)
                }
                newEnv[key] = value
            }
        } else {
            println("to do")
        }
    }

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
