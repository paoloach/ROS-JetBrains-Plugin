package it.achdjian.plugin.ros.launch

import it.achdjian.plugin.ros.utils.getEnvironment
import java.io.File
import java.io.IOException
import java.io.InputStream

object test {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {


        val env = getEnvironment("/home/paolo/ros/irobot/devel/setup.bash")


        val builder = ProcessBuilder()
                .command("/opt/ros/melodic/bin/roslaunch", "src/irobotcreate2ros/launch/irobot_joy.launch")
        builder.environment().putAll(env)
        builder.environment()["PYTHONUNBUFFERED"]="1"
        builder.directory(File("/home/paolo/ros/irobot"))
        val process = builder.start()

        val inputStream = process.inputStream
        println("Process start reading")
        while (process.isAlive) {
            if (inputStream.available() > 0) {

                val data = ByteArray(inputStream.available())
                inputStream.read(data)
                print(String(data))
            }
        }
        println("Process finished")
    }
}
