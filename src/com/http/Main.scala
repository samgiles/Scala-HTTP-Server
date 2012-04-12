package com.http

object Main {
	def main(args: Array[String]): Unit = {
	  val server = new HTTPServer(8081);
	  server.run;
	}
}