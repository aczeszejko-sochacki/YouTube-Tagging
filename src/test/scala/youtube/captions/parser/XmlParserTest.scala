package youtube.captions.parsing

import org.scalatest._

class XmlParserSpec extends FlatSpec with XmlParser {
  "Parsing response content from youtube service" should "return concatenated captions given correct content" in {
    val responseContent = """
    <transcript>
      <text start="0" dur="1.329">The year is</text>
      <text start="1.329" dur="3.679">2017 and in November of this year it will mark the 100th anniversary</text>
    </transcript>
    """

    assertResult("The year is 2017 and in November of this year it will mark the 100th anniversary") {
      captionsFromStringResponse(responseContent)
    }
  }

  it should "return empty string given wrong xml string" in {
    val wrongResponseContent = """
    <painfulxml>
    </painfulxml>
    """
    assertResult("") {
      captionsFromStringResponse(wrongResponseContent)
    }
  }
}