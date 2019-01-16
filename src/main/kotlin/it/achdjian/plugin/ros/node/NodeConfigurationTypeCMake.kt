package it.achdjian.plugin.ros.node

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfigurationSingletonPolicy
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper
import com.jetbrains.cidr.cpp.execution.CMakeRunConfigurationType
import it.achdjian.plugin.ros.ui.ICON_NODE


object IDs2 {
    val ID = "ROS.node"
    val FACTORY = "ROS.node.factory"
    val DISPLAY_NAME = "ROS node"
    val DESCRIPTION = "Run ROS node"
}


class NodeConfigurationFactoryCMake(private val configurationType: NodeConfigurationTypeCMake) : ConfigurationFactory(configurationType) {
    override fun createTemplateConfiguration(project: Project) = NodeConfigurationCMake(project, configurationType.confFactory, "ROS")
    override fun getSingletonPolicy() = RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY
    override fun getId() = IDs2.FACTORY
}

class NodeConfigurationTypeCMake :  CMakeRunConfigurationType(IDs2.ID, IDs2.DISPLAY_NAME, IDs2.DESCRIPTION,IDs2.DESCRIPTION, ICON_NODE) {
    override fun createEditor(project: Project): SettingsEditor<out CMakeAppRunConfiguration> {
        return NodeRunEditorCMake(project, CMakeBuildConfigurationHelper(project))
    }

    override fun createRunConfiguration(project: Project, configurationFactory: ConfigurationFactory): CMakeAppRunConfiguration {
       return NodeConfigurationCMake(project,confFactory, "ROS")
    }


    var confFactory = NodeConfigurationFactoryCMake(this)

}