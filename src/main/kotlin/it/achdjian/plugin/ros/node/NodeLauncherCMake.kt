package it.achdjian.plugin.ros.node

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.jetbrains.cidr.cpp.execution.CMakeLauncher
import com.jetbrains.cidr.system.LocalHost
import it.achdjian.plugin.ros.utils.getPackages
import it.achdjian.plugin.ros.utils.getVersion

class NodeLauncherCMake(private val nodeConfiguration: NodeConfigurationCMake, val prj: Project, environment: ExecutionEnvironment) : CMakeLauncher(environment, nodeConfiguration) {
    companion object {
        private val LOG = Logger.getInstance(NodeLauncherCMake::class.java)
    }


    override fun createProcess(p0: CommandLineState): ProcessHandler {
        return nodeConfiguration.pack?.let { packetName ->
            getPackages(nodeConfiguration.project)
                    .firstOrNull { it.name == packetName }?.let { rosPackage ->
                        rosPackage.getNodes().firstOrNull { it.name == nodeConfiguration.node }?.let { rosNode ->
                            val cmdLine = GeneralCommandLine(rosNode.path.toString())
                                    .withParameters(nodeConfiguration.programConfParamenters.getProgramParametersList())
                                    .withEnvironment(getVersion(prj)?.env)
                                    .withEnvironment(nodeConfiguration.programConfParamenters.envs)
                                    .withEnvironment("PYTHONUNBUFFERED", "1")
                            nodeConfiguration.programConfParamenters.workingDirectory?.let {
                                cmdLine.withWorkDirectory(it)
                            } ?: cmdLine.withWorkDirectory(rosPackage.path.toFile())


                            LOG.debug("Run ${cmdLine.commandLineString} in ${cmdLine.workDirectory}")
                            return LocalHost.INSTANCE.createProcess(cmdLine, true, true)
                        } ?: NopProcessHandler()
                    } ?: NopProcessHandler()
        } ?: NopProcessHandler()
    }

    override fun getProject() = prj
}