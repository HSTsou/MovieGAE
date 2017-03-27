package com.handsome.movie;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class NotPlayingMovie {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private String state;
	@Persistent
	private String atMoviesMvId;
	@Persistent
	private String mvName;
	@Persistent
	private String enName;
	@Persistent
	private String mvlength = null;
	@Persistent
	private String playingDate = null;
	@Persistent
	private String gate;
	@Persistent
	private String imgLink;
	@Persistent
	private String director = null;
	@Persistent
	private String writer = null;
	@Persistent
	private String actor = null;
	@Persistent	
	private Text  story = null;
	@Persistent
	private String IMDbMvId = null;
	@Persistent
	private String mv_IMDbMoblieUrl;
	@Persistent
	private boolean isThisWeek;
	@Persistent
	private List<Text> youtubeUrlList;
	@Persistent
	private boolean isOut;
	@Persistent
	private double IMDbRating ;
	@Persistent
	private double tomatoesRating ;
	@Persistent
	private String mv_tomatoesMoblieUrl;  

	
	public double getTomatoesRating() {
		return tomatoesRating;
	}


	public void setTomatoesRating(double tomatoesRating) {
		this.tomatoesRating = tomatoesRating;
	}


	public String getMv_tomatoesMoblieUrl() {
		return mv_tomatoesMoblieUrl;
	}


	public void setMv_tomatoesMoblieUrl(String mv_tomatoesMoblieUrl) {
		this.mv_tomatoesMoblieUrl = mv_tomatoesMoblieUrl;
	}


	
	


	public NotPlayingMovie() {
		 youtubeUrlList = new ArrayList<Text>();
	}
	
	
	public boolean isOut() {
		return isOut;
	}

	public void setOut(boolean isOut) {
		this.isOut = isOut;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getAtMoviesMvId() {
		return atMoviesMvId;
	}

	public void setAtMoviesMvId(String atMoviesMvId) {
		this.atMoviesMvId = atMoviesMvId;
	}
	

	public double getIMDbRating() {
		return IMDbRating;
	}
	


	public void setIMDbRating(double iMDbRating) {
		IMDbRating = iMDbRating;
	}

	public String getState() {
		return state;
	}

	public String getIMDbMvId() {
		return IMDbMvId;
	}

	public void setIMDbMvId(String iMDbMvId) {
		IMDbMvId = iMDbMvId;
	}

	public String getMv_IMDbMoblieUrl() {
		return mv_IMDbMoblieUrl;
	}

	public void setMv_IMDbMoblieUrl(String mv_IMDbMoblieUrl) {
		this.mv_IMDbMoblieUrl = mv_IMDbMoblieUrl;
	}

	public void setState(String state) {
		this.state = state;
	}

	

	public String getMvName() {
		return mvName;
	}

	public void setMvName(String mvName) {
		this.mvName = mvName;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getMvlength() {
		return mvlength;
	}

	public void setMvlength(String mvlength) {
		this.mvlength = mvlength;
	}

	public String getPlayingDate() {
		return playingDate;
	}

	public void setPlayingDate(String playingDate) {
		this.playingDate = playingDate;
	}

	public String getGate() {
		return gate;
	}

	public void setGate(String gate) {
		this.gate = gate;
	}

	public String getImgLink() {
		return imgLink;
	}

	public void setImgLink(String imgLink) {
		this.imgLink = imgLink;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	/*	public Text getStory() {
		return story ;
	}

	public void setStory(Text story) {
		this.story = story;
	}*/

	public String getStory() {
	    return story.getValue();
	}

	public void setStory(Text story) {
	    this.story = story;
	}
	
	public boolean isThisWeek() {
		return isThisWeek;
	}

	public void setThisWeek(boolean isThisWeek) {
		this.isThisWeek = isThisWeek;
	}

	public List<Text> getYoutubeUrlList() {
		return youtubeUrlList;
	}

	public void setYoutubeUrlList(List<Text> youtubeUrlList) {
		this.youtubeUrlList = youtubeUrlList;
	}

}
