package wiki.article.flows

import akka.http.scaladsl.model._
  
case class WikiArticleRawResponse(articleLink: String, status: StatusCode, content: String)
case class WikiArticleRaw(articleLink: String, content: String)
case class WikiArticleParsed(articleLink: String, rawContent: String, parsedContent: String)