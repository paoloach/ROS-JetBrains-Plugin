package it.achdjian.plugin.ros.launch

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.application.ApplicationManager
import it.achdjian.plugin.ros.data.RosEnvironments
import it.achdjian.plugin.ros.utils.getEnvironmentVariables

class LaunchLauncher(val launchConfiguration: LaunchConfiguration, environment: ExecutionEnvironment) : CommandLineState(environment) {
    private val rosEnvironments = ApplicationManager.getApplication().getComponent(RosEnvironments::class.java, RosEnvironments())

    override fun startProcess(): ProcessHandler {
        return launchConfiguration.path?.let {launchFile->
            val rosVersion = rosEnvironments.getVersion(environment.project)
            rosVersion?.let {rosVersion->
                val environmentVariables = getEnvironmentVariables(environment.project, rosVersion.env)
                val cmdLine = GeneralCommandLine(rosVersion.rosLaunch)
                if (launchConfiguration.verbose){
                    cmdLine.addParameter("-v")
                }
                if (launchConfiguration.wait){
                    cmdLine.addParameter("--wait")
                }
                if (launchConfiguration.screen){
                    cmdLine.addParameter("--screen")
                }
                if (launchConfiguration.log){
                    cmdLine.addParameter("--log")
                }
                cmdLine.addParameter("--master-logger-level=${launchConfiguration.logLevel}")
                cmdLine.addParameter(launchFile.path)
                cmdLine.setWorkDirectory("/home/paolo/ros/irobot")
                cmdLine.withEnvironment(environmentVariables)
                cmdLine.withEnvironment("PYTHONUNBUFFERED", "1")
                val rosMasterUri="http://${launchConfiguration.rosMasterAddr}:${launchConfiguration.rosMasterPort}"
                cmdLine.withEnvironment("ROS_MASTER_URI", rosMasterUri)
                val handler = KillableColoredProcessHandler(cmdLine)
                handler
            } ?: NopProcessHandler()
        } ?: NopProcessHandler()

    }


}