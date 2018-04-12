/**
 * 
 */
package com.sc.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.sc.utilities.Pair;

/**
 * @author Felipe
 *
 */
public class User {

	public String username;
	public String pw;
	public ArrayList<String> followers;
	public File followersFile;
	public PhotoCatalog pc;
	public File userDir;

	public User(String inUser, String inPasswd) {
		this.username = inUser;
		this.pw = inPasswd;
		this.userDir = new File("Server/".concat(this.username));
		this.userDir.mkdir(); // Even if it exists it's ok.
		this.pc = new PhotoCatalog(this.userDir.toString());
		this.followers = new ArrayList<String>();
		this.followersFile = new File(userDir.toPath().toString().concat("/followers.txt"));

		if (!this.followersFile.exists()) {
			try {
				this.followersFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		updateFollowers();
	}

	public void updateFollowers() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(this.followersFile));
			reader.lines().forEach(line -> this.followers.add(line));
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeFollower(String follower) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(this.followersFile));
			File temp = new File("temp.txt"); // Temporary changes with removed follower.
			BufferedWriter writer = new BufferedWriter(new FileWriter(temp));

			reader.lines().forEach(line -> {
				// For each line in followers file
				// Trim it, if the trimmed username equals to follower
				// Dont add it to new file
				String trimmed = line.trim();
				boolean isFollower = false;
				if (trimmed.equals(follower))
					isFollower = true;
				if (!isFollower)
					try {
						writer.write(line + System.getProperty("line.separator"));
					} catch (IOException e) {
						e.printStackTrace();
					}
			});
			// Temp file already altered
			// Overwrite old file.
			reader.close();
			writer.close();

			Files.move(temp.toPath(), this.followersFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			// Db updated
			// Persist in memory
			this.followers.remove(follower);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Pair<Boolean, String> addPhoto(ObjectInputStream clientIn, ObjectOutputStream clientOut) {
		try {
			return this.pc.addPhoto(clientIn, clientOut);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return new Pair<Boolean, String>(false, "Erro a adicionar foto");
	}

	public ArrayList<Photo> getPhotos() {
		return this.pc.photos;
	}

	public void sendPhotos(ObjectOutputStream clientOut, ObjectInputStream clientIn) {
		try {
			clientOut.writeObject(this.pc.size());
			for (Photo p : this.pc.photos) {
				// Send photo name.
				clientOut.writeObject(p.photo);
				File file = new File("Server/" + this.username + "/Photos/" + p.photo.substring(0,p.photo.indexOf(".")) + "/" + p.photo);
				// Send photo size in bytes.
				long size = file.length();

				// Client will now tell us if we're supposed to send this photo
				boolean supposedToSend = (boolean) clientIn.readObject();
				if (supposedToSend) {
					// Start sending.
					FileInputStream photoStream = new FileInputStream(file);
					byte buffer[] = new byte[1024];
					int count = 1024;
					clientOut.writeObject(file.length());
					while ((count = photoStream.read(buffer, 0, (int) (size < 1024 ? size : 1024))) > 0) {
						clientOut.write(buffer, 0, count);
						size -= count;
						clientOut.flush();
					}
					System.out.println("[" + LocalDateTime.now() + "] " + "Sent " + p.photo);
					photoStream.close();
				}
				// Else do nothing with this photo.

			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void addFollower(String user) {
		try {
			// Write to db
			BufferedWriter bw = new BufferedWriter(new FileWriter(this.followersFile, true));
			bw.write(user);
			bw.newLine();
			bw.close();
			// Db updated now persist in memory
			this.followers.add(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "User " + username.substring(0, 1).toUpperCase() + username.substring(1);
	}

}
