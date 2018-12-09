package it.achdjian.plugin.ros.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.ComboBox
import com.jetbrains.python.configuration.PyActiveSdkConfigurable
import java.awt.Graphics

class VersionSelector : ComboBox<Any>() {
    companion object {
        const val TABLE_CELL_EDITOR="JComboBox.isTableCellEditor"
        const val SHOW_ALL = "ShowAll"
    }
    override fun setSelectedItem(item: Any) {
        if (item == SHOW_ALL) {
            ApplicationManager.getApplication().invokeLater {
               // val allDialog = if (this@PyActiveSdkConfigurable.myModule == null) PythonSdkDetailsDialog(this@PyActiveSdkConfigurable.myProject, this@PyActiveSdkConfigurable.myAddSdkCallback, this@PyActiveSdkConfigurable.getSettingsModifiedCallback()) else PythonSdkDetailsDialog(this@PyActiveSdkConfigurable.myModule!!, this@PyActiveSdkConfigurable.myAddSdkCallback, this@PyActiveSdkConfigurable.getSettingsModifiedCallback())
               // allDialog.show()
            }
        } else {
            if ("separator" != item) {
                super.setSelectedItem(item)
            }

        }
    }

    override fun paint(g: Graphics?) {
        try {
            putClientProperty(TABLE_CELL_EDITOR, false)
            super.paint(g)
        } finally {
            putClientProperty(TABLE_CELL_EDITOR, true)
        }
    }
}