package wiki.article.response

import scala.concurrent._
import scala.language.postfixOps

import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import wiki.article.request.WikiArticleRequest

case class WikiArticleResponse private (response: Future[(StatusCode, String)])

object WikiArticleResponse {
  def apply(request: WikiArticleRequest)(implicit
      actorSystem: ActorSystem,
      actorMaterializer: ActorMaterializer,
      executionContext: ExecutionContextExecutor
    ): WikiArticleResponse = {
      val response = Http().singleRequest(request.request)
      val status: Future[StatusCode] = response map (_.status)
      val content: Future[String] = 
        response.flatMap { response =>
          response.entity.dataBytes
            .runReduce(_ ++ _)
            .map(_.utf8String)
        }

      WikiArticleResponse{ status zip content }
    }
}