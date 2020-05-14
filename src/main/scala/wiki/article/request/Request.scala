package wiki.article.request

import akka.http.scaladsl.model._

case class WikiArticleRequest private (request: HttpRequest)

object WikiArticleRequest {
  val (articleHost, articlePath) = ("en.wikipedia.org", "/w/api.php")

  def apply(unigram: String): WikiArticleRequest =
    WikiArticleRequest(
      HttpRequest(
        uri = Uri.from(
          scheme = "https",
          host = articleHost,
          path = articlePath,
          queryString = Some(s"action=query&titles=$unigram&prop=extracts&format=xml&exintro=1&explaintext"))))
}