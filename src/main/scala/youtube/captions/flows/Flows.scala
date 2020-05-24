package youtube.captions.flows

import scala.concurrent._

import akka.NotUsed
import akka.event.LoggingAdapter
import akka.stream.scaladsl._
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.http.scaladsl.model._

import youtube.captions.parsing.XmlParser
import youtube.YouTubeCaptions
import youtube.captions.request.YouTubeCaptionsRequest
import youtube.captions.response.YouTubeCaptionsResponse

trait CaptionFlows extends XmlParser {
  def idToYtIdCaptionsRaw(implicit
      actorSystem: ActorSystem,
      actorMaterializer: ActorMaterializer,
      executionContext: ExecutionContextExecutor,
      log: LoggingAdapter,
      asyncParallelism: Int,
      lang: String
    ): Flow[String, YtIdCaptionsRaw, NotUsed] =
    Flow[String]

      // Download captions of videos from the external API
      .mapAsync(asyncParallelism)(id => {
        log.info(s"Started downloading captions for video $id")
        YouTubeCaptionsResponse(YouTubeCaptionsRequest(YouTubeCaptions(id, lang)))
          .response
          .map(rawCaptions => YtIdCaptionsResponse(id, rawCaptions._1, rawCaptions._2))
      })

      // Handle wrong responses
      .map {
        case YtIdCaptionsResponse(id, StatusCodes.NotFound, content) => {
          log.error(s"Wrong video $id")

          YtIdCaptionsResponse(id, StatusCodes.NotFound, content)  // Missing haskell @ :/
        }
        case YtIdCaptionsResponse(id, StatusCodes.OK, "") => {
          log.warning(s"No captions for the video in requested language")
        }

        // Unexpected
        case YtIdCaptionsResponse(id, status, content) => {
          log.warning(s"Unexpected status $status of the captions response for video $id")
          YtIdCaptionsResponse(id, status, content)
        }
      }

      // Create the final result
      .filter { case YtIdCaptionsResponse(id, status, content) => status == StatusCodes.OK && content != "" }
      .map { case YtIdCaptionsResponse(id, status, content) => {
        log.info(s"Successfully downloaded captions for video $id")
        YtIdCaptionsRaw(id, content) 
      }}

  def ytCaptionsRawToParsed(implicit
      actorSystem: ActorSystem,
      actorMaterializer: ActorMaterializer,
      executionContext: ExecutionContextExecutor
    ): Flow[YtIdCaptionsRaw, YtIdCaptionsParsed, NotUsed] =
    Flow[YtIdCaptionsRaw]
      .map { case YtIdCaptionsRaw(id, content) => YtIdCaptionsParsed(id, content, captionsFromStringResponse(content)) }

}