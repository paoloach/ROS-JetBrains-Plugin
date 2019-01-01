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
                val cmdLine = GeneralCommandLine(rosVersion.rosLaunch, launchFile.path)
                cmdLine.setWorkDirectory("/home/paolo/ros/irobot")
                cmdLine.withEnvironment(environmentVariables)
                cmdLine.withEnvironment("PYTHONUNBUFFERED", "1")
                val handler = KillableColoredProcessHandler(cmdLine)
                handler
            } ?: NopProcessHandler()
        } ?: NopProcessHandler()

    }


}