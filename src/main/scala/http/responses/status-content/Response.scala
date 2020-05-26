package http.responses.statuscontent

import scala.concurrent._
import scala.language.postfixOps

import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

class StatusContentResponse {
  type StatusContent = (StatusCode, String)

  def response(request: HttpRequest)(implicit
      actorSystem: ActorSystem,
      actorMaterializer: ActorMaterializer,
      executionContext: ExecutionContextExecutor
    ): Future[(StatusCode, String)] = {
      val response = Http().singleRequest(request)
      val status: Future[StatusCode] = response map (_.status)
      val content: Future[String] = 
        response.flatMap { response =>
          response.entity.dataBytes
            .runReduce(_ ++ _)
            .map(_.utf8String)
        }

       status zip content
    }
}