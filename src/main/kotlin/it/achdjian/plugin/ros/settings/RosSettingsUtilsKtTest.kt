package it.achdjian.plugin.ros.settings

import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Paths

class RosSettingsUtilsKtTest {
    @Test
    fun rosEnvironmentVariables() {
        val kineticPath = Paths.get("/opt/ros/kinetic");
        diffEnvironment(kineticPath)
    }
}