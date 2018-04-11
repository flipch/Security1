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

	// Connection socket.
	private ServerSocket socket;

	public PhotoShare() {
		// Prepare the socket for listening.
		try {
			this.socket = new ServerSocket(23232);
			this.uc = new UserCatalog("Users.txt");
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
		clientOut.writeChars(this.uc.addPhoto(user, clientIn, clientOut).second());
	}

	public void checkFollower(User user, ObjectInputStream clientIn, ObjectOutputStream clientOut)
			throws ClassNotFoundException, IOException {
		String userCheck = (String) clientIn.readObject();
		Pair<Boolean, String> result = uc.checkFollower(user, userCheck);
		System.err.println("[" + LocalDateTime.now() + "] " + result.second());
		clientOut.writeChars(result.second());
	}

	public void listPhotos(User localUser, ObjectInputStream clientIn, ObjectOutputStream clientOut) {
		String userCheck;
		try {
			userCheck = (String) clientIn.readObject();
			// Prepare response
			Pair<Boolean, String> result = this.uc.checkFollower(localUser, userCheck);

			// If localUser follows userCheck
			if (result.first()) {
				// List photos
				int count = 1;
				for (Photo p : this.uc.get(userCheck).getPhotos()) {
					clientOut.writeChars(
							"Photo [" + count++ + "]:\nName " + p.photo + "\nDate created " + p.dateCreated);
				}
			} else {
				// Doesnt follow
				System.err.println("[" + LocalDateTime.now() + "] " + result.second());
				clientOut.writeChars(result.second());
			}

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendPhotos(User localUser, ObjectInputStream clientIn, ObjectOutputStream clientOut) {
		String userCheck;
		try {
			userCheck = (String) clientIn.readObject();
			// Prepare response
			Pair<Boolean, String> result = this.uc.checkFollower(localUser, userCheck);

			// If localUser follows userCheck
			if (result.first()) {
				User user = this.uc.get(userCheck);
				user.sendPhotos(clientOut);
			} else {
				// Doesnt follow
				System.err.println("[" + LocalDateTime.now() + "] " + result.second());
				clientOut.writeChars(result.second());
			}
		} catch (IOException |

				ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void unfollow(User localUser, ObjectInputStream clientIn, ObjectOutputStream clientOut) {
		try {
			String userCheck = (String) clientIn.readObject();
			Pair<Boolean, String> result = this.uc.checkFollower(localUser, userCheck);

			// If localUser follows then unfollow
			if (result.first()) {
				User user = this.uc.get(userCheck);
				user.removeFollower(localUser.username);
			} else {
				// Doesnt follow
				System.err.println("[" + LocalDateTime.now() + "] " + result.second());
				clientOut.writeChars(result.second());
			}

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void follow(User localUser, ObjectInputStream clientIn, ObjectOutputStream clientOut) {
		try {
			String userCheck = (String) clientIn.readObject();
			Pair<Boolean, String> result = this.uc.checkFollower(localUser, userCheck);

			// If localUser doesnt follow then follow
			if (!result.first()) {
				User user = this.uc.get(userCheck);
				user.addFollower(localUser.username);
			} else {
				// Already follows
				System.err.println("[" + LocalDateTime.now() + "] " + "Already follows");
				clientOut.writeChars("Already follows");
			}

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
