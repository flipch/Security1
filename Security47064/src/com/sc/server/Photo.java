/**
 * 
 */
package com.sc.server;

import java.time.LocalDateTime;

/**
 * @author Felipe
 *
 */
public class Photo {

	public String photo;
	public LocalDateTime dateCreated;

	public Photo(String photo, LocalDateTime dt) {
		this.photo = photo;
		this.dateCreated = dt;
	}

}
