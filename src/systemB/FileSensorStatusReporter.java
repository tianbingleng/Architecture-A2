package systemB;

import MessagePackage.MessageManagerInterface;

public class FileSensorStatusReporter extends Thread 
{
	MessageManagerInterface em;
	StatusSender ss = new StatusSender("FS1","This is a file sensor");
	FileSensorStatusReporter(MessageManagerInterface e)
	{
		em = e;
	}
	@Override
	public void run()
	{
		while(true)
		{
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {	}
			ss.PostState(em);
		
		}
	
	}

}
