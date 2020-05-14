package youtube.captions.request

import akka.http.scaladsl.model._

import youtube.YouTubeCaptions

// Cannot inherit from HttpRequest as it is a final class
case class YouTubeCaptionsRequest private (request: HttpRequest)

object YouTubeCaptionsRequest {
  val (captionsHost, captionsPath) = ("video.google.com", "/timedtext")

  def apply(captions: YouTubeCaptions): YouTubeCaptionsRequest = captions match {
    case YouTubeCaptions(id, language) =>
      YouTubeCaptionsRequest(
        HttpRequest(
          uri = Uri.from(
            scheme = "https",
            host = captionsHost,
            path = captionsPath,
            queryString = Some(s"lang=$language&v=$id"))))
  }
}