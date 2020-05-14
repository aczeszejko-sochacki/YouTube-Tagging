package wiki.article.parsing

import scala.xml.XML

trait XmlParser {
  def articleFromStringResponse(rawText: String) =
    (XML.loadString(rawText) \ "query")
      .map(_.text)
      .mkString(" ")
      .trim
}