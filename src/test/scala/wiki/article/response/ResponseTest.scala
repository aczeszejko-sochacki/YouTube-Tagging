// package wiki.article.response

// import org.scalatest._
// import akka.http.scaladsl.model._
// import akka.actor.ActorSystem
// import akka.stream.ActorMaterializer

// import wiki.article.request.WikiArticleRequest

// class WikiArticleResponseSpec extends AsyncFlatSpec {
//   implicit val actorSystem = ActorSystem()
//   implicit val actorMaterializer = ActorMaterializer()
//   implicit val ec = scala.concurrent.ExecutionContext.global

//   "A creation of wiki article response" should "return status OK and content given correct caption request" in {
//     val ytCaptions = YouTubeCaptions("6Af6b_wyiwI", "en")
//     val correctRequest = YouTubeCaptionsRequest(ytCaptions)

//     val correctResponse = YouTubeCaptionsResponse(correctRequest)

//     correctResponse.response map {
//       case (status: StatusCode, content: String) => {
//         assert(status == StatusCodes.OK)
//         assert(content.startsWith("<?xml version=\"1.0\" encoding=\"utf-8\" ?>"))
//       }
//     }
//   }
// }