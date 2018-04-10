package com.sc.server;

import java.io.File;
import java.io.IOException;
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
			System.err.println("[" + LocalDateTime.now() + "] " + "Failed creating socket 23232\nMore info:" + e.getMessage());
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
			while (true) {
				try {
					Socket client = this.socket.accept();
					ServerThread serverThread = new ServerThread(client, this);
					serverThread.start();
				} catch (Exception e) {
					e.printStackTrace();
					this.socket.close();
				}
			}
		} else {
			System.err.println("[" + LocalDateTime.now() + "] " + "Port is still closed!Exiting..");
		}
	}

	public boolean populateUsers(File users) {
		return uc.populate(users) ? true : false;
	}

	public Pair<Boolean, String> authUser(String inUser, String inPasswd) {
		return uc.authUser(inUser, inPasswd);
	}
}
