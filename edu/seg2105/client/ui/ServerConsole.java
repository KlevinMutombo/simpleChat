package edu.seg2105.client.ui;

import java.util.Scanner;





import edu.seg2105.client.common.ChatIF;

import edu.seg2105.edu.server.backend.EchoServer;


/**
 * ServerConsole provides user interface for the server,
 * allowing input from the console to be processed by the EchoServer and displaying
 * messages back to the console.
 */

public class ServerConsole implements ChatIF {
	
	
	// Default port to listen on if no port is specified
    final public static int DEFAULT_PORT = 5555;
    
    // Instance of EchoServer which will handle server-side processing
    EchoServer echoserver; 

    // Scanner object to read input from the console
    Scanner fromConsole;
    
    // Port number used for this server instance
    public static int port; 
    
    /**
     * Constructor for ServerConsole.
     * Initializes EchoServer and sets up the console input.
     * 
     * @param port The port on which the server will listen for connections.
     */

    public ServerConsole(int port){
    	
    	
    	// Initialize EchoServer with the specified port
        echoserver = new EchoServer(port, this);

        //Read input from console
        fromConsole = new Scanner(System.in); 




    }
    
    /**
     * Method to start accepting input from the console.
     * Reads lines from the console and sends them to the EchoServer.
     */

    public void accept(){
        try
    {

      String message;

      // Infinite loop to keep reading input from the console
      
      while (true) 
      {
    	
    	// Read the next line of input from the console
        message = fromConsole.nextLine();
        
        // Pass the message to EchoServer to handle it from the server's UI.
        echoserver.handleMessageFromServerUI(message);
      }
    } 

    catch (Exception ex){
    	
      // Print an error message if an unexpected exception occurs
      System.out.println
        ("Unexpected error while reading from console!");
        }

    }
    
    /**
     * Method to display messages from the server.
     * implements ChatIF interface display method.
     * 
     * @param message The message to be displayed on the console.
     */

    public void display(String message) 
  {
    	// Prefix "SERVER MSG>" to the message and print it to the console
    System.out.println("SERVER MSG> " + message);
  }

   /**
     * Sets up the port to use, creates an instance of ServerConsole, and starts listening for connections.
     * 
     * @param args Command line arguments, where the first argument can specify the port number.
    */
    
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    
    catch(Throwable e)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
    
    // Create an instance of ServerConsole with the specified port
    ServerConsole sv = new ServerConsole(port);
    
	try 
    {
        sv.echoserver.listen();
    }
	
	catch(Exception x){
		x.printStackTrace();
		System.out.println("Unknown Error.");
	}

    
    sv.accept();

  }


}

