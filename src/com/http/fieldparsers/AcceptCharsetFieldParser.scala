package com.http.fieldparsers

import scala.util.parsing.combinator._

case class AcceptCharset(charset: String, qvalue: Float)

class AcceptCharsetFieldParser extends QValueParser {
  
  def acceptCharset: Parser[List[AcceptCharset]] = rep1sep(charsetEntry, ",")
  
  def charsetEntry: Parser[AcceptCharset] = (
		  (charset | "*") ~ opt(qvalue) ^^ {
		    case cset ~ Some(q) => AcceptCharset(cset, q.toFloat)
		    case cset ~ None => AcceptCharset(cset, 1.0F)
		  }
  )
  
  val charsetRegex = """[a-zA-Z0-9\-]+""".r // matches a-z A-Z 0-9 and a - in a token.
  
  val charset = charsetRegex
  
  def parse(input: String): List[AcceptCharset] = parseAll(acceptCharset, input).getOrElse(Nil)
}