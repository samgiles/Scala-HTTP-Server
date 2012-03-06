package com.http.fieldparsers

import scala.util.parsing.combinator._

case class AcceptHeader(mediaType: String, mediaSubType: String, qualityFactor: Float)

class AcceptHeaderParser extends QValueParser {
  def accept: Parser[List[AcceptHeader]] = rep1sep(acceptEntry, ",")
  def acceptEntry: Parser[AcceptHeader] = (
      (mediaType <~ "/") ~
      mediaSubType ~ opt(qvalue) ^^ {
        case t ~ st ~ Some(q) => AcceptHeader(t, st, q.toFloat)
        case t ~ st ~ None => AcceptHeader(t, st, 1.0F)
      })
      
  val wordRegex = """[\w+\-]*""".r
  val mediaType = wordRegex
  val mediaSubType = wordRegex

  
  def parse(input: String): List[AcceptHeader] = parseAll(accept, input).getOrElse(Nil)
}