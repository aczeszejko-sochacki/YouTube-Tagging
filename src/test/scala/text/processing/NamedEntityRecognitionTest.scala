package text.processing

import org.scalatest._

class NamedEntityRecognitionSpec extends FlatSpec with NamedEntityRecognition {
  "Getting named entities unigrams from a text" should "return Some instance if there are entities in the text" in {
    assertResult(List("Random", "Words", "Whateva", "Very", "Word")) {
      getUnigramEntities("Sentence of Random, Words and Whateva. Another sentence of Very random words? Last Word!")
    }
  }

  it should "return None if there are no entities in the text" in {
    assertResult(List()) {
      getUnigramEntities("No entities here")
    }
  }
}