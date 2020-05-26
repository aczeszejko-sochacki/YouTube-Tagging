package http.responses.statuscontent

import scala.concurrent._
import java.net.UnknownHostException

import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.event.LoggingAdapter

import wiki.article.request.WikiArticleRequest

case class WikiArticleResponse private (response: Future[Option[WikiArticleResponse.StatusContent]])

object WikiArticleResponse extends StatusContentResponse {
  def apply(request: WikiArticleRequest)(implicit
      actorSystem: ActorSystem,
      actorMaterializer: ActorMaterializer,
      executionContext: ExecutionContextExecutor,
      log: LoggingAdapter
    ): WikiArticleResponse =
      WikiArticleResponse(
        response(request.request)
          .map(Some(_))
          .recover {
            case _: UnknownHostException => { log.error(s"No route to Wikipedia Api"); None }
            case e: Exception            => { log.error(s"Wikipedia Api internal error"); None }
          })
}