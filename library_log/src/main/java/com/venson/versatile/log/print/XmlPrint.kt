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

    override fun parseContent(msg: String): String {
        return formatXML(msg).let {
            if (it.isNullOrEmpty()) {
                NULL_TIPS
            } else {
                it
            }
        }
    }

    private fun formatXML(inputXML: String?): String? {
        inputXML ?: return null
        return try {
            val xmlInput: Source = StreamSource(StringReader(inputXML))
            val xmlOutput = StreamResult(StringWriter())
            val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "2"
            )
            transformer.transform(xmlInput, xmlOutput)
            xmlOutput.writer.toString().replaceFirst(">", ">\n")
        } catch (e: Exception) {
            e.printStackTrace()
            inputXML
        }
    }
}