/******************************************************************************************************************
* File:MaintenanceMonitor.java
* Description:
*
* This class is used to receive the status message and display the status of each component.
* It will automatically receive the message whose id is 1000, and extract the name, description
* and update the status of each component. Here, if 3 seconds, no status information are received,
* the components are considered disconnected.
*
******************************************************************************************************************/

package systemC;
import InstrumentationPackage.*;
import MessagePackage.*;
import java.util.*;

class MaintenanceMonitor extends Thread
{
	static public void main(String args[])
	{
		MessageManagerInterface em = null;	// Interface object to the message manager
		String MsgMgrIP = null;				// Message Manager IP address
		MessageWindow mw = null;					// This is the message window
		int failCount = 3;							// If in 5*delay time, no message receive, treat it as disconnected 
 		Message Msg = null;				// Message object
		MessageQueue eq = null;			// Message Queue
		int	Delay = 1000;				// The loop delay (1 second)
		boolean Done = false;			// Loop termination flag
		
 		if ( args.length == 0 )
 		{
			System.out.println("\n\nAttempting to register on the local machine..." );
			try
			{
				em = new MessageManagerInterface();
			}
			catch (Exception e)
			{
				System.out.println("Error instantiating message manager interface: " + e);
			}
		}
 		else
 		{
 			MsgMgrIP = args[0];
			System.out.println("\n\nAttempting to register on the machine:: " + MsgMgrIP );
			try
			{
				em = new MessageManagerInterface( MsgMgrIP );
			}
			catch (Exception e)
			{
				System.out.println("Error instantiating message manager interface: " + e);
			}
		}

		
		ArrayList<String> description = new ArrayList<String>();
		ArrayList<String> name = new ArrayList<String>();
		ArrayList<Integer> count = new ArrayList<Integer>();

		if (em != null)
		{
			mw = new MessageWindow("Maintaince Monitoring Console", (float)0.5, 0);
			while ( !Done )
			{
				try
				{
					eq = em.GetMessageQueue();
				}
				catch( Exception e )
				{
					mw.WriteMessage("Error getting message queue::" + e );
				}
				
				int qlen = eq.GetSize();
				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();
					if ( Msg.GetMessageId() == 1000 ) // status reading
					{
						String content = Msg.GetMessage();
						for(int j=0;j<content.length();j++)
						{
							if(content.charAt(j)=='#')
							{
								String s1=content.substring(0,j);
								String s2=content.substring(j+1);
								int mm;
								for(mm=0;mm<name.size();mm++)
								{
									if(name.get(mm).equals(s1)) break;
								}
								if(mm>=name.size())
								{
									name.add(s1);
									description.add(s2);
									count.add(new Integer(failCount));
								}
								else
								{
									count.set(mm, failCount);
								}
								break;
							}
						}
					}
				}

				String result="STATUS \t NAME \t DESCRIPTION \n";
				for(int i=0;i<name.size();i++)
				{
					int temp=count.get(i);
					if(temp<0)
					{
						result+=("Disconnected \t "+name.get(i)+" \t"+description.get(i)+"\n");
					}
					else
					{
						result+=("Connected \t "+name.get(i)+" \t"+description.get(i)+"\n");
					}
					count.set(i, temp-1);
				}
				mw.SetMessage(result);

				try
				{
					Thread.sleep( Delay );
				}
				catch( Exception e )
				{
					System.out.println( "Sleep error:: " + e );
				}
			}
		}
		else
		{
			System.out.println("Unable to register with the message manager.\n\n" );
		}
	}

}
