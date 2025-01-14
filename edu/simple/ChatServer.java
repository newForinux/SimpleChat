package edu.simple;
import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;

public class ChatServer {
	private static ArrayList<ChatThread> IdArray = new ArrayList<ChatThread>();
	private static ArrayList<String> banlist = new ArrayList<String>();

	public static void main(String[] args) {
		
		try{
			
			readBanList();
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
			e.printStackTrace();
		}
	} // main

	public static void writeBanList() {
		File file = new File ("banlist.txt");
		
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			for (String line : banlist)
				pw.println(line);
			
			pw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readBanList() {
		FileReader rw = null;
		BufferedReader br = null;
		
		File check = new File("banlist.txt"); 
		
		if (!check.exists()) {
			try {
				check.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
		
		try {
			
			rw = new FileReader("banlist.txt");	
			br = new BufferedReader(rw);
			
			String readLine = null;
			banlist.clear();
			
			while ((readLine = br.readLine()) != null) {
				banlist.add(readLine);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {rw.close();} catch(IOException e) {}
			try {br.close();} catch(IOException e) {}
		}
	}
	
	//return User's Id list
	public static ArrayList<ChatThread> getIdArray() {
		return IdArray;
	}

	public static ArrayList<String> getBanlist() {
		return banlist;
	}
	
	
}
