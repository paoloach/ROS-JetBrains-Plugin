package it.achdjian.plugin.ros.importer

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.projectImport.ProjectOpenProcessor
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.Icon

class RosWorkspaceOpenProcessor : ProjectOpenProcessor() {
    override fun getIcon(): Icon = IconLoader.getIcon("/icons/ros.svg")

    override fun canOpenProject(file: VirtualFile): Boolean {
        val src = file.findChild("src")
        src?.let {
            val cmakeLists = it.findChild("CMakeLists.txt")
            cmakeLists?.let {file->
                if (file.exists() && Files.isSymbolicLink(Paths.get(file.path))){
                    return true
                }
            } ?: return false
        }
        return false
    }

    override fun getName() = "Ros workspace"

    override fun doOpenProject(virtualFile: VirtualFile, projectToClose: Project?, forceOpenInNewFrame: Boolean): Project? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}