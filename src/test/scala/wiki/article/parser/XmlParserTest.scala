package wiki.article.parsing

import org.scalatest._

class XmlParserSpec extends FlatSpec with XmlParser {
  "Parsing response content from mediawiki api" should "return article text given correct content" in {
    val responseContent = """
    <api batchcomplete="">
      <warnings>
        <main xml:space="preserve">Unrecognized parameter: explaintext).</main>
        <extracts xml:space="preserve">blah blah</extracts>
      </warnings>
    <query>
      <normalized>
        <n from="wikipedia" to="Wikipedia"/>
      </normalized>
      <pages>
        <page _idx="5043734" pageid="5043734" ns="0" title="Wikipedia">
          <extract xml:space="preserve">
            <p class="mw-empty-elt">Blah blah blah</p>
          </extract>
        </page>
      </pages>
    </query>
    </api>
    """

    assertResult("Blah blah blah") {
      articleFromStringResponse(responseContent)
    }
  }

  it should "return empty string given wrong xml string" in {
    val wrongResponseContent = """
    <painfulxml>
    </painfulxml>
    """
    assertResult("") {
      articleFromStringResponse(wrongResponseContent)
    }
  }
}