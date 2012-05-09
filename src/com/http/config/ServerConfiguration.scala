package com.http.config

/**
 * Contains details of server configuration.
 * TODO: FUTURE: Load this from file (I'm thinking JSON format)
 */
object ServerConfiguration {
  
  val maxConnectionLingerTime = 5 * (1000); // Connection linger time in milliseconds.
  
}

object MimeTypes {

val types = Map[String, String]("asp" -> "text/asp",
  "avi" ->	"video/avi",
  "bmp" ->	"image/bmp",
  "css" ->	"text/css",
  "htm" ->	"text/html",
  "html" -> "text/html",
  "htmls" -> "text/html",
  "htt" -> "text/webviewhtml",
  "htx" -> "text/html",
  "ico" -> "image/x-icon",
  "jpeg" -> "image/jpeg",
  "jpeg" -> "image/pjpeg",
  "jpg" -> "image/jpeg",
  "js" -> "application/x-javascript",
  "log" -> "text/plain",
  "x-png" -> "image/png",
  "png" -> "image/png");
  
}