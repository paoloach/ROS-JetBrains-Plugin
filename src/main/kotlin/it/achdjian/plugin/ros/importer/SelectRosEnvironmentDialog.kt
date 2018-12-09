package it.achdjian.plugin.ros.importer

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.ComboBox
import it.achdjian.plugin.ros.RosEnvironments
import it.achdjian.plugin.ros.settings.RosVersion
import javax.swing.JDialog

class SelectRosEnvironmentDialog : JDialog(){
    var version: RosVersion? = null

    init {
        val state = ApplicationManager.getApplication().getComponent(RosEnvironments::class.java, RosEnvironments())
        val comboBox = ComboBox(state.versions.map { it.name }.toTypedArray())
        comboBox.addItemListener{
            version = state.versions.find { version -> version.name == it.item.toString() }
        }
        contentPane.add(comboBox)
    }
}