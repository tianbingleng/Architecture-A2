package systemA;
import MessagePackage.*;

public class StatusSender
{
	String description;
	String name;
	
	public StatusSender(String n, String d)
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
