package com.http.fieldparsers

object ParserTests {

  def main(args: Array[String]) = {
    
    acceptCharsetTest
    
    acceptLanguageTest
    
    
  }
  
  def acceptCharsetTest(): Unit = {
    val testA = "iso-8859-5, unicode-1-1;q=0.8"
    
    val parser = new AcceptCharsetFieldParser()
    val result = parser.parse(testA)
    
    assert(result.size == 2)
    
    result.foreach(charset => println("Charset: " + charset.charset + "; Q: " + charset.qvalue))
  }
  
  def acceptLanguageTest(): Unit = {
    val testA  = "da, en-gb;q=0.8, en;q=0.7"
    val parser = new AcceptLanguageParser()
    val result = parser.parse(testA)
    
    result.foreach(languagerange => println("Tags: " + languagerange.tags + " Q: " + languagerange.qvalue))
  }
}