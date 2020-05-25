package http.responses.statuscontent

import scala.concurrent._
import scala.language.postfixOps

import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import wiki.article.request.WikiArticleRequest

case class WikiArticleResponse private (response: Future[(StatusCode, String)])

object WikiArticleResponse extends StatusContentResponse {
  def apply(request: WikiArticleRequest)(implicit
      actorSystem: ActorSystem,
      actorMaterializer: ActorMaterializer,
      executionContext: ExecutionContextExecutor
    ): WikiArticleResponse = WikiArticleResponse(response(request.request))
}