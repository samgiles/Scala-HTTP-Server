package com.http
import scala.actors.Actor

/**
 * Handles command line inputs into the server console.
 */
class ControlHandler(listener: Actor) extends Actor {
	var running = true;
	val errorCommand = """errors ([a-zA-Z]*)""".r
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
	        System.out.println("'errors <type>' outputs the Error Logs file and displays errors of the specified type\n'terminate'  Shuts down the HTTP Server\n'help'  Displays this help message");
	      }
	      case errorCommand(tagName) => {
	        System.out.println("Preparing error logs for type: " + tagName);
	        com.logging.Logger.outputErrorLog();
	        val viewer = new com.logging.viewer.LogViewer(tagName);
	        viewer.outputItems();
	      }
	      case _ => {
	        System.out.println("Unknown command: " + string + ".  Use 'help' command for list of commands");
	      }
	    }
	  }
	}
}