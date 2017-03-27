package com.handsome.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.gson.Gson;
import com.handsome.movie.Theater;
import com.handsome.servlet.UpdateComingSoonMovieServlet;
import com.handsome.util.LocationTransformer;

public class TheaterParser {
	private static final Logger log = Logger.getLogger(TheaterParser.class
			.getName());
	final static String TH_AREA_URL = "http://www.atmovies.com.tw/showtime/";// the top
																		// of
	// area name
	String MAIN_URL = "http://www.atmovies.com.tw";
	// List<String> areaUrlList;
	List<Theater> theaterList;
	static final int TIMEOUT = 16000;

	public TheaterParser() {

		// getAllAreaUrl();

		/*
		 * allThMvtimeList = getTheaterMvShowtime("1"); for (Text text :
		 * allThMvtimeList) { System.out.println(text.getValue()); }
		 */
	}

	/**
	 * To test the TimeParser.
	 * @return
	 */
	public List<Theater> getAllThTime() {
		theaterList = new ArrayList<Theater>();
		List<String> areaUrlList = getAllAreaUrl();
		Gson gson;
		for (String areaUrl : areaUrlList) {
			for (String thUrl : getTheaterUrl(areaUrl)) {
				List<Text> l = getTheaterMvShowtime(thUrl);
				gson = new Gson();
				System.out.println(gson.toJson(l));
				
			}

		}

		return theaterList;

	}
	
	/**20151031
	 * Main function for get all theaterInfo (including time and theater basic data.)
	 */
	public List<Theater> getTheaterBasic() {
		theaterList = new ArrayList<Theater>();
		List<String> areaUrlList = getAllAreaUrl();
		Gson gson;
		for (String areaUrl : areaUrlList) {
			for (String thUrl : getTheaterUrl(areaUrl)) {
				Theater th  = getThBasicData(thUrl);
				gson = new Gson();
				//System.out.println(gson.toJson(th));
				theaterList.add(th);
			}

		}

		return theaterList;

	}

