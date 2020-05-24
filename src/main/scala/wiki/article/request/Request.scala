package wiki.article.request

import akka.http.scaladsl.model._

case class WikiArticleRequest private (request: HttpRequest)

object WikiArticleRequest {
  def apply(unigram: String): WikiArticleRequest =
    WikiArticleRequest(
      HttpRequest(
        uri = Uri.from(
          scheme = "https",
          host = ArticleHost,
          path = ArticlePath,
          queryString = Some(wikiArticleContentQuery(unigram)))))
}