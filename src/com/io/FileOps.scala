package com.io

object FileOps {
  def fprint(file: java.io.File, append: Boolean = true)(out: java.io.FileWriter => Unit) {
    val fileWriter = new java.io.FileWriter(file, append)
    
    try {
      out(fileWriter)
    } finally {
      fileWriter.close
    }
  }
}