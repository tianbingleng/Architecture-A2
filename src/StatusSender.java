/******************************************************************************************************************
* File:StatusSender.java
* Description:
*
* This class is used to send the status of each component to the maintaince
* monitor. It will used 1000 as ID, and the content includes bothe the name
* and description. The name and description are seperated with #
*
* Internal Methods:
*	public void PostState(MessageManagerInterface ei)
*
******************************************************************************************************************/

import MessagePackage.*;

public class StatusSender
{
	String description;
	String name;
	
	StatusSender(String n, String d)
	{
		description = d;
		name = n;
	}
	public void PostState(MessageManagerInterface ei)
	{
		Message msg = new Message( 1000, name + "#" + description );
		try
		{
			ei.SendMessage( msg );
			//System.out.println("111"+msg.GetMessage());
		}
		catch (Exception e)
		{
			System.out.println( "Error Posting State:: " + e );
		}
	}
}
