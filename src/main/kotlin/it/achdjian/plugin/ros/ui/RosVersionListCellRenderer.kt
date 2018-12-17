package it.achdjian.plugin.ros.ui

import com.intellij.openapi.util.IconLoader
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import it.achdjian.plugin.ros.settings.RosVersion
import javax.swing.JList

class RosVersionListCellRenderer : ColoredListCellRenderer<RosVersion>() {
    override fun customizeCellRenderer(list: JList<out RosVersion>, value: RosVersion?, index: Int, selected: Boolean, hasFocus: Boolean) {
        value?.let {
            icon = IconLoader.findIcon("/icons/ros.svg")
            append(it.name)
            append(it.path, SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES)
        }
    }
}