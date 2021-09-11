package com.venson.versatile.log.print

import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

internal object XmlPrint : BasePrint() {

    override fun print(type: Int, tag: String?, header: String, msg: String) {
        val message: String = formatXML(msg).let {
            if (it.isNullOrEmpty()) {
                header + NULL_TIPS
            } else {
                header + it
            }
        }
        printLine(tag, true)
        message.split(LINE_SEPARATOR).forEach {
            if (it.trim().isNotEmpty()) {
                printSub(type, tag, "║ $it")
            }
        }
        printLine(tag, false)
    }

    private fun formatXML(inputXML: String?): String? {
        inputXML ?: return null
        return try {
            val xmlInput: Source = StreamSource(StringReader(inputXML))
            val xmlOutput = StreamResult(StringWriter())
            val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(xmlInput, xmlOutput)
            xmlOutput.writer.toString().replaceFirst(">", ">\n")
        } catch (e: Exception) {
            e.printStackTrace()
            inputXML
        }
    }
}