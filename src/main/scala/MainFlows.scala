package main

import scala.concurrent._

import akka.NotUsed
import akka.event.LoggingAdapter
import akka.stream.scaladsl._
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import net.liftweb.json._
import net.liftweb.json.Serialization.writePretty

import text.processing.NamedEntityRecognition
import youtube.captions.flows.YtTaggedVideo
import youtube.captions.flows.YtIdCaptionsParsed
import wiki.article.flows.{ WikiArticleParsed, ArticleFlows }

trait MainFlows extends NamedEntityRecognition with ArticleFlows {
  // For json writing
  implicit val formats = DefaultFormats

  def ytTaggedVideoToYtVideoArticle(implicit
      actorSystem: ActorSystem,
      actorMaterializer: ActorMaterializer,
      executionContext: ExecutionContextExecutor,
      log: LoggingAdapter,
      asyncParallelism: Int,
      lang: String
    ): Flow[YtTaggedVideo, YtVideoArticle, NotUsed] =
    Flow[YtTaggedVideo]
      .mapAsync(asyncParallelism) {
        case YtTaggedVideo(video, List()) => {
          log.warning(s"No tag for video ${video.id}")
          Future(YtVideoArticle(video, List()))
        }
        case YtTaggedVideo(video, tags) => {
          log.info(s"Started downloading articles for video ${video.id}")
          Source(tags.toSet)
            .via(tagToArticleLinkRaw)
            .via(wikiArticleRawToParsed)
            .runWith(Sink.collection[WikiArticleParsed, List[WikiArticleParsed]])
            .map(articles => YtVideoArticle(video, articles))
        }
      }

  def ytCaptionsParsedtoTaggedVideo(implicit
      actorSystem: ActorSystem,
      actorMaterializer: ActorMaterializer,
      executionContext: ExecutionContextExecutor
    ): Flow[YtIdCaptionsParsed, YtTaggedVideo, NotUsed] =
    Flow[YtIdCaptionsParsed]
      .map { case video: YtIdCaptionsParsed => YtTaggedVideo(video, getUnigramEntities(video.parsedCaptions)) }

  def stringToJsonString: Flow[YtVideoArticle, String, NotUsed] =
    Flow[YtVideoArticle]
      .map(writePretty(_))
}