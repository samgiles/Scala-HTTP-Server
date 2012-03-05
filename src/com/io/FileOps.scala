package com.io

object FileOps {
  def fprint(file: java.io.File)(out: java.io.PrintWriter => Unit) {
    val printWriter = new java.io.PrintWriter(file)
    
    try {
      out(printWriter)
    } finally {
      printWriter.close
    }
  }
}