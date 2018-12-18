package it.achdjian.plugin.ros.data

import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class RosPackage(val path: Path) {
    val name: String
    val version: String
    val description: String

    init {

        val packageFile = path.resolve("package.xml")
        val documentBuilder = DocumentBuilderFactory.newInstance()
        val builder = documentBuilder.newDocumentBuilder()
        val doc = builder.parse(packageFile.toFile())

        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()

        val xpathName="/package/name"

        name = getNodeValue(xPath, "/package/name", doc)
        version = getNodeValue(xPath, "/package/version", doc)
        description = getNodeValue(xPath, "/package/description", doc)
    }

    private fun getNodeValue(xPath: XPath, xpath: String, doc: Document) : String {
        val nodes = xPath.evaluate(xpath,doc, XPathConstants.NODESET) as NodeList
        if (nodes.length > 0)
            return nodes.item(0).textContent.trim()
        else
            return ""
    }
}