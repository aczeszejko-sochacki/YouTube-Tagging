package youtube.captions.response

import scala.concurrent._
import scala.language.postfixOps

import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import youtube.captions.request.YouTubeCaptionsRequest

case class YouTubeCaptionsResponse private (response: Future[(StatusCode, String)])

object YouTubeCaptionsResponse {
  def apply(request: YouTubeCaptionsRequest)(implicit
      actorSystem: ActorSystem,
      actorMaterializer: ActorMaterializer,
      executionContext: ExecutionContextExecutor
    ): YouTubeCaptionsResponse = {
      val response = Http().singleRequest(request.request)
      val status: Future[StatusCode] = response map (_.status)
      val content: Future[String] = 
        response.flatMap { response =>
          response.entity.dataBytes
            .runReduce(_ ++ _)
            .map(_.utf8String)
        }

      YouTubeCaptionsResponse{ status zip content }
    }
}