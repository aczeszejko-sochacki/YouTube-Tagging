package wiki.article.request

import org.scalatest._
import akka.http.scaladsl.model._

class WikiArticleRequestSpec extends FlatSpec {
  "A creation of wiki article request" should "return correct WikiArticleRequest instance" in {
    val unigram = "yeah"

    val expectedRequest =
      HttpRequest(
        uri = Uri.from(
          scheme = "https",
          host = ArticleHost,
          path = ArticlePath,
          queryString =  Some(s"action=query&titles=$unigram&prop=extracts&format=xml&exintro=1&explaintext")))

    val testReq = WikiArticleRequest(unigram)

    assert(testReq.request == expectedRequest)
  }
}