package it.achdjian.plugin.ros.generator

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.CMakeAbstractCPPProjectGenerator
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.CMakeProjectSettings
import it.achdjian.plagin.ros.ui.panel
import it.achdjian.plugin.ros.RosEnvironments
import it.achdjian.plugin.ros.settings.RosVersion
import javax.swing.JComponent

class RosNodeGenerator : CMakeAbstractCPPProjectGenerator() {

    private var version:RosVersion?=null

    override fun getName(): String = "ROS workspace2"

    override fun getSettingsPanel(): JComponent {
        val state = ApplicationManager.getApplication().getComponent(RosEnvironments::class.java, RosEnvironments())
        val versionsName = state.versions.map { it.name }
        val p = panel("ROS versions") {
            row("ROS version") {
                comboBox(versionsName){
                    version = state.versions.find { version->version.name == it.item.toString()  }
                }
            }
        }

        return p
    }

    override fun createSourceFiles(projectName: String, path: VirtualFile): Array<VirtualFile> {
        path.createChildDirectory(this,"src")
        version?.initWorkspace(path)
        return arrayOf()
    }

    override fun generateProject(project: Project, path: VirtualFile, cmakeSetting: CMakeProjectSettings, module: Module) {
        path.createChildDirectory(this,"src")
        version?.initWorkspace(path)
    }

    override fun getCMakeFileContent(p0: String): String {
       return "CMake version 12.3"
    }

}