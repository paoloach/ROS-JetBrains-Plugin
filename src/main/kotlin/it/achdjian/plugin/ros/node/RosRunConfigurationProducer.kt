package it.achdjian.plugin.ros.node

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement

class RosRunConfigurationProducer : RunConfigurationProducer<NodeConfiguration>(NodeConfigurationType.getInstance().confFactory) {
    override fun isConfigurationFromContext(configuration: NodeConfiguration?, context: ConfigurationContext?): Boolean {
        return false;
    }

    override fun setupConfigurationFromContext(configuration: NodeConfiguration?, context: ConfigurationContext?, sourceElement: Ref<PsiElement>?): Boolean {
        return false;
    }
}