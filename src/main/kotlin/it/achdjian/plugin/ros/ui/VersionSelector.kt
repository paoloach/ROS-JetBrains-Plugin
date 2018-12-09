package it.achdjian.plugin.ros.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.ComboBox
import it.achdjian.plugin.ros.RosEnvironments
import java.awt.Component
import java.awt.Graphics
import javax.swing.*
import javax.swing.border.EmptyBorder

class VersionSelector : ComboBox<Any>() {
    companion object {
        const val TABLE_CELL_EDITOR="JComboBox.isTableCellEditor"
        const val SHOW_ALL = "ShowAll"
    }

    init {
        val state = ApplicationManager.getApplication().getComponent(RosEnvironments::class.java, RosEnvironments())
        state.versions.forEach {
            addItem(it.name)
        }
        addItem(VersionSelectorRenderer.SEPARATOR_STRING)
        addItem(SHOW_ALL)
        renderer = VersionSelectorRenderer()
    }

    override fun setSelectedItem(item: Any) {
        if (item == SHOW_ALL) {
            ApplicationManager.getApplication().invokeLater {
               // val allDialog = if (this@PyActiveSdkConfigurable.myModule == null) PythonSdkDetailsDialog(this@PyActiveSdkConfigurable.myProject, this@PyActiveSdkConfigurable.myAddSdkCallback, this@PyActiveSdkConfigurable.getSettingsModifiedCallback()) else PythonSdkDetailsDialog(this@PyActiveSdkConfigurable.myModule!!, this@PyActiveSdkConfigurable.myAddSdkCallback, this@PyActiveSdkConfigurable.getSettingsModifiedCallback())
               // allDialog.show()
            }
        } else {
            if (VersionSelectorRenderer.SEPARATOR_STRING != item) {
                super.setSelectedItem(item)
            }

        }
    }

}

class VersionSelectorRenderer :  JLabel() , ListCellRenderer<Any?> {

    companion object {
        const val SEPARATOR_STRING="SEPARATOR"
    }
    private var separator = JSeparator(SwingConstants.HORIZONTAL)

    init {
        isOpaque=true
        border = EmptyBorder(1,1,1,1)
    }


    override fun getListCellRendererComponent(list: JList<out Any?>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        val str= value?.toString()?:""
        if (SEPARATOR_STRING == str) {
            return separator
        }
        if (isSelected) {
            background = list?.selectionForeground
            foreground = list?.selectionForeground
        } else {
            background = list?.background
            foreground = list?.foreground
        }
        font = list?.font
        text=str
        return this
    }
}