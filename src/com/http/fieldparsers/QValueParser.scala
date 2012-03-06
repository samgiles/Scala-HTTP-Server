package com.http.fieldparsers

import scala.util.parsing.combinator._

trait QValueParser extends JavaTokenParsers {
	val qvalue = ";" ~> "q" ~ "=" ~> floatingPointNumber
}