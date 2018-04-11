package com.sc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

import com.sc.utilities.Pair;

/**
 * Server class
 * 
 * @author Felipe
 *
 */
public class PhotoShare {

	// State of PhotoShare server.
	private UserCatalog uc;
	private PhotoCatalog pc;

	// Connection socket.
	private ServerSocket socket;

	public PhotoShare() {
		// Prepare the socket for listening.
		try {
			this.socket = new ServerSocket(23232);
		} catch (IOException e) {
			System.err.println(
					"[" + LocalDateTime.now() + "] " + "Failed creating socket 23232\nMore info:" + e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * Starts listening for connections and handles accepting logic
	 * 
	 * @throws IOException
	 */
	public void startListening() throws IOException {
		if (this.socket.getLocalPort() != -1) {
			try {
				Socket client = this.socket.accept();
				ServerThread serverThread = new ServerThread(client, this);
				serverThread.start();
			} catch (Exception e) {
				e.printStackTrace();
				this.socket.close();
			}

		} else {
			System.err.println("[" + LocalDateTime.now() + "] " + "Port is still closed!Exiting..");
		}
	}

	public boolean populateUsers(String users) {
		return uc.populate(users) ? true : false;
	}

	public Pair<Boolean, String> authUser(String inUser, String inPasswd) {
		return uc.authUser(inUser, inPasswd);
	}

	public void addPhoto(User user, ObjectInputStream clientIn, ObjectOutputStream clientOut)
			throws ClassNotFoundException, IOException {
		pc.addPhoto(user, clientIn, clientOut);
	}

	public void checkFollower(User user, ObjectInputStream clientIn, ObjectOutputStream clientOut)
			throws ClassNotFoundException, IOException {
		String userCheck = (String) clientIn.readObject();
		Pair<Boolean, String> result = uc.checkFollower(user, userCheck);
		System.err.println("[" + LocalDateTime.now() + "] " + result.second());
		clientOut.writeChars(result.second());
		;

	}

}
