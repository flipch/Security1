package com.sc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

import com.sc.utilities.Pair;

/**
 * Class which is gonna run the logic for the initialized socket
 * 
 * @author Felipe
 *
 */
public class ServerThread extends Thread {

	private Socket socket = null;
	private PhotoShare server;

	ServerThread(Socket soc, PhotoShare state) {
		this.socket = soc;
		this.server = state;
		System.out.println("[" + LocalDateTime.now() + "]" + " New client");
	}

	/**
	 * Run client logic for given socket
	 */
	public void run() {
		try {
			// Set response channels
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

			String inUser = "";
			String inPasswd = "";

			try {
				// User authentication.
				inUser = (String) inStream.readObject();
				inPasswd = (String) inStream.readObject();

				//
				if (auth(inUser, inPasswd, outStream)) {
					String operacao = (String) inStream.readObject();
					switch (operacao) {
					default:

					}
				}
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			outStream.close();
			inStream.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Authenticates given username and password.
	 * Responds to client with answer.
	 * 
	 * @param inUser User to test
	 * @param inPasswd Password to test
	 * @param outStream	Client communication channel for response
	 * @return validUser ? true : false
	 * @throws IOException
	 */
	private boolean auth(String inUser, String inPasswd, ObjectOutputStream outStream) throws IOException {

		Pair<Boolean, String> response = this.server.authUser(inUser, inPasswd);
		outStream.writeChars(response.second());

		boolean validUser = response.first();
		return validUser;
	}
}