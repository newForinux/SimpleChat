package edu.simple;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.io.*;

public class ChatClient {
	
	Socket sock = null;
	PrintWriter pw = null;
	BufferedReader br = null;
	boolean endflag = false;

	
	public ChatClient(String args1, String args2) {
		try {
			sock = new Socket(args1, 10001);
			pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			pw.println(args2);
			
			
		}catch(Exception ex) {
			if (!endflag)
				System.out.println(ex);
		}
		
	}

	
	
	public void start() {
		try{
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));	
			pw.flush();
			
			InputThread it = new InputThread(sock, br);
			it.start();
			String line = null;
			
			while((line = keyboard.readLine()) != null){
				pw.println(line);
				pw.flush();
				if(line.equals("/quit")){
					endflag = true;
					break;
				}
	
			}
			
			System.out.println("Connection closed.");
		
		}catch(Exception ex){
			if(!endflag)
				System.out.println(ex);
		}finally{
			try{
				if(pw != null)
					pw.close();
			}catch(Exception ex){}
			try{
				if(br != null)
					br.close();
			}catch(Exception ex){}
			try{
				if(sock != null)
					sock.close();
			}catch(Exception ex){}
		} // finally
	}
	
	
	public static void main(String[] args) {
		
		//User must enter Id and IP in each blank.
		Scanner sc = new Scanner(System.in);
		System.out.println("your name >> ");
		String str1 = sc.nextLine();
		
		System.out.println("server ip >> ");
		String str2 = sc.nextLine();
		
		ChatClient chatClient = new ChatClient(str2, str1);
		chatClient.start();
	}
}
