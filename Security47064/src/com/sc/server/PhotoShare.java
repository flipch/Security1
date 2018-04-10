package com.sc.server;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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

	public void addPhoto(String user, ObjectInputStream clientIn, ObjectOutputStream clientOut)
			throws ClassNotFoundException, IOException {

		String userDir = "Server/".concat(user);

		File dir = new File(userDir);
		if (!dir.exists())
			dir.mkdir();

		// Get photo name.
		String photo;
		photo = (String) clientIn.readObject();

		// Prepare dir where photo and info related to it going to be stored.
		String temp = userDir.concat("/".concat(photo));
		File picFile = new File(temp);

		// Get list of all pictures.
		File[] listOfFiles = dir.listFiles();

		// If doesn't exist accept the one client is sending us
		if (!picFile.exists()) {
			clientOut.writeObject("[" + LocalDateTime.now() + "] " + "Receiving Image.");

			FileOutputStream photoOutput = new FileOutputStream(temp);
			OutputStream photoStream = new BufferedOutputStream(photoOutput);
			byte buffer[] = new byte[1024]; // 1024 bytes at a time
			int count;
			long size = (long) clientIn.readObject();
			while ((count = clientIn.read(buffer, 0, (int) (size < 1024 ? size : 1024))) > 0) {
				photoOutput.write(buffer, 0, count);
				size -= count;
				photoStream.flush();
			}

			// Log new photo and it's creation date to server photo list

			BufferedWriter writer = new BufferedWriter(new FileWriter(userDir.concat("listaFotos.txt"), true));
			writer.write(photo + ":" + LocalDateTime.now());
			writer.newLine();
			writer.close();
			clientOut.writeObject("[" + LocalDateTime.now() + "] " + "Success, received image.");
			photoStream.close();

		} else {
			System.out.println("[" + LocalDateTime.now() + "] " + "Picture already exists");
			clientOut.writeObject("[" + LocalDateTime.now() + "] " + "Picture already exists");
		}
	}
}
