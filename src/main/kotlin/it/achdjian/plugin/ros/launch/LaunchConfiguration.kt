package it.achdjian.plugin.ros.launch

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import org.jdom.Element

class LaunchConfiguration(project: Project, configurationFactory: ConfigurationFactory, targetName: String) :
        LocatableConfigurationBase<RunProfileState>(project, configurationFactory, targetName), RunConfigurationWithSuppressedDefaultDebugAction {
    override fun getConfigurationEditor() = LaunchEditor(project)

    companion object {
        const val PATH_TAG = "path"
    }

    var path: VirtualFile? = null


    override fun getState(executor: Executor, environment: ExecutionEnvironment) = LaunchLauncher(this, environment)

    @Throws(InvalidDataException::class)
    override fun readExternal(parentElement: Element) {
        super.readExternal(parentElement)
        val xmlPath = parentElement.getAttributeValue(PATH_TAG)
        xmlPath?.let {
            path = VirtualFileManager.getInstance().findFileByUrl(it)
        }

    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(parentElement: Element) {
        super.writeExternal(parentElement)
        path?.let {
            parentElement.setAttribute(PATH_TAG, it.url)
        }
    }
}