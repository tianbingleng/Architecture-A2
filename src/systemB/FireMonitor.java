/******************************************************************************************************************
* File:FireMonitor.java
* Course: 17655
* Project: Assignment A2
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*	1.0 
*
* Description:
*
* This class monitors the fire systems systems that control museum sprinkler and fire alarm. In addition to
* monitoring the fire sensor, the FireMonitor also triggers a call to the sprinkler controller if the fire is sensed and there is no
* output by the security guard for 10 seconds.
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
*
*
*
******************************************************************************************************************/

package systemB;
import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

public class FireMonitor extends Thread{

	private MessageManagerInterface em = null;	// Interface object to the message manager
	private String MsgMgrIP = null;				// Message Manager IP address
	
	String sprinklerState= "false";		// Current relative sprinkler state.
	String FireState = "false";	// Current Fire state as reported by the temperature sensor
	
	boolean Registered = true;					// Signifies that this class is registered with an message manager.
	MessageWindow mw = null;					// This is the message window
	Indicator sprinklerAction;								// Temperature indicator
	Indicator fireAction;
	private static final int EXITCODE = 97;

	
	long startTime=0;
	
	public FireMonitor()
	{
		// message manager is on the local system

		try
		{
			// Here we create an message manager interface object. This assumes
			// that the message manager is on the local machine

			em = new MessageManagerInterface();

		}

		catch (Exception e)
		{
			System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
			Registered = false;

		} // catch

	} //Constructor

	public FireMonitor( String MsgIpAddress )
	{
		// message manager is not on the local system

		MsgMgrIP = MsgIpAddress;

		try
		{
			// Here we create an message manager interface object. This assumes
			// that the message manager is NOT on the local machine

			em = new MessageManagerInterface( MsgMgrIP );
		}

		catch (Exception e)
		{
			System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
			Registered = false;

		} // catch

	} // Constructor

	public void run()
	{
		StatusSender ss = new StatusSender("FM1", "This is a fire monitor");
		Message Msg = null;				// Message object
		MessageQueue eq = null;			// Message Queue
		
		
		
		int	Delay = 2500;				// The loop delay (1 second)
		boolean Done = false;			// Loop termination flag
		

		if (em != null)
		{
			mw = new MessageWindow("Fire Monitoring Console", 0, 0);
			fireAction = new Indicator ("Fire UNK", mw.GetX()+ mw.Width(), 0);
			sprinklerAction = new Indicator ("Sprinkler UNK", mw.GetX()+ mw.Width(), mw.Height()/2);
			
			try
	    	{
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );

			} // try

	    	catch (Exception e)
			{
				System.out.println("Error:: " + e);

			}
			
			while(!Done){
				ss.PostState(em);
				if((System.currentTimeMillis() - startTime >= 10000) && (startTime!=0)){ //if there is fire, then check if time > 10sec
					if(FireState.equals("true") && sprinklerState.equals("false")){
						sprinklerState="true"; //switch on sprinker state
						startTime =0; //reset start time.
					}
				}
				try
				{
					eq = em.GetMessageQueue();

				} // try

				catch( Exception e )
				{
					mw.WriteMessage("Error getting message queue::" + e );

				}
				
				
				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();

					if ( Msg.GetMessageId() == 8 ) // Fire reading
					{
						try
						{
							FireState = Msg.GetMessage();
							System.out.println("fire state: "+Msg.GetMessage());
							if(FireState.equals("true")){ //Input taken at the console
								startTime = System.currentTimeMillis(); //fire sensed, start count down immediately. 
								System.out.println( "1: Confirm Sprinkler Action" );
								System.out.println( "2: Cancel Sprinkler Action" );
								System.out.println( "X: Stop System\n" );
								System.out.print( "\n>>>> " );
							}
							else{
								System.out.println( "X: Stop System\n" ); //no fire, so console should display only close
								System.out.print( "\n>>>> " );
							}
							
						} // try

						catch( Exception e )
						{
							mw.WriteMessage("Error reading fire: " + e);

						} // catch

					}
					// If the message ID == 97 then this is a signal that the simulation
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

						fireAction.dispose();
						sprinklerAction.dispose();

					}	
				
				
				}//for
				
				//write states in message window.
				mw.WriteMessage("Fire state:: " + FireState + "  Sprinkler state:: " + sprinklerState );
				
				if(FireState.equals("true")){ //switch on fire on indicator
					fireAction.SetLampColorAndMessage("Fire On", 3);					
					FireAlarm(true); //send signal to controller
					
				}else{ // set fire off and sprinkler to off; once the fire is switched off the sprinkler will automatically switch off
					startTime = 0;
					fireAction.SetLampColorAndMessage("Fire Off", 1);
					FireAlarm(false);
					sprinklerState = "false";
					sprinklerAction.SetLampColorAndMessage("Sprinkler Action off", 1);
					SprinklerControl(false);
				}
				
				if(sprinklerState.equals("true")){ //if sprinkler action confirmed send signal to sprinkler controller
					sprinklerAction.SetLampColorAndMessage("Sprinkler Action Confirmed", 3);
					SprinklerControl(true);
				}else{ // sprinkler action is false, ask sprinkler controller to not switch on sprinkler.
					sprinklerAction.SetLampColorAndMessage("Sprinkler Action off", 1);
					SprinklerControl(false);
				}
			
				// This delay slows down the sample rate to Delay milliseconds

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
		
	}
	
	public boolean IsRegistered()
	{
		return( Registered );

	} 
	//function to set the sprinker state to true and turn indicator on. Also, call function to send message to controller
	public void setSprinklerState(Boolean stateSprinkler){
		if(stateSprinkler){
			sprinklerState = "true";
			sprinklerAction.SetLampColorAndMessage("SSprinkler Action Confirmed", 3);
			SprinklerControl(true);
			startTime =0;
			
		}else{ //function to set the sprinker state to false and turn indicator off. Also, call function to send message to controller
			sprinklerState = "false";
			sprinklerAction.SetLampColorAndMessage("Sprinkler Action off", 1);
			SprinklerControl(false);
			startTime =0;
		}
			
	}
//send fire alarm message to Firecontroller
	public void FireAlarm(Boolean stateFire){
		Message msg;

		if ( stateFire )
		{
			msg = new Message( (int) 10, "true" );

		} else {

			msg = new Message( (int) 10, "false" );

		} // if

		// Here we send the message to the message manager.

		try
		{
			em.SendMessage( msg );

		} // try

		catch (Exception e)
		{
			System.out.println("Error sending fire control message:: " + e);

		} // catch

	}
	
	//function to send sprinkler on and off message to sprinkler controller.
	public void SprinklerControl(Boolean stateSprinkler){
		Message msg;

		if ( stateSprinkler )
		{
			msg = new Message( (int) 9, "true" );

		} else {

			msg = new Message( (int) 9, "false" );

		} // if

		// Here we send the message to the message manager.

		try
		{
			em.SendMessage( msg );

		} // try

		catch (Exception e)
		{
			System.out.println("Error sending sprinkler control message:: " + e);

		} // catch	
	}
	//getter method
	public String getSprinklerState() {
		return sprinklerState;
	}
	//getter method
	public String getFireState() {
		return FireState;
	}
	//send halt message for exit code received.
	public void Halt()
	{
		mw.WriteMessage( "***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***" );

		// Here we create the stop message.

		Message msg;

		msg = new Message( (int) EXITCODE, "XXX" );

		// Here we send the message to the message manager.

		try
		{
			em.SendMessage( msg );

		} // try

		catch (Exception e)
		{
			System.out.println("Error sending halt message:: " + e);

		} // catch

	}//HALT
	
}
