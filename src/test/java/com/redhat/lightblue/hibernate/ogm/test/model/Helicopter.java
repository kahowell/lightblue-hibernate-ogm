package com.redhat.lightblue.hibernate.ogm.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Indexed
public class Helicopter {	

	private String uuid;
	private String name;
	private String make;

	@Id
	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	@Field(analyze = Analyze.NO, store = Store.YES, indexNullAs = "#<NULL>#")
	@Column(name = "helicopterName")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Field(analyze = Analyze.NO, store = Store.YES, indexNullAs = "#<NULL>#")
	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	@Override
	public String toString() {
		return "Helicopter [uuid=" + uuid + ", name=" + name + ", make=" + make + "]";
	}
}