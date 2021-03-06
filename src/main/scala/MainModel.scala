package main

import youtube.captions.flows.YtIdCaptionsParsed
import wiki.article.flows.WikiArticleParsed

case class YtVideoArticle(video: YtIdCaptionsParsed, articles: List[WikiArticleParsed])

object GlobalTypes {
  type Language = String
  type Parallelism = Int
}