package systemB;
import InstrumentationPackage.*;
import MessagePackage.*;
import TermioPackage.Termio;

public class FireSensor{

	public static void main(String args[])
	{
		String MsgMgrIP;				// Message Manager IP address
										
		int Delay = 2500; // The loop delay (1 second)
		
		MessageManagerInterface em = null;// Interface object to the message manager
		
		boolean CurrentFireState = false;		// Current simulated ambient room temperature
		boolean Done = false;			// Loop termination flag
		Termio UserInput = new Termio();	// Termio IO Object
		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the message manager
		/////////////////////////////////////////////////////////////////////////////////

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

		// Here we check to see if registration worked. If ef is null then the
		// message manager interface was not properly created.


		if (em != null)
		{
			FileSensorStatusReporter fssr = new FileSensorStatusReporter(em);
			fssr.start();
			// We create a message window. Note that we place this panel about 1/2 across
			// and 1/3 down the screen

			float WinPosX = 0.5f; 	//This is the X position of the message window in terms
								 	//of a percentage of the screen height
			float WinPosY = 0.3f; 	//This is the Y position of the message window in terms
								 	//of a percentage of the screen height

			MessageWindow mw = new MessageWindow("Fire Sensor", WinPosX, WinPosY );

			mw.WriteMessage("Registered with the message manager." );

	    	try
	    	{
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );

			} // try

	    	catch (Exception e)
			{
				mw.WriteMessage("Error:: " + e);

			} // catch

			mw.WriteMessage("\nInitializing Fire Simulation::" );
			
			mw.WriteMessage("   Initial No fire set:: " + CurrentFireState );
			String Option = null;
			while(!Done){
				if(!CurrentFireState){
					System.out.print( "\nEnter 1 to start Fire>>> " );
					Option = UserInput.KeyboardReadString();
				}else
				{
					System.out.print( "\nEnter 2 to end Fire>>> " );
					Option = UserInput.KeyboardReadString();
				}
				
				if(Option.equals("1")){
					if(!CurrentFireState){
						CurrentFireState = true;
						PostFire(em, CurrentFireState);
						mw.WriteMessage("Current Fire State::  " + CurrentFireState);
					}
				}
				else if(Option.equals("2")){
					if(CurrentFireState){
						CurrentFireState=false;
						PostFire(em, CurrentFireState);
						mw.WriteMessage("Current Fire State::  " + CurrentFireState);
					}
				}
				try {
					Thread.sleep(Delay);
				} catch (Exception e) {
					System.out.println("Sleep error:: " + e);
				}	
			
			}
				
			}else{
				System.out.println("Unable to register with the message manager.\n\n");
			}
			
	}
	 // if

			
		
	/***************************************************************************
	* CONCRETE METHOD:: PostTemperature
	* Purpose: This method posts the specified temperature value to the
	* specified message manager. This method assumes an message ID of 1.
	*
	* Arguments: MessageManagerInterface ei - this is the messagemanger interface
	*			 where the messagxe will be posted.
	*
	*			 float temperature - this is the temp value.
	*
	* Returns: none
	*
	* Exceptions: None
	*
	***************************************************************************/

	static private void PostFire(MessageManagerInterface ei, boolean firestate )
	{
		// Here we create the message.

		Message msg = new Message( (int) 8, String.valueOf(firestate) );

		// Here we send the message to the message manager.

		try
		{
			ei.SendMessage( msg );
			
			//System.out.println( "Sent Fire Message" );

		} // try

		catch (Exception e)
		{
			System.out.println( "Error Posting Fire:: " + e );

		} // catch

	} // PostFire

} // TemperatureSensor

