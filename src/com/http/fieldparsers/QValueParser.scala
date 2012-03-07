package com.http.fieldparsers

import scala.util.parsing.combinator._
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

trait QValueParser extends JavaTokenParsers {
	val qvalue = ";" ~> "q" ~ "=" ~> floatingPointNumber
	
}