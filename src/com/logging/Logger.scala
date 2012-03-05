package com.logging

object Logger {
  
  val errorFile = new java.io.File("error_log.log")
  val debugFile = new java.io.File("debug_log.log")
  
  def error(error: String): Unit = {
    write(errorFile, error)
  }
  
  def debug(debug: String): Unit = {
    write(debugFile, debug)
  }
  
  private def write(file: java.io.File, message: String): Unit = {
    com.io.FileOps.fprint(file)(out => {
      out.println(new java.util.Date().toString + " - " + message)
    })
  }
  
}