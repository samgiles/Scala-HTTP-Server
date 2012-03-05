package com.http

import scala.actors.Actor._

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
  
  val remoteAddress = socket.getRemoteSocketAddress;
  
  object ConnectionManager extends scala.actors.Actor {
    def act = {
      while(keepAlive){
        Thread.sleep(1500);
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
        }
        
        case sendResponse: SendResponse => {
          com.logging.Logger.debug("Sending response: " + sendResponse)
          close = true
        }
        
        case ForceClose => {
          close = true;
        }
        
        case _ => {
          com.logging.Logger.debug("Incorrect case message receieved in Connection Actor receive block.")
        }
      }
    }
    socket.close
  }
  
  // Self start the actors.
  this.start
  IncomingRequestHandler.start
  ConnectionManager.start
  
}