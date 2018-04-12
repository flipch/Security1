/**
 * 
 */
package com.sc.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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

	public ArrayList<Photo> photos;
	private File photosDir, photoLog;

	public PhotoCatalog(String userDir) {
		this.photosDir = new File(userDir.concat("/Photos"));
		this.photos = new ArrayList<Photo>();
		this.photoLog = new File(this.photosDir.toString().concat("/listaFotos.txt"));
		if (!this.photoLog.exists())
			try {
				this.photoLog.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if (!this.photosDir.exists()) {
			this.photosDir.mkdir();
		}
		updatePhotos();
	}

	private void updatePhotos() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(photoLog));
			reader.lines().forEach(line -> {
				String[] args = line.split(":", 2);
				this.photos.add(new Photo(args[0], args[1]));
			});

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	public Pair<Boolean, String> addPhoto(ObjectInputStream clientIn, ObjectOutputStream clientOut)
			throws ClassNotFoundException, IOException {

		// Get photo name.
		String photoName;
		photoName = (String) clientIn.readObject();

		// Prepare dir where photo is going to be stored.
		File photoDir = new File(this.photosDir + "/" + photoName.substring(0, photoName.indexOf(".")));

		if (!photoDir.exists())
			photoDir.mkdir();

		File picFile = new File(photoDir + "/" + photoName);
		// If doesn't exist accept the one client is sending us
		if (!picFile.exists()) {
			clientOut.writeObject(true);
			System.out.println("[" + LocalDateTime.now() + "] " + "Receiving Image.");

			FileOutputStream photoOutput = new FileOutputStream(picFile);
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

			BufferedWriter writer = new BufferedWriter(
					new FileWriter(this.photosDir.toString().concat("/listaFotos.txt"), true));
			LocalDateTime dt = LocalDateTime.now();
			writer.write(photoName + ":" + dt);
			writer.newLine();
			System.out.println("[" + LocalDateTime.now() + "] " + "Logged new photo {" + photoName + "}");
			// Sync photo catalog

			this.photos.add(new Photo(photoName, dt.toString()));

			writer.close();
			photoStream.close();
			return new Pair<Boolean, String>(true, "Success, received image.");
		} else {
			System.out.println("[" + LocalDateTime.now() + "] " + "Picture already exists");
			clientOut.writeObject(false);
			return new Pair<Boolean, String>(false, "Picture already exists");
		}
	}

}
