package systemB;
import TermioPackage.*;
import MessagePackage.*;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
public class FireConsole{
	Termio UserInput = new Termio();
	private static String Option = "";				// Menu choice from user
	

    
	public static void main(String args[])
	{
		
	
	boolean Done = false;				// Main loop flag
	int	Delay = 2500;					// The loop delay (2.5 seconds)
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
		
	
	if(Option.equals("1")){
		    if (Monitor.getFireState().equals("true")){
			Monitor.setSprinklerState(true);
			System.out.println("Sprinkler action confirmed");
			Option ="";
		    }
		
	}
	if(Option.equals("2")){
		    if (Monitor.getFireState().equals("true")){
			 Monitor.setSprinklerState(false);
			
			 System.out.println("Sprinkler action cancelled");
			 Option ="";
		    }
	}
	if ( Option.equalsIgnoreCase( "X" ) )
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