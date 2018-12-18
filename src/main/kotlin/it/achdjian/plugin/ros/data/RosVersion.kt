package it.achdjian.plugin.ros.data

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.xmlb.annotations.Transient
import it.achdjian.plugin.ros.settings.CreatePackage
import it.achdjian.plugin.ros.settings.createPackageFactory
import it.achdjian.plugin.ros.settings.diffEnvironment
import it.achdjian.plugin.ros.settings.findInitCmd
import java.nio.file.Files
import java.nio.file.Paths


data class RosVersion(var path: String, var name: String) {

    val env = diffEnvironment(Paths.get(path))
    private val initWorkspaceCmd = findInitCmd(Paths.get(path))
    val envPath: List<String>
    private val createPackage: CreatePackage

    init {
        envPath = splitPath(env)
        createPackage = createPackageFactory(this)
    }

    @Transient
    val packages: MutableList<RosPackage> = ArrayList()

    fun initWorkspace(projectPath: VirtualFile) {

        initWorkspaceCmd?.let {
            val target = Paths.get(path, "/share/catkin/cmake/toplevel.cmake")
            val link = Paths.get(projectPath.path, "src/CMakeLists.txt")
            Files.createSymbolicLink(link, target)
        }
    }

    fun createPackage(path: VirtualFile, name: String, dependencies: List<String>) = createPackage.createPackage(path, name, dependencies)

    fun searchPackages() {
        packages.clear()
        val packagesPath = env["ROS_PACKAGE_PATH"]
        LOG.info("packagePath: $packagesPath")
        packagesPath?.let { path ->
            Files
                    .list(Paths.get(path))
                    .filter { toFilter -> toFilter.resolve("package.xml").toFile().exists() }
                    .map { path -> RosPackage(path) }
                    .forEach { rosPackage ->
                        packages.add(rosPackage)
                    }
        }
        packages.sortWith(PackagesComparator())
    }

    companion object {
        private val LOG = Logger.getInstance(RosVersion::class.java)

        private fun splitPath(env: Map<String, String>): List<String> {
            var path = env["PATH"]

            if (path == null) {
                val actualEnv = System.getenv()
                path = actualEnv["PATH"]
            }
            val splittedPath = ArrayList<String>()
            path?.let {
                splittedPath.addAll(it.split(":"))
            }

            return splittedPath
        }
    }
}


class PackagesComparator : Comparator<RosPackage> {
    override fun compare(a: RosPackage, b: RosPackage): Int {
        val isTopA = isTop(a.name)
        val isTopB = isTop(b.name)

        if (isTopA && isTopB) {
            return a.name.compareTo(b.name)
        }
        if (isTopA && !isTopB) {
            return -1
        }
        if (!isTopA && isTopB) {
            return 1
        }
        return a.name.compareTo(b.name)
    }


    private fun isTop(name: String) = name == "roscpp" || name == "rosmsg" || name == "rospy"
}