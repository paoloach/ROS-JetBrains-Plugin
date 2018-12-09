package it.achdjian.plugin.ros.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.FixedSizeButton
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.ComboboxSpeedSearch
import com.intellij.ui.IdeBorderFactory.createTitledBorder
import com.intellij.ui.layout.LCFlags
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import it.achdjian.plugin.ros.RosEnvironments
import it.achdjian.plugin.ros.ui.RosTablePackageModel
import it.achdjian.plugin.ros.ui.VersionSelector
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors
import javax.swing.*


class ScanActionListener(private val panel: JPanel) : ActionListener {
    var modified = false

    override fun actionPerformed(p0: ActionEvent?) {
        val versions = scan()

        val state = ApplicationManager.getApplication().getComponent(RosEnvironments::class.java, RosEnvironments())

        versions.filter { !state.contains(it) }.forEach {
            addVersion(it, panel)
            state.add(it)
            modified = true
        }
    }

    private fun scan(): List<RosVersion> =
            Files
                    .list(Paths.get("/opt/ros"))
                    .map { RosVersion(it.toString()) }
                    .collect(Collectors.toList())

}

class SelectEnvironment(val versionSelector: VersionSelector, val model: RosTablePackageModel) : ActionListener {
    companion object {
        private val LOG = Logger.getInstance(RosSettings::class.java)
    }

    override fun actionPerformed(actionEvent: ActionEvent) {
        val state = ApplicationManager.getApplication().getComponent(RosEnvironments::class.java, RosEnvironments())
        state.versions.forEach {
            if (it.name == versionSelector.selectedItem) {
                model.updateVersions(it)
            }
        }
        versionSelector.invalidate()
    }
}

class RosSettings : ApplicationComponent, Configurable {
    private var scanActionListener: ScanActionListener? = null
    private val model = RosTablePackageModel()

    override fun initComponent() {
        val versions = scan()
        val state = ApplicationManager.getApplication().getComponent(RosEnvironments::class.java, RosEnvironments())
        versions.filter { !state.contains(it) }.forEach {
            state.add(it)
        }
    }

    override fun isModified(): Boolean {
        var modified = false
        scanActionListener?.let {
            modified = it.modified
        }
        return modified
    }

    override fun apply() {
    }

    override fun createComponent(): JComponent {
            val layout = GridBagLayout()
        val mainPanel = JPanel(layout)
        val version = JLabel("ROS version")
        val emptyLabel = JLabel("  ")
        val versionSelector = VersionSelector()
        ComboboxSpeedSearch(versionSelector)
        versionSelector.putClientProperty(VersionSelector.TABLE_CELL_EDITOR, true)
        versionSelector.addActionListener(SelectEnvironment(versionSelector, model))
        val preferredSize = versionSelector.preferredSize


        val detailsButton = FixedSizeButton()
        detailsButton.icon = IconLoader.findIcon("/icons/ros.svg")
        detailsButton.preferredSize = Dimension(preferredSize.height, preferredSize.height)

        val packageTable = JBTable(model)


        val c = GridBagConstraints()
        c.fill = 2
        c.insets = JBUI.insets(2)
        c.gridx = 0
        c.gridy = 0
        mainPanel.add(version, c)
        c.gridx = 1
        c.gridy = 0
        c.weightx = 0.1
        mainPanel.add(versionSelector, c)
        c.insets = JBUI.insets(2, 0, 2, 2)
        c.gridx = 2
        c.gridy = 0
        c.weightx = 0.0
        mainPanel.add(detailsButton, c)

        c.insets = JBUI.insets(2, 2, 0, 2)
        c.gridx = 0
        ++c.gridy
        c.gridwidth = 3
        c.weightx = 0.0
        mainPanel.add(emptyLabel, c)
        c.gridx = 0
        ++c.gridy
        c.weighty = 1.0
        c.gridwidth = 3
        c.gridheight = -1
        c.fill = 1
        mainPanel.add(JScrollPane(packageTable), c)

        return mainPanel
    }


    override fun getDisplayName() = "ROS"

    private fun scan(): List<RosVersion> =
            Files
                    .list(Paths.get("/opt/ros"))
                    .map { RosVersion(it.toString()) }
                    .collect(Collectors.toList())
}

fun addVersion(version: RosVersion, panel: JPanel) {
    version.searchPackages()

    val versionPanel = com.intellij.ui.layout.panel(LCFlags.fillX, title = version.name) {
        row("path") {
            label(version.path.toString())
        }
        row("init workspace command") {
            label(version.initWorkspaceCmd?.toString() ?: "Not found")
        }
    }

    panel.add(versionPanel)
}