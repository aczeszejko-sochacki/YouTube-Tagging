package youtube.captions.flows

import akka.http.scaladsl.model._

case class YtIdCaptionsResponse(id: String, status: StatusCode, content: String)
case class YtIdCaptionsRaw(id: String, content: String)
case class YtIdCaptionsParsed(id: String, rawCaptions: String, parsedCaptions: String)
case class YtTaggedVideo(video: YtIdCaptionsParsed, tags: Option[List[String]])
