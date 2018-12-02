package it.achdjian.plugin.ros

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import it.achdjian.plugin.ros.settings.RosVersion

@State(name = "ROS.environments", storages = [(Storage("ROSEnvironments.xml"))])
data class RosEnvironments(var versions: MutableList<RosVersion> =ArrayList())  : PersistentStateComponent<RosEnvironments> {

    override fun getState(): RosEnvironments {
        return this
    }

    override fun loadState(state: RosEnvironments) {
        val state = ApplicationManager.getApplication().getComponent(RosEnvironments::class.java, RosEnvironments())
        versions = state.versions
    }

    fun contains(rosVersion: RosVersion) = versions.contains(rosVersion)

    fun add(rosVersion: RosVersion) = versions.add(rosVersion)
}