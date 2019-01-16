package it.achdjian.plugin.ros.node

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration
import com.jetbrains.cidr.cpp.execution.CMakeRunConfigurationType
import com.jetbrains.cidr.execution.BuildTargetAndConfigurationData
import com.jetbrains.cidr.execution.CidrCommandLineState
import com.jetbrains.cidr.execution.CidrExecutableDataHolder
import it.achdjian.plugin.ros.utils.RosCommonProgramRunConfigurationParameters
import org.jdom.Element

class NodeConfigurationCMake(project: Project, configurationFactory: ConfigurationFactory, targetName: String) :
        CMakeAppRunConfiguration(project, configurationFactory, targetName) , CidrExecutableDataHolder {


    companion object {
        const val PACKAGE_TAG = "package"
        const val NODE_TAG = "node"
        const val ROS_MASTER_ADDR_TAG = "ros_master_addr"
        const val ROS_MASTER_PORT_TAG = "ros_master_port"
    }


    var pack: String? = null
    var node: String? = null
    var rosMasterAddr = "localhost"
    var rosMasterPort = 11311

    val programConfParamenters = RosCommonProgramRunConfigurationParameters(project)

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = NodeRunEditorCMake(project, CMakeRunConfigurationType.getHelper(project))

    override fun getState(executor: Executor, environment: ExecutionEnvironment): CidrCommandLineState? {
        return  CidrCommandLineState(environment, NodeLauncherCMake(this, project,environment))
    }

    override fun getTargetAndConfigurationData(): BuildTargetAndConfigurationData? {
        val targetAndConfigurationData = super.getTargetAndConfigurationData()

        return targetAndConfigurationData
    }


    @Throws(InvalidDataException::class)
    override fun readExternal(parentElement: Element) {
        super.readExternal(parentElement)
        parentElement.getAttributeValue(PACKAGE_TAG)?.let { pack = it }
        parentElement.getAttributeValue(NODE_TAG)?.let { node = it }
        parentElement.getAttributeValue(ROS_MASTER_ADDR_TAG)?.let { rosMasterAddr = it }
        parentElement.getAttributeValue(ROS_MASTER_PORT_TAG)?.let { rosMasterPort = it.toInt() }
        programConfParamenters.readExternal(parentElement)
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(parentElement: Element) {
        super.writeExternal(parentElement)
        pack?.let { parentElement.setAttribute(PACKAGE_TAG, it) }
        node?.let { parentElement.setAttribute(NODE_TAG, it) }
        parentElement.setAttribute(ROS_MASTER_ADDR_TAG, rosMasterAddr)
        parentElement.setAttribute(ROS_MASTER_PORT_TAG, rosMasterPort.toString())
        programConfParamenters.writeExternal(parentElement)
    }



}