/******************************************************************************************************************
* File:FireController.java
* Course: 17655
* Project: Assignment A2
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*	1.0 
*
* Description:
*
* This class simulates a device that controls the fire alarm. It polls the message manager for message
* ids = 10 and reacts to them by turning on or off the fire alarm. 
* 
* The state (on/off) is graphically displayed on the terminal in the indicator. Command messages are displayed in
* the message window. 
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
*
* 
*
******************************************************************************************************************/
package systemB;
import InstrumentationPackage.*;
import MessagePackage.*;

public class FireController {

	public static void main(String args[])
	{

		String MsgMgrIP;					// Message Manager IP address
		Message Msg = null;					// Message object
		MessageQueue eq = null;				// Message Queue
		StatusSender ss = new StatusSender("FC1", "This is the file controller one");
		MessageManagerInterface em = null;	// Interface object to the message manager
		boolean FireAlarmState = false;	// Heater state: false == off, true == on
		
		int	Delay = 2500;					// The loop delay (2.5 seconds)
		boolean Done = false;				// Loop termination flag
		final int EXITCODE =97;
		
		if ( args.length == 0 )
 		{
			// message manager is on the local system

			System.out.println("\n\nAttempting to register on the local machine..." );

			try
			{
				// Here we create an message manager interface object. This assumes
				// that the message manager is on the local machine

				em = new MessageManagerInterface();
			}

			catch (Exception e)
			{
				System.out.println("Error instantiating message manager interface: " + e);

			} // catch

		} else {

			// message manager is not on the local system

			MsgMgrIP = args[0];

			System.out.println("\n\nAttempting to register on the machine:: " + MsgMgrIP );

			try
			{
				// Here we create an message manager interface object. This assumes
				// that the message manager is NOT on the local machine

				em = new MessageManagerInterface( MsgMgrIP );
			}

			catch (Exception e)
			{
				System.out.println("Error instantiating message manager interface: " + e);

			} // catch

		} // if

		// Here we check to see if registration worked. If em is null then the
		// message manager interface was not properly created.

		if (em != null)
		{
			System.out.println("Registered with the message manager." );

			/* Now we create the humidity control status and message panel
			** We put this panel about 2/3s the way down the terminal, aligned to the left
			** of the terminal. The status indicators are placed directly under this panel
			*/

			float WinPosX = 0.0f; 	//This is the X position of the message window in terms
									//of a percentage of the screen height
			float WinPosY = 0.60f;	//This is the Y position of the message window in terms
								 	//of a percentage of the screen height

			MessageWindow mw = new MessageWindow("Fire Alarm Controller Status Console", WinPosX, WinPosY);

			// Now we put the indicators directly under the humitity status and control panel

			Indicator falarm = new Indicator ("FireAlarm OFF", mw.GetX(), mw.GetY()+mw.Height());
		

			mw.WriteMessage("Registered with the message manager." );

	    	try
	    	{
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );

			} // try

	    	catch (Exception e)
			{
				System.out.println("Error:: " + e);

			} // catch

			/********************************************************************
			** Here we start the main simulation loop
			*********************************************************************/
while(!Done){
	ss.PostState(em); //send working state to the StatusSender
	mw.WriteMessage("Sprinkler state:: " + FireAlarmState );
	try
	{
		eq = em.GetMessageQueue(); //get message fro queue

	} // try

	catch( Exception e )
	{
		mw.WriteMessage("Error getting message queue::" + e );

	} // catch
	
	int qlen = eq.GetSize();

	for ( int i = 0; i < qlen; i++ )
	{
		Msg = eq.GetMessage();
		
		if ( Msg.GetMessageId() == 10 )
		{
			if (Msg.GetMessage().equalsIgnoreCase("true")) // humidifier on
			{
				FireAlarmState = true;
			}
			else if (Msg.GetMessage().equalsIgnoreCase("false")){
				FireAlarmState = false;
			}
		}
	
		
		// If the message ID == EXITCODE then this is a signal that the simulation
		// is to end. At this point, the loop termination flag is set to
		// true and this process unregisters from the message manager.

		if ( Msg.GetMessageId() == EXITCODE )
		{
			Done = true;

			try
			{
				em.UnRegister();

	    	} // try

	    	catch (Exception e)
	    	{
				mw.WriteMessage("Error unregistering: " + e);

	    	} // catch

	    	mw.WriteMessage( "\n\nSimulation Stopped. \n");

			// Get rid of the indicators. The message panel is left for the
			// user to exit so they can see the last message posted.

			falarm.dispose();
			

		} // if

		
	} //for
	
	if (FireAlarmState){ //set the fire alarm indicator ON.
		falarm.SetLampColorAndMessage("FireAlarm On",3);
	}else
	{
		falarm.SetLampColorAndMessage("FireAlarm Off",0);
	}
	
	
	try
	{
		Thread.sleep( Delay );

	} // try

	catch( Exception e )
	{
		System.out.println( "Sleep error:: " + e );

	} // catch
	
	
}
}
		else {
			System.out.println("Unable to register with the message manager.\n\n" );
		
		
	}
	
}}
