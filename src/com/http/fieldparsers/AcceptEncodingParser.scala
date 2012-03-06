package com.http.fieldparsers

import scala.util.parsing.combinator._

case class AcceptEncoding(encoding: String, qvalue: Float)

class AcceptEncodingParser extends QValueParser {
  def acceptEncoding: Parser[List[AcceptEncoding]] = rep1sep(encodingEntry, ",")
  
  def encodingEntry: Parser[AcceptEncoding] = (
      (encoding | "*") ~ opt(qvalue) ^^ {
        case enc ~ Some(q) => AcceptEncoding(enc, q.toFloat)
        case enc ~ None => AcceptEncoding(enc, 1.0F)
      }
  	)
  	
  val wordRegex = """[\w+\-]*""".r
  	
  val encoding = wordRegex
  
   def parse(input: String): List[AcceptEncoding] = parseAll(acceptEncoding, input).getOrElse(Nil)
}