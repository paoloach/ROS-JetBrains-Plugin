package it.achdjian.plugin.ros.importer

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.wm.impl.welcomeScreen.NewWelcomeScreen
import javax.swing.Icon

class ImporterRosWorkspaceAction  : AnAction(AllIcons.ToolbarDecorator.Import) , DumbAware {

    override fun actionPerformed(event: AnActionEvent?) {

    }

}