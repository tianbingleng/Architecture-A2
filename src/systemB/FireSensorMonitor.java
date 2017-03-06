package systemB;

import InstrumentationPackage.MessageWindow;
import MessagePackage.MessageManagerInterface;

public class FireSensorMonitor extends Thread{
	MessageManagerInterface em = null;
	MessageWindow mw =null;
	public FireSensorMonitor(MessageManagerInterface em1, MessageWindow mw1){
		this.em = em1;
		this.mw = mw1;
	}
	@Override
	public void run() {
	
		StatusSender ss = new StatusSender("FS1", "This is Fire Sensor One.");
		while(true){
			ss.PostState(em);
			try {
				mw.WriteMessage(ss.name);
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		
		
	}

}
