package it.achdjian.plugin.ros.launch

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationSingletonPolicy
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.NotNullLazyValue
import javax.swing.Icon

object IDs {
    val ID = "ROS.launch"
    val FACTORY = "ROS.launch.factory"
    val DISPLAY_NAME = "ROS launch"
    val DESCRIPTION = "Run ROS launch file"
}


object ICON : NotNullLazyValue<Icon>() {
    override fun compute() = IconLoader.findIcon("/icons/ros.svg") ?: AllIcons.Icon
}


class LaunchConfigurationFactory(val configurationType: LaunchConfigurationType) : ConfigurationFactory(configurationType) {
    override fun createTemplateConfiguration(project: Project) = LaunchConfiguration(project, configurationType.confFactory, "")
    override fun getSingletonPolicy() = RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY
    override fun getId() = IDs.FACTORY

}

class LaunchConfigurationType : ConfigurationTypeBase(IDs.ID,  IDs.DISPLAY_NAME, IDs.DESCRIPTION, ICON) {
    var confFactory = LaunchConfigurationFactory(this)


    init {
        addFactory(object : ConfigurationFactory(this) {
            override fun createTemplateConfiguration(project: Project): RunConfiguration =
                    LaunchConfiguration(project, this, IDs.FACTORY)

        })
    }

}