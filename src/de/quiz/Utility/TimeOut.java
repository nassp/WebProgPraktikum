package de.quiz.Utility;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.TimerTask;

import org.apache.catalina.websocket.WsOutbound;

public class TimeOut extends TimerTask{
	
	private WsOutbound myOutbound = null;
	
	public TimeOut(WsOutbound outbound)
	{
		myOutbound = outbound;
	}

 @Override
 public void run() {
  // TODO Auto-generated method stub
	 
	 System.out.println("TimeOut!");
	 
	 String meins = "{\"id\": \"11\", \"answer\": \""
				+ String.valueOf(10)
				+ "\"}";
		CharBuffer buffer = CharBuffer.wrap(meins);
		try {
			this.myOutbound.writeTextMessage(buffer);
			this.myOutbound.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  
 }

}