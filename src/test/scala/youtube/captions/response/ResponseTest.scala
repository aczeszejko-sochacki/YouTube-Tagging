package youtube.captions.response

import org.scalatest.AsyncFlatSpec
import scala.language.postfixOps
import scala.concurrent._

import akka.http.scaladsl.model._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import youtube.captions.request.YouTubeCaptionsRequest
import youtube.YouTubeCaptions

class YouTubeCaptionsResponseSpec extends AsyncFlatSpec {
  implicit val actorSystem = ActorSystem()
  implicit val actorMaterializer = ActorMaterializer()
  implicit val ec = scala.concurrent.ExecutionContext.global

  "A creation of caption response" should "return status OK and content given correct caption request" in {
    val ytCaptions = YouTubeCaptions("6Af6b_wyiwI", "en")
    val correctRequest = YouTubeCaptionsRequest(ytCaptions)

    val correctResponse = YouTubeCaptionsResponse(correctRequest)

    correctResponse.response map {
      case (status: StatusCode, content: String) => {
        assert(status == StatusCodes.OK)
        assert(content.startsWith("<?xml version=\"1.0\" encoding=\"utf-8\" ?>"))
      }
    }
  }

  it should "return status NotFound given incorrect caption request id" in {

    // Wrong id
    val captionsWrongId = YouTubeCaptions("evil", "en")

    val wrongIdRequest = YouTubeCaptionsRequest(captionsWrongId)

    val wrongIdResponse = YouTubeCaptionsResponse(wrongIdRequest)

    wrongIdResponse.response map {
      case (status: StatusCode, content: String) => assert(status == StatusCodes.NotFound)
    }
  }

  it should "return status OK and empty content given correct id and wrong language" in {
    val captionsWrongLang = YouTubeCaptions("6Af6b_wyiwI", "")

    val wrongLangRequest = YouTubeCaptionsRequest(captionsWrongLang)

    val wrongLangResponse = YouTubeCaptionsResponse(wrongLangRequest)

    wrongLangResponse.response map {
      case (status: StatusCode, content: String) => { assert(status == StatusCodes.OK); assert(content == "") }
    }
  }
}