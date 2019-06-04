package edu.simple;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatThread extends Thread{
	private Socket sock;
	private String id;
	private BufferedReader br;
	private HashMap<String, PrintWriter> hm;
	private boolean initFlag = false;
	private boolean isBan = false;
	private ArrayList<ChatThread> result = new ArrayList<ChatThread>();
	
	//Ban word is array of String that searched.
	
	public String getusrId() {
		return id;
	}
	
	public ChatThread(Socket sock, HashMap<String, PrintWriter> hm){
		this.sock = sock;
		this.hm = hm;
		
		
		try{
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			
			id = br.readLine();
			broadcast(id + " entered.");
			System.out.println("[Server] User (" + id + ") entered.");

			synchronized(hm){
				
				hm.put(this.id, pw);
				
			}
			
			initFlag = true;
			
			
		}catch(Exception ex){
			System.out.println(ex);
		}
	} 
	
	//When we got array of User's Id, we can use these id as key value, so we'll get
	//PrintWriter Instance of each user.
	//so that we can send message to PrintWriter method that requested.
	public void send_userlist() {
		result = ChatServer.getIdArray();
		PrintWriter pw = hm.get(id);
		
		for (int i = 0; i < result.size(); i++) {
			pw.println(result.get(i).getusrId());
		}
		
		pw.println(result.size() + " " + "exists");
		pw.flush();
	}
	
	//the method that warned about ban word 
	public void send_warning() {
		PrintWriter pw = hm.get(id);
		
		pw.println ("warning : " + "You used banned word!");
		pw.flush();
	}
	
	public void manage_spam() {
		PrintWriter pw = hm.get(id);
		
		pw.println("ban word list_____");
		for (String ban : ChatServer.getBanlist()) {
			pw.println(ban);
		}
		
		pw.flush();
	}
	
	public void add_spam(String line) {
		int start = line.indexOf(" ") + 1;
		int end = line.length();
		PrintWriter pw = hm.get(id);
		
		ChatServer.getBanlist().add(line.substring(start, end));
		
		pw.println("successfully added.");
		pw.flush();
	}
	
	
	public void run(){
		try{
			String line = null;
			while((line = br.readLine()) != null){
				
				//searching which the message sending from user includes ban word
				for (String banner : ChatServer.getBanlist()) {
					if (line.indexOf(banner) > -1)
						isBan = true;
				}
				
				if (!isBan) {
					if(line.equals("/quit")) {
						ChatServer.writeBanList();
						break;
					}
					
					else if (line.equals("/spamlist"))
						manage_spam();
					
					else if (line.indexOf("/addspam") == 0) {
						add_spam(line);
					}
					
					//When user writes "/userlist", call send_userlist method
					else if (line.equals("/userlist"))
						send_userlist();
			
					else if(line.indexOf("/to ") == 0)
						sendmsg(line);
				
					else
						broadcast(id + " : " + line);
					}
				
				//When the message included ban word, call warning methods
				else {
					send_warning();
					isBan = false;
				}
				
			}
			
		}catch(Exception ex){
			System.out.println(ex);
		}finally{
			synchronized(hm){
				hm.remove(id);
			}
			broadcast(id + " exited.");
			try{
				if(sock != null)
					sock.close();
			}catch(Exception ex){}
		}
	} // run
	
	//add to show current time in sendmsg methods
	public void sendmsg(String msg){
		int start = msg.indexOf(" ") +1;
		int end = msg.indexOf(" ", start);
		
		//we implements adding message that shows time in whisper.
		long time = System.currentTimeMillis();
		SimpleDateFormat daytime = new SimpleDateFormat("[a hh:mm]");
		String tstr = daytime.format(new Date(time));
		
		if(end != -1){
			String to = msg.substring(start, end);
			String msg2 = msg.substring(end+1);
			Object obj = hm.get(to);
			if(obj != null){
				PrintWriter pw = (PrintWriter)obj;
				pw.println(id + " whisphered. : " + tstr + " " + msg2);
				pw.flush();
			} // if
		}
	} // sendmsg
	
	
	//add to show current time in broadcast methods
	public void broadcast(String msg){
		
		//we implements adding message that shows time in broadcast.
		long time = System.currentTimeMillis();
		SimpleDateFormat daytime = new SimpleDateFormat("[a hh:mm]");
		String tstr = daytime.format(new Date(time));
		
		//PrintWriter Instance that uses in current
		PrintWriter mypw = hm.get(id);
		
		synchronized(hm){
			Collection collection = hm.values();
			Iterator iter = collection.iterator();
			while(iter.hasNext()){
				PrintWriter pw = (PrintWriter)iter.next();
				
				//If the message is mine, not to print me
				if (!pw.equals(mypw)) {
					pw.println(tstr + " " + msg);
					pw.flush();
				}
			}
		}
	} // broadcast
	
}
