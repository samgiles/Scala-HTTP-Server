package com.http
import scala.actors.Actor

/**
 * Handles command line inputs into the server console.
 */
class ControlHandler(listener: Actor) extends Actor {
	var running = true;
	
	def act = {
	  val scanner: java.util.Scanner = new java.util.Scanner(System.in);
	  
	  while (running) {
	    System.out.print("#");
	    val string = scanner.nextLine();
	    
	    string match {
	      case "terminate" => {
	        System.out.println("Shutting down server...");
	        listener ! ServerCommands.Terminate;
	        running = false;
	      }
	      case "help" => {
	        System.out.println("'errors' outputs the Error Logs and opens the log viewer\n'terminate'  Shuts down the HTTP Server\n'help'  Displays this help message");
	      }
	      case "errors" => {
	        System.out.println("Preparing error logs, in a few moments the error view will open..\n");
	        com.logging.Logger.outputErrorLog();
	      }
	      case _ => {
	        System.out.println("Unknown command: " + string + ".  Use 'help' command for list of commands");
	      }
	    }
	  }
	}
}