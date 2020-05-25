package main

import java.nio.file.Paths
import scala.io.StdIn.readLine

import akka.event.Logging
import akka.util.ByteString
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.http.scaladsl.Http

import main.MainFlows
import main.GlobalTypes._
import youtube.videos.{ IdProvider, WrongPathException }
import youtube.captions.flows.CaptionFlows
import com.typesafe.config.{ Config, ConfigFactory }

object Main extends App with CaptionFlows with MainFlows {
  implicit val actorSystem = ActorSystem()
  implicit val actorMaterializer = ActorMaterializer()
  implicit val context = actorSystem.dispatcher
  
  implicit val log = Logging(actorSystem, "YouTube-Tagging")

  // Import and validate configuration
  val config = ConfigFactory.load();
  implicit val lang: Language = config.getString("captionsLanguage")
  val maxOpenRequests = config.getInt("akka.http.host-connection-pool.max-open-requests")
  implicit val parallelism: Parallelism = config.getInt("parallelism")

  require(parallelism * parallelism <= maxOpenRequests, "Wrong parellelism + max-open-requests values")

  def termination = { Http().shutdownAllConnectionPools; actorSystem.terminate }

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
      .map(x => { println(x.parsedCaptions); x })
      .via(ytCaptionsParsedtoTaggedVideo)
      .via(ytTaggedVideoToYtVideoArticle)
      .via(stringToJsonString)
      .map(ByteString(_))
      .runWith(FileIO.toPath(Paths.get(saveDir + destinationFilename)))
      .foreach(_ => { log.info("Successfully downloaded the entire content"); termination })
  } catch {
    case _: WrongPathException => log.error("Wrong source path. Actor system terminated."); termination
    case e: Exception => log.error(s"Unexpected exception"); e.printStackTrace; termination
  }
}
