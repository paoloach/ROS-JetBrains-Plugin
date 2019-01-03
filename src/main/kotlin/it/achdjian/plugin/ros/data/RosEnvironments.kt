package it.achdjian.plugin.ros.data

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.project.modifyModules
import com.jetbrains.python.sdk.baseDir
import it.achdjian.plugin.ros.utils.getRosVersionFromCMakeLists
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

class RosEnvironments {
    companion object {
        val LOG = Logger.getInstance(RosEnvironments::class.java.name)
    }

    var versions: MutableList<RosVersion> = ArrayList()
    private val defaultVersionsName: List<String>

    init {

        val defaultVersions = Files.list(Paths.get("/opt/ros")).collect(Collectors.toList())?.let { it } ?: ArrayList()
        defaultVersionsName = defaultVersions.map { it.fileName.toString() }

        LOG.trace("default version name: ")
        defaultVersionsName.forEach{LOG.trace("it")}

        val versions = HashMap<String, String>()
        defaultVersions.associateByTo(versions, { it.fileName.toString() }, { it.toString() })

        val customVerison = ApplicationManager
                .getApplication()
                .getComponent(RosCustomVersion::class.java, RosCustomVersion(HashMap()))

        LOG.trace("defaultVersionToRemove")
        customVerison.defaultVersionToRemove.forEach { LOG.trace(it) }
        customVerison.defaultVersionToRemove.forEach { versions.remove(it) }

        LOG.trace("custom versions")
        customVerison.versions.forEach { LOG.trace(it.key) }

        customVerison
                .versions
                .forEach { (key, value) -> versions[key] = value }

        LOG.trace("ROS versions")
        customVerison.versions.forEach { LOG.trace("${it.key} --> ${it.value}") }
        this.versions.addAll(scan(versions))

    }

    fun isDefaultVersion(versionName: String) = defaultVersionsName.contains(versionName)

    fun contains(rosVersion: RosVersion) = versions.contains(rosVersion)

    fun add(rosVersion: RosVersion) = versions.add(rosVersion)

    fun remove(rosVersion: RosVersion) = versions.removeIf { it.name == rosVersion.name }

    fun getOwnerVersion(path: Path) = versions.firstOrNull { path.startsWith(it.path) }

    private fun scan(versions: Map<String, String>): List<RosVersion> =
            versions.map { RosVersion(it.value, it.key) }.toList()

    fun getVersion(project: Project) =
        project.projectFile
                ?.parent
                ?.parent
                ?.findChild("CMakeLists.txt")
                ?.let {
                    getRosVersionFromCMakeLists(it)
                }


}