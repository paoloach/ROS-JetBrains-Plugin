package it.achdjian.plugin.ros

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import it.achdjian.plugin.ros.settings.RosVersion
import java.nio.file.Path

data class RosEnvironments(var versions: MutableList<RosVersion> = ArrayList()) {

    fun contains(rosVersion: RosVersion) = versions.contains(rosVersion)

    fun add(rosVersion: RosVersion) = versions.add(rosVersion)

    fun getOwnerVersion(path: Path) = versions.firstOrNull { path.startsWith(it.path) }
}