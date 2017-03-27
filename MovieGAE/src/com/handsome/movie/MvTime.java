package com.handsome.movie;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class MvTime {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;   
	
	@Persistent
	private long gaeMvId;	
	@Persistent
	private List<String> allMvThShowtimeList;
	@Persistent
	private Date updateDate;
	
	public Key getKey() {
		return key;
	}


	public void setKey(Key key) {
		this.key = key;
	}


	public long getGaeMvId() {
		return gaeMvId;
	}


	public void setGaeMvId(long gaeMvId) {
		this.gaeMvId = gaeMvId;
	}


	public List<String> getAllMvThShowtimeList() {
		return allMvThShowtimeList;
	}


	public void setAllMvThShowtimeList(List<String> allMvThShowtimeList) {
		this.allMvThShowtimeList = allMvThShowtimeList;
	}


	public Date getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}




	
	public MvTime() {
		
	}

}
