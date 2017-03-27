package com.handsome.movie;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Theater {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	String thId;	
	@Persistent
	String thName;
	@Persistent
	String address;
	@Persistent
	String phone;
	@Persistent
	String lat,lng  ;
	@Persistent
	List<Text> thMvShowtimeList;
	@Persistent
	Date updateTime;
	
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public List<Text> getThMvShowtimeList() {
		return thMvShowtimeList;
	}

	public void setThMvShowtimeList(List<Text> thMvShowtimeList) {
		this.thMvShowtimeList = thMvShowtimeList;
	}
	public Theater(){
		
	}
	
	public Theater(String thId, String thArea, String thName, String address,
			String phone, String lat, String lng) {
		super();
		this.thId = thId;		
		this.thName = thName;
		this.address = address;
		this.phone = phone;
		this.lat = lat;
		this.lng = lng;
		thMvShowtimeList = new ArrayList<Text>();
	}
	
	public String getThId() {
		return thId;
	}
	public void setThId(String thId) {
		this.thId = thId;
	}

	
	public String getThName() {
		return thName;
	}
	public void setThName(String thName) {
		this.thName = thName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	
}
