/**
 * 
 */
package com.sc.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 * @author Felipe
 *
 */
public class User {

	public String username;
	public String pw;
	public ArrayList<String> followers;
	private File followersFile;

	public User(String inUser, String inPasswd) {
		this.username = inUser;
		this.pw = inPasswd;
		this.followersFile = new File("Server/".concat(this.username).concat("/followers.txt"));
		if (!this.followersFile.exists())
			try {
				this.followersFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			Files.move(temp.toPath(), this.followersFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
