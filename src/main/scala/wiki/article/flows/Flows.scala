package wiki.article.flows

import scala.concurrent._

import akka.NotUsed
import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.stream.scaladsl._
import akka.stream.Materializer
import akka.actor.ActorSystem

import main.GlobalTypes._
import wiki.article.parsing.XmlParser
import wiki.article.request.WikiArticleRequest
import http.responses.statuscontent.WikiArticleResponse

trait ArticleFlows extends XmlParser {
  val wikipediaCoreUrl = ".wikipedia.org/wiki/"

  def tagToArticleLinkRaw(implicit
      actorSystem: ActorSystem,
      actorMaterializer: Materializer,
      executionContext: ExecutionContextExecutor,
      log: LoggingAdapter,
      asyncParallelism: Parallelism,
      lang: Language
    ): Flow[String, WikiArticleRaw, NotUsed] =
    Flow[String]

      // Download articles for tags from the external API
      .mapAsync(asyncParallelism)(tag => {
        log.info(s"Started downloading article for tag $tag")
        WikiArticleResponse(WikiArticleRequest(tag))
          .response
          .map {
            case Some(rawArticle) => WikiArticleRawResponse(tag, rawArticle._1, rawArticle._2)
            case None => None
          }
      })

      // Filter responses failed with an error
      .filterNot(_ == None)

      // Handle wrong responses (status code not OK etc.)
      .map {
        case WikiArticleRawResponse(tag, status, _) if (status != StatusCodes.OK) => {
          log.error(s"Could not download article for tag $tag. Response status code: $status")
          WikiArticleRawResponse(tag, status, "")
        }
        case WikiArticleRawResponse(tag, StatusCodes.OK, content) => WikiArticleRawResponse(tag, StatusCodes.OK, content)
      }
      .filter { case WikiArticleRawResponse(tag, status, content) => status == StatusCodes.OK && content != "" }

      // Create the final result
      .map { case WikiArticleRawResponse(tag, status, content) => {
        log.info(s"Successfully downloaded article for tag $tag")
        WikiArticleRaw(lang + wikipediaCoreUrl + tag, content)
      }}

  def wikiArticleRawToParsed(implicit
      actorSystem: ActorSystem,
      actorMaterializer: Materializer,
      executionContext: ExecutionContextExecutor
    ): Flow[WikiArticleRaw, WikiArticleParsed, NotUsed] =
    Flow[WikiArticleRaw]
      .map { case WikiArticleRaw(tag, rawContent) => WikiArticleParsed(tag, rawContent, articleFromStringResponse(rawContent)) }
}
