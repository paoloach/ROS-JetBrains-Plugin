package it.achdjian.plugin.ros.settings

import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

data class InitWorkspaceCmd(val executableFile: File, val args: String?) {
    override fun toString() = executableFile.toString() + " " + args
}


