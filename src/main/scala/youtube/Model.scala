package youtube

class YouTubeVideo(id: String)

case class YouTubeCaptions(videoId: String, language: String) extends YouTubeVideo(videoId)