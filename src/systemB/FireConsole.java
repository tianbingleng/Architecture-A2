/******************************************************************************************************************
* File:FireConsole.java
* Course: 17655
* Project: Assignment A2
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*	1.0 
*
* Description: This class is the console for the museum fire control system. This process consists of two
* threads. The FireMonitor object is a thread that is started that is responsible for the monitoring and control of
* the fire systems. The main thread provides a text interface for the security guard to confirm or cancel the sprinkler
* action when there is a fire. Also, if the action is not confirmed or cancelled within 10 seconds, the sprinkler will
* go off automatically.
*
* Parameters: None
*
* Internal Methods: None
*
******************************************************************************************************************/
package systemB;
import TermioPackage.*;
import MessagePackage.*;


public class FireConsole{
	Termio UserInput = new Termio();
	private static String Option = "";				// Menu choice from user
	

    
	public static void main(String args[])
	{
		
	
	boolean Done = false;				// Main loop flag
	
	Termio UserInput2 = new Termio();
	
	FireMonitor Monitor = null;			// The environmental control system monitor
	
	
/////////////////////////////////////////////////////////////////////////////////
// Get the IP address of the message manager
/////////////////////////////////////////////////////////////////////////////////

if ( args.length != 0 )
{
// message manager is not on the local system

Monitor = new FireMonitor( args[0] );

} else {
//initialize the thread for monitoring fire.
Monitor = new FireMonitor();

} // if

if (Monitor.IsRegistered() )
{
	Monitor.start(); // Here we start the monitoring and control thread
	System.out.println( "\n\n\n\n" );
	System.out.println( "Fire Control System (FCS) Command Console: \n" );

	if (args.length != 0)
		System.out.println( "Using message manger at: " + args[0] + "\n" );
	else
		System.out.println( "Using local message manger \n" );

	System.out.println( "FireState: " + Monitor.getFireState() );
	System.out.println( "SprinklerState: " + Monitor.getSprinklerState() + "\n" );
	
	while(!Done){
		System.out.println( "X: Stop System\n" );
		System.out.print( "\n>>>> " );
		Option = UserInput2.KeyboardReadString();
		
	
	if(Option.equals("1")){ // confirm sprinkler action by security guard
		    if (Monitor.getFireState().equals("true")){
			Monitor.setSprinklerState(true);
			System.out.println("Sprinkler action confirmed");
			Option ="";
		    }
		
	}
	if(Option.equals("2")){ // cancel sprinkler action by security guard
		    if (Monitor.getFireState().equals("true")){
			 Monitor.setSprinklerState(false);
			
			 System.out.println("Sprinkler action cancelled");
			 Option ="";
		    }
	}
	if ( Option.equalsIgnoreCase( "X" ) ) //close consoles
	{
		// Here the user is done, so we set the Done flag and halt
		// the environmental control system. The monitor provides a method
		// to do this. Its important to have processes release their queues
		// with the message manager. If these queues are not released these
		// become dead queues and they collect messages and will eventually
		// cause problems for the message manager.

		Monitor.Halt();
		Done = true;
		System.out.println( "\nConsole Stopped... Exit monitor mindow to return to command prompt." );
		Monitor.Halt();

	} // if
		
}

}
}


}