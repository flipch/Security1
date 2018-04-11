/**
 * 
 */
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
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.sc.utilities.Pair;

/**
 * @author Felipe
 *
 */
public class PhotoCatalog {

	private ArrayList<Photo> photos;

	public Pair<Boolean, String> addPhoto(User user, ObjectInputStream clientIn, ObjectOutputStream clientOut)
			throws ClassNotFoundException, IOException {

		String userDir = "Server/".concat(user.username);

		File dir = new File(userDir);
		if (!dir.exists())
			dir.mkdir();

		// Get photo name.
		String photo;
		photo = (String) clientIn.readObject();

		// Prepare dir where photo and info related to it going to be stored.
		String temp = userDir.concat("/".concat(photo));
		File picFile = new File(temp);

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
			LocalDateTime dt = LocalDateTime.now();
			writer.write(photo + ":" + dt);
			writer.newLine();
			System.out.println("[" + LocalDateTime.now() + "] " + "Logged new photo {" + photo + "}");
			// Sync photo catalog

			this.photos.add(new Photo(photo, dt));

			writer.close();
			photoStream.close();
			return new Pair<Boolean,String>(true,"Success, received image.");
		} else {
			System.out.println("[" + LocalDateTime.now() + "] " + "Picture already exists");
			return new Pair<Boolean,String>(false,"Picture already exists");
		}
	}

}
