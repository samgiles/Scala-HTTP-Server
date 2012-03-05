package com.http

import scala.actors.Actor._
import com.http.fieldparsers._

case class ReceivedLine(line: String)
case class SendResponse(response: String)
case object ForceClose

/**
 * A connection.
 * 
 * See <a href="http://ftp.ics.uci.edu/pub/ietf/http/draft-ietf-http-connection-00.txt">HTTP Connection Management</a> for information on HTTP Connection Management
 */
class Connection(socket: java.net.Socket) extends scala.actors.Actor {
  
  /**
   * The time the connection was created.
   */
  val creationTime = java.lang.System.currentTimeMillis();
  
  /**
   * Performs a check to see whether this connection has been open for too long.
   */
  def keepAlive(): Boolean = ((java.lang.System.currentTimeMillis() - creationTime) < com.http.config.ServerConfiguration.maxConnectionLingerTime)
  
  val remoteAddress = socket.getRemoteSocketAddress
  
  com.logging.Logger.debug("Incoming connection from: " + remoteAddress);
  
  object ConnectionManager extends scala.actors.Actor {
    def act = {
      val sleepTime = com.http.config.ServerConfiguration.maxConnectionLingerTime;  // rather than the thread blasting itself polling the time do it only once.
      while(keepAlive){
        Thread.sleep(sleepTime);
      }
      Connection.this ! ForceClose
    }
  }
  
  object IncomingRequestHandler extends scala.actors.Actor {
    
    def act = {
      if (!socket.isClosed) {
    	val istream = socket.getInputStream
      	val reader = new java.io.BufferedReader(new java.io.InputStreamReader(istream))
        
    	def readLine: String = {
    	  var line: String = null;
    	  try {  // We could be blocked waiting for the next line when the connection is closed.
    		  line = reader.readLine
    	  } catch {
    	    case e: java.net.SocketException => {
    	      line = null
    	    }
    	  }
    	  return line;
    	}
    	
      	var line = readLine;
    	
      	while(line != null && !socket.isClosed) {
    	  Connection.this ! ReceivedLine(line)
    	  line = readLine;
      	}
      }
    }
  }
  
  def act = {
    var close = false
    while(!close) {
      // Receive Incoming connection data
      receive {
        case requestLine: ReceivedLine => {
          // We Received a line from the client!
          com.logging.Logger.debug("Receieved: " + requestLine.line)
          RequestFieldParser(requestLine.line) match {
            case request: RequestLine => {
             
            }
            case _ => {
              
            }
          }
        }
        
        case respond: SendResponse => {
          com.logging.Logger.debug("Sending response: " + respond.response)
          sendResponse(respond.response)
          close = true
        }
        
        case ForceClose => {
          sendResponse("HTTP/1.1 404 Not Found\n\r");
          close = true;
        }
        
        case _ => {
          com.logging.Logger.debug("Incorrect case message receieved in Connection Actor receive block.")
        }
      }
    }
    socket.close
  }
  
  
  def sendResponse(response: String): Unit = {
    
    val os = socket.getOutputStream
    val out = new java.io.PrintStream(os)
    
    out.print(response);
    out.close
  }
  
  // Self start the actors.
  this.start
  IncomingRequestHandler.start
  ConnectionManager.start
  
}