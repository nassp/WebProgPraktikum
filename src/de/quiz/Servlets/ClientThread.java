//TODO: Broadcast senden!
//package de.quiz.Servlets;
//
//import java.io.PrintWriter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.json.JSONObject;
//
//import de.quiz.ServiceManager.ServiceManager;
//import de.quiz.UserManager.IUserManager;
//
//class ClientThread implements Runnable {
//	public void run(HttpServletRequest request,
//			HttpServletResponse response) {
//		response.setContentType("text/event-stream");
//		response.setCharacterEncoding("UTF-8");
//        PrintWriter out = response.getWriter();
//        JSONObject json = ServiceManager.getInstance().getService(IUserManager.class).getPlayerList();
//		
//        //IUser currentUser = ServiceManager.getInstance().getService(IUserManager.class).getUserById(String.valueOf(i));
//		//out.print(json);
//        int i = 0;
//        boolean data;
//        out.write("data: {\n");
//        out.write("data: \"id\": 6");
//        for(i=0;i<6;i++){
//        	if(json.has("name"+i)){
//				out.write(",\n");
//        		out.write("data: \"name"+i+"\": \""+json.get("name"+i)+"\"");
//				
//        	}
//        }
//        out.write("\n");
//        out.write("data: }\n\n");
//        
//        
//        //out.write("event: server-time\n\n"); 
//        //out.write("data: "+ i + "\n\n");
//        //System.out.println("Data Sent!!!"+i);
//       
//        out.close();
//	}
//}