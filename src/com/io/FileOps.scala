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
  
  def fread(file: java.io.File): java.nio.Buffer = {
    val fileReader = new java.io.FileReader(file);

    val out = java.nio.CharBuffer.allocate(file.length().toInt);
    try {
      fileReader.read(out)
    } finally {
      fileReader.close();
    }
    out.rewind();
    return out;
  }
}