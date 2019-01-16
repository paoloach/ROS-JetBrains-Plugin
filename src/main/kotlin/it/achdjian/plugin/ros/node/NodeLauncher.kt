package it.achdjian.plugin.ros.node

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.PtyCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebugSession
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess
import com.jetbrains.cidr.execution.testing.CidrLauncher
import com.jetbrains.cidr.system.LocalHost
import it.achdjian.plugin.ros.utils.getPackages
import it.achdjian.plugin.ros.utils.getVersion
import java.io.File

class NodeLauncher(private val nodeConfiguration: NodeConfiguration, environment: ExecutionEnvironment) : CommandLineState(environment) {
    companion object {
        private val LOG = Logger.getInstance(NodeLauncher::class.java)
    }


    fun getProject() = environment.project

    fun createProcess(): ProcessHandler = startProcess()

    override fun startProcess(): ProcessHandler {
        return nodeConfiguration.pack?.let { packetName ->
            getPackages(nodeConfiguration.project)
                    .firstOrNull { it.name == packetName }?.let { rosPackage ->
                        rosPackage.getNodes().firstOrNull { it.name == nodeConfiguration.node }?.let { rosNode ->
                            var workDir = File(nodeConfiguration.programConfParamenters.workingDirectory)
                            if (!workDir.exists()) {
                                workDir = rosPackage.path.toFile();
                            }
                            val cmdLine = PtyCommandLine(GeneralCommandLine(rosNode.path.toString())
                                    .withParameters(nodeConfiguration.programConfParamenters.getProgramParametersList())
                                    .withEnvironment(getVersion(environment.project)?.env)
                                    .withEnvironment(nodeConfiguration.programConfParamenters.envs)
                                    .withEnvironment("PYTHONUNBUFFERED", "1"))
                                    .withWorkDirectory(workDir)
                            val rosMasterUri = "http://${nodeConfiguration.rosMasterAddr}:${nodeConfiguration.rosMasterPort}"
                            cmdLine.withEnvironment("ROS_MASTER_URI", rosMasterUri)
                            LOG.debug("Run ${cmdLine.commandLineString} in ${cmdLine.workDirectory}")
                            return KillableColoredProcessHandler(cmdLine) //LocalHost.INSTANCE.createProcess(cmdLine, true, true)
                        } ?: NopProcessHandler()
                    } ?: NopProcessHandler()
        } ?: NopProcessHandler()
    }


}