	/** step 1
	 * example:  /showtime/a04/
	 * 
	 * @return
	 */
	public List<String> getAllAreaUrl() {
		Document doc;
		List<String> areaUrlList = new ArrayList<String>();
		try {
			doc = Jsoup.connect(TH_AREA_URL).userAgent("Mozilla/5.0")
					.timeout(TIMEOUT).get();
			Elements areaElement = doc.select("ul.theaterArea > li > a");
			for (Element element : areaElement) {				
				String url = element.attr("href");
				//System.out.println(url);
				areaUrlList.add(url);
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
		return areaUrlList;
	}

	/** step 2
	 * 
	 * @param areaUrl
	 * @return all theater url in a Area.  ex : /showtime/t02d09/a02/
	 */
	public List<String> getTheaterUrl(String areaUrl) {
		List<String> thUrl = new ArrayList<String>();
		Document doc;

		try {
			doc = Jsoup.connect(MAIN_URL + areaUrl).userAgent("Mozilla/5.0")
					.timeout(TIMEOUT).get();
			Elements eInfo = doc
					.select("ul#theaterList > li > a");

			for (Element e : eInfo) {

				if (!e.attr("href").equalsIgnoreCase("")) {
					
						//System.out.println(e.attr("href"));
						String thId = e.attr("href");// "/showtime/t02e04/a02/"
						thUrl.add(thId);
					
				}
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
		return thUrl;
	}

	/** step 3??
	 * 20151012
	 * @param thUrl
	 * @return theater basic data(no time).
	 */
	public Theater getThBasicData(String thUrl) {

		Theater th = new Theater();
		LocationTransformer lt;
		Document doc;

		try {
			doc = Jsoup.connect(MAIN_URL + thUrl).userAgent("Mozilla/5.0")
					.timeout(TIMEOUT).get();
			th.setThId(thUrl);
			/*
			 * get name
			 */
			Elements eThName = doc.select("div[style = margin-bottom:0px;border:0px solid #ccc;] > h2");

			if (!eThName.text().equalsIgnoreCase("")) {
				String thName = eThName.text();
				System.out.println(thName);
				th.setThName(thName);
			}
			/*
			 * get address, phone
			 */
			String address="", phone="";
			Elements eThInfo = doc.select("div[style = margin-bottom:0px;border:0px solid #ccc;] > ul > li");
			
			for (Element element : eThInfo) {
				if( element.text().contains("地址:")){
					String thInfo =  element.text().replaceAll("\\(地圖\\)", "");
					address = thInfo.substring(thInfo.indexOf("地址:") + 3).trim();
					th.setAddress(address);
				}
				
				if( element.text().contains("電話:")){
					phone =  element.text().substring( element.text().indexOf("電話:") + 3).trim();
					th.setPhone(phone);
				}
			}			
			
			
						
			System.out.println("address=" + address + "|phone=" + phone);
			
			

			lt = new LocationTransformer();
			String[] latlng = null;
			latlng = lt.getLocationInfoByAddress(address);
			
		
			System.out.println(address + " lat,lng  = " + latlng[0] + ","+ latlng[1]);
			th.setLat(latlng[0]);
			th.setLng(latlng[1]);
			
			/*
			 * 
			 */

		} catch (IOException e) {

			e.printStackTrace();
		} catch (JSONException e) {

			e.printStackTrace();
		}
		
		return th;
	}
/*
	/**
	 * get name, thUrl(id), phone, address, lat, lng
	 * 
	 * @return
	 
	public List<Theater> getAllThBasicInfo(String areaUrl) {
		List<Theater> thList = new ArrayList<Theater>();
		Theater th = new Theater();
		Document doc;
		LocationTransformer lt;
		try {
			doc = Jsoup.connect(mainUrl + areaUrl).userAgent("Mozilla/5.0")
					.timeout(6000).get();
			Elements eInfo = doc
					.select("div.only_text > div.row > div.col-1 > span.at11 > a, div.only_text > div.row > div.col-2,  div.only_text > div.row > div.col-3  ");
			int parseTimeForRound = 0;// one round has three elements, also has
										// three for loop.
			int time = 0;
			for (Element e : eInfo) {

				parseTimeForRound += 1;

				if (parseTimeForRound % 3 == 0) {// to reset Theater Bean
					thList.add(th);
					parseTimeForRound = 0;
					th = new Theater();
				}

				if (!e.text().equalsIgnoreCase("")) {
					time++;
					System.out.println(time);

					String str = e.text();
					if (str.contains("網站")) {
						String phone = str.replaceAll("網站|", "");
						th.setPhone(phone);
						System.out.println(phone);
					} else if (str.contains("(地圖)")) {
						String address = str.replaceAll("\\(地圖\\)", "");
						th.setAddress(address);
						lt = new LocationTransformer();
						String[] latlng = null;

						latlng = lt.getLocationInfoByAddress(address);

						th.setLat(latlng[0]);
						th.setLng(latlng[1]);
						System.out.println(address + " lat,lng " + latlng[0]
								+ ";" + latlng[1]);
					} else {

						String thName = str;
						th.setThName(thName);
						System.out.println(thName);
					}

					// get thUrl(id)
					if (!e.attr("href").equalsIgnoreCase("")) {
						System.out.println(e.attr("href"));
						// thUrlList.add(e.attr("href"));
						String thId = e.attr("href");// "/showtime/t02e04/a02/"
						th.setThId(thId);
					}
				}
			}

		} catch (IOException e) {

			e.printStackTrace();
		} catch (JSONException e1) {
		
			e1.printStackTrace();
		}
		return thList;
	}*/

	/**
	 * get String =
	 * "dateUrl!@mvId|mvName|img|length|version|time@mvId|mvName|img|length|version|time...."
	 * pattern
	 * 
	 * @param thUrl
	 * @return 
	 */
	public List<Text> getTheaterMvShowtime(String thUrl) {

		String formatThUrl =  MAIN_URL + thUrl;

		List<Text> allInfoList = new ArrayList<Text>();
		List<String> dateUrl = new ArrayList<String>();
		//System.out.println(formatThUrl);
		Document doc;
		try {
			doc = Jsoup
					.connect(formatThUrl)
					.userAgent("Mozilla/5.0").timeout(TIMEOUT).get();
			Elements thMulitDate = doc.select("div.content.content-left > div:contains(選擇日期) > a");//that's bad solution I think.
			
			//System.out.println(thMulitDate);
			/**
			 * fetch the date of this theater
			 */
			if(thMulitDate.isEmpty()){//just one date situation.
				//System.out.println("isEmpty");
				dateUrl.add(thUrl);
			}else{
				// get one theater's playing date Url (more than one day).
				for (Element date : thMulitDate) {
					//System.out.println(date.attr("href") );
					dateUrl.add(date.attr("href"));
				}
			}
			
		

			for (String url : dateUrl) {

				// System.out.println(url + "____");
				doc = Jsoup.connect(MAIN_URL + url).userAgent("Mozilla/5.0")
						.timeout(TIMEOUT).get();
				Elements eName = doc
						.select("div#theaterShowtimeBlock > ul#theaterShowtimeTable");
				//System.out.println("doc !!\n "+ eName);
				String allThMVtime = "";
				allThMVtime += url+"!";
				for (Element e : eName) {

					/**
					 * get the MV ID and title. 
					 */
					if (!e.select("li.filmTitle").isEmpty()) {//e.className().equalsIgnoreCase("filmTitle")
						String mvId = e.select("li.filmTitle > a").attr("href").substring(7,
								e.select("li.filmTitle > a").attr("href").length() - 1);
						String mvName = e.select("li.filmTitle > a").text();
						//System.out.println("mvId = "+ mvId + " title = "+ mvName );
						allThMVtime += "@" + mvId + "|" + mvName + "|";
					}

					/**
					 * MV imgUrl and length
					 */
					if (!e.select("li > ul > li").isEmpty()) {
						String imgUrl = e.select("li > ul > li > a > img").attr("src");
						String length = e.select("li > ul > li").get(1).text();
						
						if(length.length()>3){
							length = length.substring(3);//片長 ：123分						
						}
						//System.out.println("mvImg: " + imgUrl+" length: "+length);
						allThMVtime += imgUrl + "|"+ length + "|";
					}

					/**
					 * version and time
					 */
					if (!e.select("li > ul").get(1).select("li").isEmpty()) {
						String time="";
						String version="";
						String classStr = e.select("li > ul").get(1).select("li").attr("class");
						if(classStr.equalsIgnoreCase("filmVersion")){//get version
							version = e.select("li > ul").get(1).select("li.filmVersion").text();
						}
						
						//"([01]?[0-9]|2[0-3]):[0-5][0-9]" 直接用這個找出 時間 的樣式，既不用去限定 版本 和 其他戲院了 , but no use it..
						if(!classStr.equalsIgnoreCase("filmVersion")){
							//System.out.println( e.select("li > ul").get(1).select("li"));
							time = e.select("li > ul").get(1).select("li:not(.theaterElse)").text();
							time = time.replaceAll("\u00a0|☆訂票", "");
						}
						
						if(!classStr.equalsIgnoreCase("theaterElse")){//非"其他戲院"
							time = e.select("li > ul").get(1).select("li:not(.filmVersion)").select("li:not(.theaterElse)").text();
							time = time.replaceAll("\u00a0|☆訂票", "");
						}
						//System.out.println("version = "+ version + " time = " +time);
						allThMVtime += version + "|" +time ;
					}

					/*if (e.tagName().equalsIgnoreCase("ul")) {
						String time = e.text().replaceAll("\u00a0|☆訂票", "");
						// System.out.println("time = "+time);
						allThMVtime += time;
					}*/

				}

				System.out.println(allThMVtime);
				allInfoList.add(new Text(allThMVtime));
			}

		} catch (IOException e) {
			log.severe(" TheaterParserException = " + e);
			e.printStackTrace();
			return allInfoList;
		}
		
		return allInfoList;

	}

}
