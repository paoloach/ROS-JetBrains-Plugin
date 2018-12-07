package it.achdjian.plugin.ros.settings

import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class RosVersion(val path: Path) {
    val name = path.fileName.toString()
    val env = diffEnvironment(path)
    val envPath = splitPath(env)
    val initWorkspaceCmd = findInitCmd(path)
    val createPackage = createPackageFactory(this)

    val packages: MutableList<String> = ArrayList()

    fun initWorkspace(projectPath: VirtualFile) {

        initWorkspaceCmd?.let {
            val cmd = it.executableFile.absolutePath.toString() + " " + it.args
            val envList = env.map { envEntry -> envEntry.key + "=" + envEntry.value }.toTypedArray()
            val target = Paths.get(path.toString(), "/share/catkin/cmake/toplevel.cmake")
            val link = Paths.get(projectPath.path, "src/CMakeLists.txt")
            Files.createSymbolicLink(link, target)
        }
    }

    fun createPackage(path: VirtualFile, name: String, dependencies: List<String>) = createPackage.createPackage(path, name, dependencies)

    fun searchPackages() {
        packages.clear()
        val packagesPath = env["ROS_PACKAGE_PATH"]
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

    private  fun splitPath(env: Map<String,String>):List<String> {
        var path = env["PATH"]

        if (path == null){
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