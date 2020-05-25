package http.responses.statuscontent

import scala.concurrent._
import scala.language.postfixOps

import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import youtube.captions.request.YouTubeCaptionsRequest

case class YouTubeCaptionsResponse private (response: Future[(StatusCode, String)])

object YouTubeCaptionsResponse extends StatusContentResponse {
  def apply(request: YouTubeCaptionsRequest)(implicit
      actorSystem: ActorSystem,
      actorMaterializer: ActorMaterializer,
      executionContext: ExecutionContextExecutor
    ): YouTubeCaptionsResponse = YouTubeCaptionsResponse(response(request.request))
}