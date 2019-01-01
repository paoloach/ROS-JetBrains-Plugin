package it.achdjian.plugin.ros.launch

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.components.textFieldWithBrowseButton
import com.intellij.ui.layout.panel
import it.achdjian.plugin.ros.ui.LauncherFileChooser
import javax.swing.JPanel


object LaunchFileChooserDescriptor : FileChooserDescriptor(true, false, false, false, false, false)

class LaunchEditor(private val project: Project) : SettingsEditor<LaunchConfiguration>() {
    private var  browseButton: TextFieldWithBrowseButton? = null

    override fun applyEditorTo(launchConfiguration: LaunchConfiguration) {
        browseButton?.let {
            launchConfiguration.path = VirtualFileManager.getInstance().findFileByUrl(it.text)
        }
    }

    override fun resetEditorFrom(launchConfiguration: LaunchConfiguration) {
        browseButton?.let {
            launchConfiguration.path?.let{path->
                it.text = path.path
            }

        }
    }

    override fun createEditor() : JPanel {
        val p =  panel {
            row("Launch file") {
                browseButton = textFieldWithBrowseButton("Launch file", "", project, LaunchFileChooserDescriptor)
            }
        }
        return p;
    }
}