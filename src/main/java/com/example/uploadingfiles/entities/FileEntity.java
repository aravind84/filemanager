package com.example.uploadingfiles.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import org.apache.commons.lang3.RandomStringUtils;

@Entity
public class FileEntity {

    @Id
    private String id=RandomStringUtils.randomAlphabetic(21);
    public String getId() {
		return id;
	}

	private String name;
	private Long size;
	private Date timestamp;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


}
