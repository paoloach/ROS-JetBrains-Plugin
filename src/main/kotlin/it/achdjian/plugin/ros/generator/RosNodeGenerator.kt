package it.achdjian.plugin.ros.generator

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.cidr.cpp.cmake.CMakeSettings
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.CMakeAbstractCPPProjectGenerator
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.CMakeProjectSettings
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace
import it.achdjian.plagin.ros.ui.panel
import it.achdjian.plugin.ros.RosEnvironments
import it.achdjian.plugin.ros.settings.RosVersion
import it.achdjian.plugin.ros.ui.PackagesPanel
import it.achdjian.plugin.ros.utils.createMainCMakeLists
import it.achdjian.plugin.ros.utils.releaseProfile
import java.io.File
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel


class RosNodeGenerator : CMakeAbstractCPPProjectGenerator() {

    private var version: RosVersion? = null
    private lateinit var packagesPanel: PackagesPanel

    override fun getName(): String = "ROS workspace"

    override fun getSettingsPanel(): JComponent {
        val state = ApplicationManager.getApplication().getComponent(RosEnvironments::class.java, RosEnvironments())
        val versionsName = state.versions.map { it.name }

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        packagesPanel = PackagesPanel()

        val optionPanel = panel("ROS version") {
            row("ROS version") {
                comboBox(versionsName) {
                    version = state.versions.find { version -> version.name == it.item.toString() }
                    version?.let {
                        it.searchPackages()
                        packagesPanel.setPackages(it.packages)
                    }
                }
            }
        }

        panel.add(optionPanel)
        panel.add(packagesPanel)

        return panel
    }

    override fun createSourceFiles(projectName: String, path: VirtualFile): Array<VirtualFile> {
        path.createChildDirectory(this, "src")
        version?.initWorkspace(path)
        version?.createPackage(path, projectName, packagesPanel.selected())
        return arrayOf()
    }

    override fun getCMakeFileContent(p0: String) = createMainCMakeLists()

    override fun generateProject(project: Project, path: VirtualFile, cmakeSetting: CMakeProjectSettings, module: Module) {
        super.generateProject(project, path, cmakeSetting, module)
        val cMakeWorkspace = CMakeWorkspace.getInstance(project)
        version?.let {
            val settings = cMakeWorkspace.settings
            var releaseProfile = releaseProfile(it, File(path.path))

            settings.profiles = listOf(releaseProfile)
        }
    }

}