package edu.simple;
import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;

public class ChatServer {
	private static ArrayList<ChatThread> IdArray = new ArrayList<ChatThread>();

	public static void main(String[] args) {
		try{
			ServerSocket server = new ServerSocket(10001);
			
			System.out.println("Waiting connection...");
			
			HashMap<String, PrintWriter> hm = new HashMap<String, PrintWriter>();
			
			while(true){
				Socket sock = server.accept();
				ChatThread chatthread = new ChatThread(sock, hm);
				
				//When other user logs in, add user's id in array list.
				IdArray.add(chatthread);
				
				chatthread.start();
				
			} // while
		}catch(Exception e){
			System.out.println(e);
		}
	} // main

	
	//return User's Id list
	public static ArrayList<ChatThread> getIdArray() {
		return IdArray;
	}
}
