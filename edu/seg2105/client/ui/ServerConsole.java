package edu.seg2105.client.ui;

import java.util.Scanner;



import edu.seg2105.client.common.ChatIF;

import edu.seg2105.edu.server.backend.EchoServer;

public class ServerConsole implements ChatIF {
    
    final public static int DEFAULT_PORT = 5555;

    EchoServer echoserver; 

    Scanner fromConsole;

    public static int port; 

    public ServerConsole(int port){

        echoserver = new EchoServer(port, this);

        fromConsole = new Scanner(System.in); 




    }

    public void accept(){
        try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        echoserver.handleMessageFromServerUI(message);
      }
    } 

    catch (Exception ex){
      System.out.println
        ("Unexpected error while reading from console!");
        }

    }

    public void display(String message) 
  {
    System.out.println("SERVER MSG> " + message);
  }

  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[1]); //Get port from command line
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	catch (NumberFormatException ne) 
    {
        port = DEFAULT_PORT;
    }

    ServerConsole sv = new ServerConsole(port);
    sv.accept();

  }


}

