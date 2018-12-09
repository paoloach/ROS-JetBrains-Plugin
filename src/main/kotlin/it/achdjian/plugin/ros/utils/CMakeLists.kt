package it.achdjian.plugin.ros.utils

import com.intellij.openapi.util.io.FileUtil
import com.jetbrains.cidr.cpp.cmake.CMakeSettings
import it.achdjian.plugin.ros.RosEnvironments
import it.achdjian.plugin.ros.settings.RosVersion
import java.io.File

fun getResourceAsString(resourceName: String): String {
    val resource = RosEnvironments::class.java.classLoader.getResourceAsStream(resourceName)
    return resource?.let {
        FileUtil.loadTextAndClose(resource)
    } ?: ""

}

fun createMainCMakeLists():String {
    return getResourceAsString("templates/CMakeLists.txt")
}


fun releaseProfile(version: RosVersion, baseDir: File) : CMakeSettings.Profile {
    val buildDir = File(baseDir, "build")
    val options = "-DCATKIN_DEVEL_PREFIX=${baseDir}/devel -DCMAKE_INSTALL_PREFIX=${baseDir}/install"
    return CMakeSettings.Profile(
            "Release",
            "Release",
            "",
            "",
            true,
            version.env,
            buildDir,
            options)
}