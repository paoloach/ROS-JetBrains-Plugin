package it.achdjian.plugin.ros.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.options.Configurable
import com.intellij.ui.IdeBorderFactory.createTitledBorder
import com.intellij.ui.layout.LCFlags
import it.achdjian.plagin.ros.ui.GridLayout2
import it.achdjian.plugin.ros.RosEnvironments
import java.awt.BorderLayout
import java.awt.Button
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class ScanActionListener(private val panel: JPanel) : ActionListener{
    var modified = false

    override fun actionPerformed(p0: ActionEvent?) {
        val versions = scan()

        val state = ApplicationManager.getApplication().getComponent(RosEnvironments::class.java, RosEnvironments())

        versions.filter{!state.contains(it)}.forEach {
            addVersion(it, panel)
            state.add(it)
            modified=true
        }
    }

    private fun scan(): List<RosVersion> =
            Files
                    .list(Paths.get("/opt/ros"))
                    .map { RosVersion(it.fileName.toString(), it, diffEnvironment(it), findInitCmd(it)) }
                    .collect(Collectors.toList())

}

class RosSettings : ApplicationComponent, Configurable {
    private var scanActionListener : ScanActionListener? = null

    override fun isModified(): Boolean {
        var modified=false
        scanActionListener?.let {
            modified = it.modified
        }
        return modified
    }

    override fun apply() {
    }

    override fun createComponent(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        val scanButton = Button("Scan ROS version")
        scanButton.maximumSize = Dimension(200,200)
        panel.add(scanButton)
        val versionPanel = JPanel()
        val internalPanel = JPanel()
        versionPanel.layout = BorderLayout()
        versionPanel.add(internalPanel, BorderLayout.CENTER)
        versionPanel.border = createTitledBorder("ROS Versions")
        internalPanel.layout = BoxLayout(internalPanel, BoxLayout.Y_AXIS)
        val state = ApplicationManager.getApplication().getComponent(RosEnvironments::class.java, RosEnvironments())
        state.versions.forEach{
            addVersion(it, internalPanel)
        }

        panel.add(versionPanel)
        scanActionListener = ScanActionListener(internalPanel)
        scanButton.addActionListener( scanActionListener )

        return panel
    }


    override fun getDisplayName() = "ROS"
}

fun addVersion(version: RosVersion, panel: JPanel) {

    val versionPanel = com.intellij.ui.layout.panel(LCFlags.fillX, title = version.name){
        row("name"){
            label(version.name)
        }
        row("path"){
            label(version.path.toString())
        }
        row("init workspace command"){
            label(version.initWorkspaceCmd?.toString() ?: "Not found")
        }
    }

    panel.add(versionPanel)
}