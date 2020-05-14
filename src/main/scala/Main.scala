import java.nio.file.Paths
import scala.io.StdIn.readLine

import akka.event.Logging
import akka.util.ByteString
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.http.scaladsl.Http

import main.MainFlows
import youtube.videos.{ IdProvider, WrongPathException }
import youtube.captions.flows.CaptionFlows

object Main extends App with CaptionFlows with MainFlows {
  implicit val actorSystem = ActorSystem()
  implicit val actorMaterializer = ActorMaterializer()
  implicit val context = actorSystem.dispatcher
  
  implicit val log = Logging(actorSystem, "YouTube-Tagging")

  implicit val asyncParellism = 16

  // Language of captions
  implicit val lang = "en"

  try {
    println("Provide the source path (skip to set src/main/resources/exampleIds.csv)")
    val sourcePath = readLine match {
      case ""   => "src/main/resources/exampleIds.csv"
      case path => path
    }
    println("Provide the name of the destination file (destination dir is src/main/resources))")
    val (saveDir, destinationFilename) = ("src/main/resources/", readLine)

    IdProvider.sourceFromPath(sourcePath)
      .via(idToYtIdCaptionsRaw)
      .via(ytCaptionsRawToParsed)
      .via(ytCaptionsParsedtoTaggedVideo)
      .via(ytTaggedVideoToYtVideoArticle)
      .via(stringToJsonString)
      .map(ByteString(_))
      .runWith(FileIO.toPath(Paths.get(saveDir + destinationFilename)))
      .foreach(_ => {
        log.info("Successfully downloaded the entire content")
        Http().shutdownAllConnectionPools
        actorSystem.terminate
      })
  } catch {
    case e: WrongPathException => {
      log.error("Wrong source path. Actor system terminated.")
      Http().shutdownAllConnectionPools
      actorSystem.terminate
    }
    case e: Exception => {
      log.error(s"Unexpected exception: ${e.printStackTrace}")
      Http().shutdownAllConnectionPools
      actorSystem.terminate
    }
  }
}
