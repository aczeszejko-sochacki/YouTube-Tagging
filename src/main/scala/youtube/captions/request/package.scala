package youtube.captions

package object request {
  val CaptionsHost = "video.google.com"
  val CaptionsPath = "/timedtext"

  def youTubeVideoLangQuery(videoId: String, language: String) =
    s"lang=$language&v=$videoId"
}