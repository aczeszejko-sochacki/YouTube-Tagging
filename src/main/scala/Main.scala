package main

import scala.concurrent.duration._
import java.nio.file.Paths
import java.lang.ArrayIndexOutOfBoundsException

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
import com.typesafe.config.ConfigFactory

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
  val articleRequestsPerSec = config.getInt("articleRequestsPerSec")

  require(parallelism * parallelism <= maxOpenRequests, "Wrong parellelism + max-open-requests values")

  def termination = { Http().shutdownAllConnectionPools; actorSystem.terminate }

  try {
    val (sourcePath, destPath) = (args(0), args(1))

    IdProvider.sourceFromPath(sourcePath)
      .via(idToYtIdCaptionsRaw)
      .via(ytCaptionsRawToParsed)
      .via(ytCaptionsParsedtoTaggedVideo)
      .throttle(articleRequestsPerSec, 1.second)
      .via(ytTaggedVideoToYtVideoArticle)
      .via(stringToJsonString)
      .map(ByteString(_))
      .runWith(FileIO.toPath(Paths.get(destPath)))
      .foreach(_ => { log.info("Successfully downloaded the entire content"); termination })
  } catch {
    case _: ArrayIndexOutOfBoundsException => log.error("Running needs exactly 2 arguments"); termination
    case _: WrongPathException             => log.error("Wrong source path. Actor system terminated."); termination
    case e: Exception                      => log.error(s"Unexpected exception"); e.printStackTrace; termination
  }
}
