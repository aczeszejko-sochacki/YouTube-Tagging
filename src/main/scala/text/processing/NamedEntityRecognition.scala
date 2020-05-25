package text.processing

trait NamedEntityRecognition {
  def getUnigramEntities(text: String): List[String] =
    text
      // Fix nasty XML coding
      .replace("\n", " ")
      .replace("quot", " ")

      // Extract sentences
      .split("\\.|\\?|\\!")
      .map(_.trim.split(" "))

      // First token is proabably capitalized due to being first
      .flatMap(_.tail)

      // We do not want e.g. trailing commas
      .map(_.filter(char => char.isLetter || char == ' '))

      // The crucial condition
      .filter(token => token == token.capitalize)

      // Non-literal tokens can satisfy the above conditions
      .filter(_.matches("[A-Za-z]*"))

      // Common case handled manually
      .filterNot(token => token == "I" || token == "")

      .toList
}
