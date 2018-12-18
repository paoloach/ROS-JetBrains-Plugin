package it.achdjian.plugin.ros.settings

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.xmlb.annotations.Transient
import it.achdjian.plugin.ros.ui.RosTablePackageModel
import org.jetbrains.rpc.LOG
import java.nio.file.Files
import java.nio.file.Paths


data class RosVersion(var path: String, var name: String) {

    val system = false
    val env = diffEnvironment(Paths.get(path))
    val initWorkspaceCmd = findInitCmd(Paths.get(path))
    val envPath: List<String>
    private val createPackage: CreatePackage

    init {
        envPath = splitPath(env)
        createPackage = createPackageFactory(this)
    }

    @Transient
    val packages: MutableList<String> = ArrayList()

    fun initWorkspace(projectPath: VirtualFile) {

        initWorkspaceCmd?.let {
            val cmd = it.executableFile.absolutePath.toString() + " " + it.args
            val envList = env.map { envEntry -> envEntry.key + "=" + envEntry.value }.toTypedArray()
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
                    .map { toExtractName -> toExtractName.fileName.toString() }
                    .forEach { toAdd ->
                        packages.add(toAdd)
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


class PackagesComparator : Comparator<String> {
    override fun compare(a: String, b: String): Int {
        val isTopA = isTop(a)
        val isTopB = isTop(b)

        if (isTopA && isTopB) {
            return a.compareTo(b)
        }
        if (isTopA && !isTopB) {
            return -1
        }
        if (!isTopA && isTopB) {
            return 1
        }
        return a.compareTo(b)
    }


    private fun isTop(name: String) = name == "roscpp" || name == "rosmsg" || name == "rospy"
}