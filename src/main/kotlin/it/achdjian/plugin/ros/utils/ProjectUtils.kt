package it.achdjian.plugin.ros.utils

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermissions
import java.util.*

object CONST {
    val perms: EnumSet<PosixFilePermission> = EnumSet.of<PosixFilePermission>(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ)
}

fun getEnvironmentVariables(project: Project, env: Map<String, String>): Map<String, String> {
    val newEnv = HashMap<String, String>()
    project.projectFile
            ?.parent
            ?.parent
            ?.parent
            ?.findChild("devel")
            ?.findChild("setup.bash")
            ?.let {
                return getEnvironment(it.path, env)
            }
    return newEnv
}


fun getEnvironment(setupFile: String, env: Map<String, String> = emptyMap()): Map<String, String> {
    val log = Logger.getInstance("#it.achdjian.plugin.ros.utils.getEnvironment")
    log.trace("getEnvironment($setupFile)")
    val checkEnvFile = "source $setupFile\nset"
    val tempFile = Files.createTempFile("checkSetup", ".bash", PosixFilePermissions.asFileAttribute(CONST.perms))
    Files.write(tempFile, checkEnvFile.toByteArray())
    val e = env.entries.map { v -> "${v.key}=${v.value}" }.toList().toTypedArray()
    val process = Runtime.getRuntime().exec(tempFile.toString(), e)

    val scanner = Scanner(process.inputStream)
    log.info(tempFile.toString())
    val newEnv = HashMap<String, String>()
    while (scanner.hasNextLine()) {
        val line = scanner.nextLine()
          val assignment = line.indexOfFirst { it == '=' }
        if (assignment > 0) {
            var value = line.substring(assignment + 1)
            val key = line.substring(0, assignment)
            if (value[0] == '\"') {
                value = value.drop(1)
            }
            if (value.last() == '\"') {
                value = value.dropLast(1)
            }
            newEnv[key] = value
            log.trace("$key=$value")
        } else {
            log.trace("Not parsed: $line")
        }
    }
    return newEnv
}
