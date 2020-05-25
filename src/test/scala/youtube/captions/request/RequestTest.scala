package youtube.captions.request

import org.scalatest._
import akka.http.scaladsl.model._

import youtube.YouTubeCaptions

class YouTubeCaptionsOpsSpec extends FlatSpec {
  "A creation of caption request" should "return correct YouTubeCaptionRequest instance" in {
    val (id, language) = ("id", "language")
    val captions = YouTubeCaptions(id, language)

    val expectedRequest =
      HttpRequest(
        uri = Uri.from(
          scheme = "https",
          host = CaptionsHost,
          path = CaptionsPath,
          queryString = Some(s"lang=$language&v=$id")))

    val testReq = YouTubeCaptionsRequest(captions)

    assert(testReq.request == expectedRequest)
  }
}