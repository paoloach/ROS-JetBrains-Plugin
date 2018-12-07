package it.achdjian.plugin.ros.generator

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


data class RosPackage(
        val name: String,
        val description: String) {
}


