package it.achdjian.plugin.ros.node

import com.intellij.execution.ui.CommonProgramParametersPanel
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.fields.IntegerField
import com.intellij.ui.layout.panel
import it.achdjian.plugin.ros.data.RosNode
import it.achdjian.plugin.ros.data.RosPackage
import it.achdjian.plugin.ros.ui.RosNodeListCellRenderer
import it.achdjian.plugin.ros.ui.RosPackageListCellRenderer
import it.achdjian.plugin.ros.utils.getPackages

class NodeRunEditor(val project: Project) : SettingsEditor<NodeConfiguration>() {
    companion object {
        private val LOG = Logger.getInstance(NodeRunEditor::class.java)
    }

    private val comboPackages = ComboBox<RosPackage>()
    private val comboNodes = ComboBox<RosNode>()
    private val arguments = CommonProgramParametersPanel()
    private val packages = getPackages(project)
    private val rosMasterAddr = JBTextField("127.0.0.1")
    private val rosMasterPort = IntegerField("11311", 0, 65535)


    init {
        comboPackages.renderer = RosPackageListCellRenderer()
        comboNodes.renderer = RosNodeListCellRenderer()
        packages.forEach { comboPackages.addItem(it) }
        comboPackages.addActionListener { actionEvent ->
            LOG.trace("ActionEvent: ${actionEvent}")
            val selected = comboPackages.selectedItem as RosPackage
            LOG.trace("selected: ${selected.name}")
            comboNodes.removeAllItems()
            selected.getNodes().forEach { comboNodes.addItem(it) }
        }


        val selected = comboPackages.selectedItem as RosPackage
        selected.getNodes().forEach { comboNodes.addItem(it) }
    }


    override fun resetEditorFrom(configuration: NodeConfiguration) {
        packages.firstOrNull { it.name == configuration.pack }?.let {
            comboPackages.selectedItem = it
            comboNodes.selectedItem = it.getNodes().firstOrNull { it.name == configuration.node }
        }
        arguments.reset(configuration.programConfParamenters)
        rosMasterAddr.text = configuration.rosMasterAddr
        rosMasterPort.value = configuration.rosMasterPort
        LOG.info("set with configuration: $configuration")
    }

    override fun applyEditorTo(configuration: NodeConfiguration) {
        comboPackages.selectedItem?.let {
            configuration.pack = (it as RosPackage).name
        }
        comboNodes.selectedItem?.let { configuration.node = (it as RosNode).name }
        arguments.applyTo(configuration.programConfParamenters)
        configuration.rosMasterAddr=rosMasterAddr.text
        configuration.rosMasterPort=rosMasterPort.value
        LOG.info("set configuration a: $configuration")
    }


    override fun createEditor() = panel {
        row("Package") {
            comboPackages(grow)
        }

        row("Node") {
            comboNodes(grow)
        }
        titledRow("ROS MASTER") {
            row("ROS MASTER address") {
                rosMasterAddr(grow)
            }
            row("ROS MASTER port") {
                rosMasterPort(grow)
            }
        }
        row{
            arguments(grow)
        }

    }

}