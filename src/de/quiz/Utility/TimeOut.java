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

		String meins = "{\"id\": \"11\", \"answer\": \""
				+ String.valueOf(index+10) + "\"}";
		CharBuffer buffer = CharBuffer.wrap(meins);
		try {
			this.myOutbound.writeTextMessage(buffer);
			this.myOutbound.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		Question currentQuestion = Quiz.getInstance().requestQuestion(p,
//				new TimeOut(this.myOutbound, p), error);
//
//		if (currentQuestion != null) {
//			// count++;
//			System.out.println("Der Index der korrekten Antwort ist"
//					+ String.valueOf(currentQuestion.getCorrectIndex()));
//
//			long timeout = currentQuestion.getTimeout();
//			System.out.println("TimeOut vom Server: " + timeout);
//			String question = currentQuestion.getQuestion();
//			String answer1 = currentQuestion.getAnswerList().get(0);
//			String answer2 = currentQuestion.getAnswerList().get(1);
//			String answer3 = currentQuestion.getAnswerList().get(2);
//			String answer4 = currentQuestion.getAnswerList().get(3);
//
//			String meins2 = "{\"id\": \"9\", \"timeout\": \"" + timeout
//					+ "\", \"question\": \"" + question + "\", \"answer1\": \""
//					+ answer1 + "\", \"answer2\": \"" + answer2
//					+ "\", \"answer3\": \"" + answer3 + "\", \"answer4\": \""
//					+ answer4 + "\"}";
//			CharBuffer buffer2 = CharBuffer.wrap(meins2);
//
//			// CharBuffer buffer = CharBuffer.wrap("9");
//			try {
//				this.myOutbound.writeTextMessage(buffer2);
//				this.myOutbound.flush();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			System.out.println(p.getName() + ": Frage wurde versendet!");
//
//		} else {
//			System.out.println("Unerwarteter Fehler!");
//			Quiz.getInstance().setDone(p);
//
//			// for (Player p : Quiz.getInstance().getPlayerList()) {
//			// if (!p.isDone()) {
//			// return;
//			// } else {
//			// broadcastGameEnd();
//			// }
//			// }
//		}

	}

}