package com.http.fieldparsers
import scala.collection.mutable.ListBuffer

case class LanguageTag(primaryTag: String, subTag: String)

case class AcceptLanguage(tags: List[LanguageTag], qvalue: Float)

class AcceptLanguageParser extends QValueParser {
  
  def acceptLanguage: Parser[List[AcceptLanguage]] = rep1sep(languageRange ~ opt(qvalue) ^^ { 
    case lr ~ Some(q) => AcceptLanguage(lr, q.toFloat)
    case lr ~ None => AcceptLanguage(lr, 1.0F)
  }, ",")
  
  def languageRange: Parser[List[LanguageTag]] = (
      repsep(languageTag,",") | rep("*" ^^ {
      	case wc => LanguageTag("*", "*")
      }))
  
  val wildCardTag = "*"
  
  def languageTag: Parser[LanguageTag] = tag ~ opt("-" ~ tag) ^^ {
    case t ~ Some("-" ~ st) => LanguageTag(t, st)
    case t ~ None => LanguageTag(t, "")
  }
    
  val tag = """([a-zA-Z]{1,8})""".r
  
  def parse(input: String): List[AcceptLanguage] = parseAll(acceptLanguage, input).getOrElse(Nil)
}