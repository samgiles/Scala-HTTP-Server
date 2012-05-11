package com.logging.viewer
import scala.xml._
import scala.io.Source
import java.util._
import java.net._
import scala.collection.mutable.{Queue, HashMap}

object XmlHelper {
	
	def importFeed(url_s: String): Node = {
	  val url: URL = new URL(url_s);	  
	  val conn = url.openConnection;
	  return XML.load(conn.getInputStream);
	}
	
	def getTagName(node: Node): String = {
	  return node.label
	}
	
	
	def getTags(tagName:String, node:Node, filter: (Node) => Boolean = null): List[Node] = {
	  var tags: List[Node] = new LinkedList[Node]();
	  
	  for (tag <- (node \\ tagName)) {
	    if (filter != null && filter(tag)) { tags.add(tag); } else if (filter == null) { tags.add(tag); }
	  }
	  
	  return tags;
	}
}