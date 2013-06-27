package de.quiz.Utility;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.TimerTask;

import org.apache.catalina.websocket.WsOutbound;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Question;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;

public class TimeOut extends TimerTask {

	private WsOutbound myOutbound = null;
	private Player p = null;
	long index = 10;
	Question quest;

	// Constructor with the outbound and player to write messages
	public TimeOut(WsOutbound outbound, Player p) {
		myOutbound = outbound;
		this.p = p;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public void setThisQuestion(Question quest) {
		this.quest = quest;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		System.out.println("TimeOut!");

		QuizError error = new QuizError();
		Quiz.getInstance().answerQuestion(p, new Long(5), error);

		// sends the right answer to the player if the question timed out with
		// the right value + 10 to differ from a normal answer
		String meins = "{\"id\": \"11\", \"answer\": \""
				+ String.valueOf(index + 10) + "\"}";
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