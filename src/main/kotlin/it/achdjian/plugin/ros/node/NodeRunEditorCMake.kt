package it.achdjian.plugin.ros.node

import com.intellij.execution.ui.CommonProgramParametersPanel
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.fields.IntegerField
import com.intellij.ui.layout.panel
import com.intellij.util.ui.GridBag
import com.intellij.util.ui.JBUI
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfigurationSettingsEditor
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper
import com.jetbrains.cidr.execution.ExecutableData
import it.achdjian.plugin.ros.data.RosNode
import it.achdjian.plugin.ros.data.RosPackage
import it.achdjian.plugin.ros.ui.RosNodeListCellRenderer
import it.achdjian.plugin.ros.ui.RosPackageListCellRenderer
import it.achdjian.plugin.ros.utils.getPackages
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel

class NodeRunEditorCMake(val project: Project, helper: CMakeBuildConfigurationHelper) : CMakeAppRunConfigurationSettingsEditor(project, helper) {
    companion object {
        private val LOG = Logger.getInstance(NodeRunEditorCMake::class.java)
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

    override fun resetEditorFrom(cmakeConfiguration: CMakeAppRunConfiguration) {
      //  super.resetEditorFrom(cmakeConfiguration)

        val configuration = cmakeConfiguration as NodeConfigurationCMake

        packages.firstOrNull { it.name == configuration.pack }?.let {
            comboPackages.selectedItem = it
            comboNodes.selectedItem = it.getNodes().firstOrNull { it.name == configuration.node }
        }
        arguments.reset(configuration.programConfParamenters)
        rosMasterAddr.text = configuration.rosMasterAddr
        rosMasterPort.value = configuration.rosMasterPort

    }

    override fun applyEditorTo(cmakeConfiguration: CMakeAppRunConfiguration) {
        //super.applyEditorTo(cmakeConfiguration)

        val configuration = cmakeConfiguration as NodeConfigurationCMake

        comboPackages.selectedItem?.let {
            configuration.pack = (it as RosPackage).name
        }
        comboNodes.selectedItem?.let {
            configuration.node = (it as RosNode).name
            cmakeConfiguration.executableData = ExecutableData(it.path.toString())

        }
        arguments.applyTo(configuration.programConfParamenters)
        configuration.rosMasterAddr=rosMasterAddr.text
        configuration.rosMasterPort=rosMasterPort.value


    }

    override fun createEditor(): JComponent {
        val jPanel = JPanel(GridBagLayout())
        val gridBag = GridBag().setDefaultFill(1).setDefaultAnchor(10).setDefaultWeightX(1, 1.0).setDefaultInsets(0, JBUI.insets(0, 0, 4, 10)).setDefaultInsets(1, JBUI.insetsBottom(4))
        this.createEditorInner(jPanel, gridBag)

        return jPanel
    }


    override fun createEditorInner(mainPanel: JPanel, gridBag: GridBag){
        //this.setupTargetCombo(mainPanel, gridBag)
//        this.setupConfigurationCombo(mainPanel, gridBag)
        //this.createAdditionalControls(mainPanel, gridBag)
        //this.setupCommonProgramParametersPanel(mainPanel, gridBag)


        for (component in mainPanel.components) {
            if (component is CommonProgramParametersPanel) {
                component.setVisible(false)//todo get rid of this hack
            }
        }

        val panel = panel {
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
        mainPanel.add(panel)
    }

}