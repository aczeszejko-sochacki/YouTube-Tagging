package wiki.article

package object request {
  val ArticleHost = "en.wikipedia.org"
  val ArticlePath = "/w/api.php"
  
  def wikiArticleContentQuery(token: String) =
    s"action=query&titles=$token&prop=extracts&format=xml&exintro=1&explaintext"
}