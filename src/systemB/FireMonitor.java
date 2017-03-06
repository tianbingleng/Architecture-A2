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
			sprinklerAction = new Indicator ("Sprinkler UNK", mw.GetX()+ mw.Width(), 0);
			
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
				if((System.currentTimeMillis() - startTime >= 10000) && (startTime!=0)){
					if(FireState.equals("true") && sprinklerState.equals("false")){
						sprinklerState="true";
						startTime =0;
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
							if(FireState.equals("true")){
								startTime = System.currentTimeMillis();
								System.out.println( "1: Confirm Sprinkler Action" );
								System.out.println( "2: Cancel Sprinkler Action" );
								System.out.println( "X: Stop System\n" );
								System.out.print( "\n>>>> " );
							}
							else{
								System.out.println( "X: Stop System\n" );
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
				
				
				mw.WriteMessage("Fire state:: " + FireState + "  Sprinkler state:: " + sprinklerState );
				
				if(FireState.equals("true")){
					fireAction.SetLampColorAndMessage("Fire On", 3);					
					FireAlarm(true);
					
				}else{ // set fire off and sprinkler to off
					startTime = 0;
					fireAction.SetLampColorAndMessage("Fire Off", 1);
					FireAlarm(false);
					sprinklerState = "false";
					sprinklerAction.SetLampColorAndMessage("Sprinkler Action off", 1);
					SprinklerControl(false);
				}
				
				if(sprinklerState.equals("true")){
					sprinklerAction.SetLampColorAndMessage("Sprinkler Action Confirmed", 3);
					SprinklerControl(true);
				}else{
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
	
	public void setSprinklerState(Boolean stateSprinkler){
		if(stateSprinkler){
			sprinklerState = "true";
			sprinklerAction.SetLampColorAndMessage("SSprinkler Action Confirmed", 3);
			SprinklerControl(true);
			startTime =0;
			
		}else{
			sprinklerState = "false";
			sprinklerAction.SetLampColorAndMessage("Sprinkler Action off", 1);
			SprinklerControl(false);
			startTime =0;
		}
			
	}

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

	public String getSprinklerState() {
		return sprinklerState;
	}

	public String getFireState() {
		return FireState;
	}
	
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
