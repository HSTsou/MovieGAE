package com.handsome.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.appengine.api.datastore.Text;
import com.handsome.util.AtMoviesAssets;
import com.handsome.util.RegexUtil;

/**
 * get a specific movie's theater and showtime.
 * 
 * !!Attention!! all data is assembled by String and "|" split every
 * information. For example, "AreaName|theaterId|TheaterName(hall)|timeStr
 * 
 * @author handsome
 * 
 */
public class MovieShowtimeParser {
	final static String mvInfoUrl = "http://www.atmovies.com.tw/movie/%s/";// http://www.atmovies.com.tw/movie/fien22361509/
	final static String mainUrl = "http://www.atmovies.com.tw/"; // http://www.atmovies.com.tw/showtime/fien22361509/a02/
	String theaterUrl, mvId;
	Document doc;
	final static int TIMEOUT = 16000;
	
	
	public MovieShowtimeParser(String mvId) {
		super();
		this.mvId = mvId;
	}

	public MovieShowtimeParser() {
		// TODO Auto-generated constructor stub
	}

	// "AreaName|theaterId|TheaterName|hallName|timeStr
	public List<String> getMovieShowtime() {

		List<String> allMvTheaterTimeList = new ArrayList<String>();

		List<String> AreaTheaterUrlList;
		AtMoviesAssets asHelper = new AtMoviesAssets();
		AreaTheaterUrlList = getArea_TheaterLink(mvId);
		if (AreaTheaterUrlList.isEmpty()) {// no theater time.
			return null;
		} else {
			for (int j = 0; j < AreaTheaterUrlList.size(); j++) {
				String allMvTheaterTimeStr = "";
				String areaId = AreaTheaterUrlList.get(j);//ex:/showtime/flcn74027270/a02/
				String areaName = asHelper.getAreaNameById(areaId);
				//System.out.println("Area : "+areaName);
				allMvTheaterTimeStr += areaName;
				allMvTheaterTimeList.addAll(getMovieShowtimeListNew(areaId, areaName));				

			}
		}
		return allMvTheaterTimeList;
	}

	public List<String> getArea_TheaterLink(String mvId) {

		List<String> theaterList = null;
		Document doc;

		try {
			doc = Jsoup.connect(String.format(mvInfoUrl, mvId))
					.userAgent("Mozilla/5.0").timeout(TIMEOUT).get();
			theaterList = new ArrayList<>();

			Elements theaterLink = doc
					.select("div.movie_theater >select > option");

			for (Element e : theaterLink) {
				if (!e.attr("value").equalsIgnoreCase("")) {
					//System.out.println(e.attr("value"));
					theaterList.add(e.attr("value"));
				}
			}

		} catch (IOException e1) {

			e1.printStackTrace();
		}

		return theaterList;

	}
	//¥x¥_|/showtime/t02e09/a02/|ªO¾ô¨q®õ¼v«°||12:40
	//AreaName|theaterId|TheaterName|version|timeStr
	public List<String> getMovieShowtimeListNew(String theaterUrl, String areaName) {
		
		List<String> allMvThTimeInfoList = new ArrayList<String> ();
		String allMvThTimeInfo;
		
		try {
						
			doc = Jsoup.connect(mainUrl + theaterUrl).userAgent("Mozilla/5.0")
					.timeout(TIMEOUT).get();
			Elements theaterName = doc.select("div#filmShowtimeBlock > ul");
			
			for (Element e : theaterName) {
				String thUrl ="",thName="", version = "", time = "";
				
				Elements li  = e.select("li");
				for (Element ee : li) {
					if(ee.attr("class").equalsIgnoreCase("theaterTitle")){					
						thUrl = ee.select("a").attr("href");
						thName = ee.text();
					}else if(ee.attr("class").equalsIgnoreCase("filmVersion")){					
						version = ee.text();
										
					}else{
						if(!ee.text().equalsIgnoreCase("")){
							time += " "+ee.text();							
						}
						
					}
				}
				
				//System.out.println("thUrl: "+ thUrl);
				//System.out.println("thName: "+ thName);
				//System.out.println("version: "+ version);	
				//System.out.println("time: " +time.trim());				
				
				allMvThTimeInfo = areaName+"|"+thUrl+"|"+thName+"|"+ version+"|"+time.trim();
				
				System.out.println(allMvThTimeInfo);
				allMvThTimeInfoList.add(allMvThTimeInfo);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return allMvThTimeInfoList;
	
	}
	
	
	public List<HashMap<String, String>> getMovieShowtimeList(String theaterUrl) {

		List<HashMap<String, String>> theaterName_time_list = null;
		try {

			doc = Jsoup.connect(mainUrl + theaterUrl).userAgent("Mozilla/5.0")
					.timeout(TIMEOUT).get();
			Elements theaterName = doc
					.select("div.showtime01 > div.row-1_2 > a");

			Elements theaterShowtime = doc
					.select(" div.showtime01 > div.row-2");

			boolean isSameTheater = false;
			theaterName_time_list = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> theaterName_time_hashMap = new HashMap<String, String>();// <thName,
																								// timeStr>

			for (int i = 0; i < theaterName.size(); i++) {
				String thId = theaterName.get(i).attr("href");
				String thNameStr = theaterName.get(i).text();
				String thTimeStr = theaterShowtime.get(i).text();

				if (isSameTheater) {// when "true" express that this loop has
									// the same theaterName, So skip this time.
					isSameTheater = false;
					continue;
				}

				if (thTimeStr.contains("¡K¡÷¡K¡÷")) {// if there is more playingTime
													// at the next same
													// theaterName
					isSameTheater = true;
					thTimeStr = thTimeStr.replaceAll("¡K¡÷¡K¡÷", "");// remove this
																	// mark

					theaterName_time_hashMap.put(thId + "|" + thNameStr,
							thTimeStr + theaterShowtime.get(i + 1).text());// put
																			// TheaterName,
																			// time
																			// &
																			// next
																			// block
																			// time

				} else {
					RegexUtil rex = new RegexUtil(
							"([01]?[0-9]|2[0-3]):[0-5][0-9]");// find time
																// pattern

					if (rex.getIndexOfStr(thTimeStr) != -1) {

						int index = rex.getIndexOfStr(thTimeStr);
						String hallName = thTimeStr.substring(0, index);
						String hallTime = thTimeStr.substring(index);

						thNameStr += "|" + hallName;
						theaterName_time_hashMap.put(thId + "|" + thNameStr,
								hallTime);
						// System.out.println("!!!"+hallName+ " " +
						// hallTime);
					}
				}

			}
			theaterName_time_list.add(theaterName_time_hashMap);

			/*
			 * for (HashMap<String, String> m : theaterName_time_list) {
			 * 
			 * for (Object key : m.keySet()) { System.out.println(key + "|" +
			 * m.get(key)); } }
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}

		return theaterName_time_list;
	}

}
