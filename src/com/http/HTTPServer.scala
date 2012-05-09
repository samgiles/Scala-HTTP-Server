package com.http
import scala.actors.Actor
import scala.actors._
import java.net.ServerSocket

/**
 * Main application object.
 */
class HTTPServer(val port: Int) extends Actor {
	
	var running = true;
	
	def act = {
	  while (running) {
	    receive {
	      case ServerCommands.Terminate => {
	        running = false;
	      }
	      case _ => {
	        
	      }
	    }
	  }
	  System.exit(0);
	}
	
	// set up the console handling
	val controlHandler = new ControlHandler(this);
	
	def run(): Unit = {
	  this.start;
	  controlHandler.start;
	  
	  val sock: ServerSocket = new ServerSocket(port);
	  
	  while(running) {
	    val connection = sock.accept();
	    val handler = new Connection(connection);
	    handler.start;
	  }
	  
	}
	
}