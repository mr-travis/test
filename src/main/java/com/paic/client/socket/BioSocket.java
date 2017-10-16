package com.paic.client.socket;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
/**
 * 
    * @ClassName: BioSocket
    * @Description: TODO(这里用一句话描述这个类的作用)
    * @author EX_KJKFB_OUYANGH
    * @date 2017年10月16日
    *
 */
public class BioSocket {
	
	private static final Logger bioLogger = Logger.getLogger(BioSocket.class);
	
	private String ip;
	
	private int port;
	
	private long conTimeout;
	
	private int soTimeout;

	public BioSocket(String ip, int port, long conTimeout, int soTimeout) {
		this.ip = ip;
		this.port = port;
		this.conTimeout = conTimeout;
		this.soTimeout = soTimeout;
	}
	
	
	public void send(byte[] message) {
		bioLogger.info("message : "+new String(message));
		Socket socket = null;
		BufferedOutputStream out = null;
		try {
			socket = new Socket(ip, port);
			socket.setSoTimeout(soTimeout);
			out = new BufferedOutputStream(socket.getOutputStream());
			
			out.write(message);
			out.flush();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null) {
				socket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public static byte[] generateMessage(String request) {
		byte[] bytes = request.getBytes();
		
		return bytes;
	}
	
	public static void main(String[] args) {
		BioSocket socket = new BioSocket("127.0.0.1", 9080, 3000, 4000);
		socket.send(generateMessage("hello"));
	}
}
