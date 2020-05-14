package youtube.videos

import scala.concurrent._
import java.nio.file.{Paths, Files}

import akka.util.ByteString
import akka.stream.IOResult
import akka.stream.scaladsl._

object IdProvider {

  def sourceFromPath(path: String) = sourceFromCorrectPath(checkPath(path))

  private def sourceFromCorrectPath(path: String): Source[String, Future[IOResult]] =
    FileIO
      .fromPath(Paths.get(path))
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 16, allowTruncation = true))
      .map(_.utf8String)

  private def checkPath(path: String) = {
    val pathInstance = Paths.get(path)

    if (Files.isRegularFile(pathInstance)) path
    else throw new WrongPathException
  }
}