package http.responses.statuscontent

import scala.concurrent._
import java.net.UnknownHostException

import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.event.LoggingAdapter

import youtube.captions.request.YouTubeCaptionsRequest

case class YouTubeCaptionsResponse private (response: Future[Option[YouTubeCaptionsResponse.StatusContent]])

object YouTubeCaptionsResponse extends StatusContentResponse {
  def apply(request: YouTubeCaptionsRequest)(implicit
      actorSystem: ActorSystem,
      actorMaterializer: ActorMaterializer,
      executionContext: ExecutionContextExecutor,
      log: LoggingAdapter
    ): YouTubeCaptionsResponse =
      YouTubeCaptionsResponse(
        response(request.request)
          .map(Some(_))
          .recover {
            case _: UnknownHostException => { log.error(s"No route to YouTube Api"); None }
            case e: Exception            => { log.error(s"YouTube Api internal error"); None }
          })
}