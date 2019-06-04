package edu.simple;
import java.io.BufferedReader;
import java.net.Socket;

public class InputThread extends Thread{
	private Socket sock = null;
	private BufferedReader br = null;
	
	public InputThread(Socket sock, BufferedReader br) {
		this.sock = sock;
		this.br = br;
	}

	public void run(){
		try{
			String line = null;
		
			//it is implemented repeatedly till there are not any string to given BufferedReader instance given parameter
			while((line = br.readLine()) != null){
				System.out.println(line);
			}
		}catch(Exception ex){
		}finally{
			try{
				if(br != null)
					br.close();
			}catch(Exception ex){}
			try{
				if(sock != null)
					sock.close();
			}catch(Exception ex){}
		}
	} // InputThread
}
