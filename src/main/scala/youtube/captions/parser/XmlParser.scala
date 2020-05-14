package youtube.captions.parsing

import scala.xml.XML

trait XmlParser {
  def captionsFromStringResponse(rawText: String): String =
    (XML.loadString(rawText) \ "text")
      .map (_.text)
      .mkString(" ")
}