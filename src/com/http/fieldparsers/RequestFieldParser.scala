package com.http.fieldparsers

object RequestFieldParser {
  def apply(fieldLine: String): Any = {
    
    // Does the field Line contain an HTTP version?
    if (fieldLine.contains("HTTP/1")) { // Seems like a weak method of filtering out the request line
      // Request Line.
      var requestLineComponents = fieldLine.split("""\s""");
      val method = requestLineComponents(0);
      val uri = requestLineComponents(1);
      val httpVersion = requestLineComponents(2);
      return new RequestLine(method, uri, httpVersion);
    }

    return null;
  }
}

case class RequestLine(httpMethod: String, uri: String, httpVersion: String) {
}