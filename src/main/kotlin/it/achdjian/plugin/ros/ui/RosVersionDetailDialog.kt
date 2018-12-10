package it.achdjian.plugin.ros.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.AnActionButton
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ListSpeedSearch
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import it.achdjian.plugin.ros.RosEnvironments
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ListSelectionModel

class RosVersionDetailDialog(project: Project) : DialogWrapper(project, true) {
    val versionList = JBList<String>()
    private var mainPanel: JPanel? = null

    override fun createCenterPanel(): JComponent {
        versionList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        ListSpeedSearch(versionList)


        val decorator = ToolbarDecorator
                .createDecorator(versionList)
                .disableUpDownActions()
                .setAddAction {
                    addSdk(it)
                    updateOkButton()

                }.setEditAction {
                    editSdk()
                    updateOkButton()
                }.setRemoveAction {
                    removeSdk()
                    updateOkButton()
                }
//                .addExtraAction{
//                    PythonSdkDetailsDialog.ShowPathButton()
//                }
        decorator.setPreferredSize(Dimension(600, 500))
        val panel = decorator.createPanel()
        mainPanel = panel
        refreshVersionList()
        addListeners()
        return panel

    }

    private fun addListeners() {
//        this.myListener = object : SdkModel.Listener {
//            override fun sdkChanged(sdk: Sdk, previousName: String?) {
//                if (sdk == null) {
//                    `$$$reportNull$$$0`(0)
//                }
//
//                this@PythonSdkDetailsDialog.refreshVersionList()
//            }
//        }
//        this.myProjectSdksModel.addListener(this.myListener)
//        this.mySdkList.addListSelectionListener(ListSelectionListener { this@PythonSdkDetailsDialog.updateOkButton() })
    }

    private fun refreshVersionList() {
        val state = ApplicationManager.getApplication().getComponent(RosEnvironments::class.java, RosEnvironments())
        versionList.clearSelection()
        versionList.model = CollectionListModel(state.versions)
        state.versions.forEach {

        }
//        val allPythonSdks = this.myInterpreterList.getAllPythonSdks(this.myProject)
//        var projectSdk = this.getSdk()
//        val notAssociatedWithOtherProjects = (StreamEx.of<Sdk>(allPythonSdks).filter({ sdk -> !sdk.isAssociatedWithAnotherModule(this.myModule) }) as StreamEx<*>).toList()
//        val pythonSdks = if (this.myHideOtherProjectVirtualenvs) notAssociatedWithOtherProjects else allPythonSdks
//        this.mySdkList.setModel(CollectionListModel(pythonSdks))
//        this.mySdkListChanged = false
//        if (projectSdk != null) {
//            projectSdk = this.myProjectSdksModel.findSdk(projectSdk!!.getName())
//            this.mySdkList.clearSelection()
//            this.mySdkList.setSelectedValue(projectSdk, true)
//            this.mySdkList.updateUI()
//        }

    }

    protected fun updateOkButton() {
//        super.setOKActionEnabled(this.isModified())
    }

    private fun removeSdk() {
//        val selectedSdk = this.getSelectedSdk()
//        if (selectedSdk != null) {
//            val sdk = this.myProjectSdksModel.findSdk(selectedSdk)
//            SdkConfigurationUtil.removeSdk(sdk)
//            this.myProjectSdksModel.removeSdk(sdk)
//            this.myProjectSdksModel.removeSdk(selectedSdk)
//            if (this.myModificators.containsKey(selectedSdk)) {
//                val modificator = this.myModificators.get(selectedSdk) as SdkModificator
//                this.myModifiedModificators.remove(modificator)
//                this.myModificators.remove(selectedSdk)
//            }
//
//            this.refreshVersionList()
//            this.mySdkListChanged = true
//            val currentSdk = this.getSdk()
//            if (currentSdk != null) {
//                this.mySdkList.setSelectedValue(currentSdk, true)
//            }
//        }

    }

    private fun addSdk(button: AnActionButton) {
//        PythonSdkDetailsStep.show(this.myProject, this.myModule, this.myProjectSdksModel.getSdks(), null as DialogWrapper?, this.mainPanel, button.preferredPopupPoint!!.screenPoint, null as String?) { sdk -> this.addCreatedSdk(sdk, true) }
    }

    private fun editSdk() {
//        val currentSdk = this.getSelectedSdk()
//        if (currentSdk != null) {
//            if (currentSdk!!.getSdkAdditionalData() is RemoteSdkAdditionalData<*>) {
//                this.editRemoteSdk(currentSdk)
//            } else {
//                this.editSdk(currentSdk)
//            }
//        }

    }
}