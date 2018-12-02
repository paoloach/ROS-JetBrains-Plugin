package it.achdjian.plugin.ros.settings

import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.nio.file.Path

data class InitWorkspaceCmd(val executableFile:File, val args: String?){
    override fun toString() = executableFile.toString() + " " + args
}

data class RosVersion(val name: String, val path: Path, val env: Map<String,String>, val initWorkspaceCmd: InitWorkspaceCmd?) {
    fun initWorkspace(path:VirtualFile){
        initWorkspaceCmd?.let {
            val cmd = it.executableFile.absolutePath.toString() + " " + it.args
            val envList = env.map {it.key + "="+it.value}.toTypedArray()


            val process = Runtime.getRuntime().exec(cmd, envList, File(path.path))
            process.waitFor()
        }

    }
}
