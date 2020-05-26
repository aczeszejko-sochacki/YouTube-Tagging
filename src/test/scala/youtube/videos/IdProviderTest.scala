package youtube.videos

import java.nio.file.NoSuchFileException
import java.lang.IllegalArgumentException

import org.scalatest.AsyncFlatSpec
import akka.stream.scaladsl.Sink
import akka.actor.ActorSystem
import akka.stream.Materializer

class IdProviderSpec extends AsyncFlatSpec {
  implicit val actorSystem = ActorSystem()
  implicit val actorMaterializer = Materializer

  "Reading ids from file" should "throw WrongPathException given nonexisting file" in {
    assertThrows[WrongPathException] {
      IdProvider.sourceFromPath("src/test/resources/blackhole").runWith(Sink.ignore)
    }
  }

 it should "throw WrongPathException given path of directory" in {
    assertThrows[WrongPathException] {
      IdProvider.sourceFromPath("src/test/resources").runWith(Sink.ignore)
    }
  }

 it should "return a source of ids as strings given correct path" in {
   IdProvider
     .sourceFromPath("src/test/resources/exampleIds.csv")
     .runWith(Sink.collection[String, List[String]])
     .map { result => assert(result == List("firstId", "secondId")) }
  }
}