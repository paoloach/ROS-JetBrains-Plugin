package it.achdjian.plugin.ros.node

import com.intellij.execution.configurations.*
import com.intellij.openapi.project.Project
import it.achdjian.plugin.ros.ui.ICON_NODE


object IDs {
    val ID = "ROS.node"
    val FACTORY = "ROS.node.factory"
    val DISPLAY_NAME = "ROS node"
    val DESCRIPTION = "Run ROS node"
}


class NodeConfigurationFactory(private val configurationType: NodeConfigurationType) : ConfigurationFactory(configurationType) {
    override fun createTemplateConfiguration(project: Project) = NodeConfiguration(project, configurationType.confFactory, "")
    override fun getSingletonPolicy() = RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY
    override fun getId() = IDs.FACTORY
}

class NodeConfigurationType : ConfigurationTypeBase(IDs.ID, IDs.DISPLAY_NAME, IDs.DESCRIPTION, ICON_NODE) {
    companion object {
        fun getInstance(): NodeConfigurationType {
            return ConfigurationTypeUtil.findConfigurationType<NodeConfigurationType>(NodeConfigurationType::class.java!!)
        }
    }

    var confFactory = NodeConfigurationFactory(this)

    init {
        addFactory(object : ConfigurationFactory(this) {
            override fun createTemplateConfiguration(project: Project): RunConfiguration =
                    NodeConfiguration(project, this, IDs.FACTORY)

        })
    }


}