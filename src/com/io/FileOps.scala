package com.io
import java.io.File
import java.io.FileInputStream

object FileOps {
  def fprint(file: java.io.File, append: Boolean = true)(out: java.io.FileWriter => Unit) {
    val fileWriter = new java.io.FileWriter(file, append)
    
    try {
      out(fileWriter)
    } finally {
      fileWriter.close
    }
  }
  
  def fread(file: java.io.File): java.nio.CharBuffer = {
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
  
  def getFileContents(file: File) : Array[Byte] = {
    val inputStream = new FileInputStream(file);
    val flength: Int = file.length.toInt;
    var byteArray = new Array[Byte](flength);
    inputStream.read(byteArray);
    return byteArray;
  }
  
  
  def getExtension(file: java.io.File):String = {
    
    val extension: String = file.getName.lastIndexOf('.') match {
      case -1 => ""
      case x: Int => file.getName.substring(x + 1).toLowerCase
    }
    return extension;
  }
}