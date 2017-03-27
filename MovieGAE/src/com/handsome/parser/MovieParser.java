package com.handsome.parser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.print.Doc;

import org.apache.tools.ant.util.regexp.RegexpUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.appengine.api.datastore.Text;
import com.handsome.movie.Movie;
import com.handsome.util.ParseDoubleUtil;
import com.handsome.util.RegexUtil;

public class MovieParser {

	//static String mainUrl = "http://www.atmovies.com.tw/";
	static String IMDbMoblieSiteUrl = "http://m.imdb.com/title/tt%s/";
	static String firstRunUrl = "http://www.atmovies.com.tw/movie/new/";
	static String secondRunUrl = "http://www.atmovies.com.tw/movie/now2/";
	static String comingSoonUrl = "http://www.atmovies.com.tw/movie/next/0/";
	//final static String MOVIE_MAIN_URL = "http://www.atmovies.com.tw/movie/%s/";
	final static String MV_INFO_URL = "http://www.atmovies.com.tw/movie/%s/";
	final static String MV_TRAILER_URL = "http://app2.atmovies.com.tw/filmMoreTrailer/%s/";
	static final int TIMEOUT = 20000;
	Movie m;
	
	public MovieParser(){
		
	}
	
	public Movie getMovie(String atMoviesId){
		m = new Movie();
		m = getMvInfo(atMoviesId );		
		return m;
	}
	
	public Movie getMvInfo(String atMoviesId ) {
		Document doc;
		Movie m = new Movie();
		
		String  mvName, enName, mvlength = null, playingDate = null, 
				gate = null, imgLink, director = null, writer = null, actor = null,
				 state,IMDbMvId = null, mv_IMDbMoblieUrl, mv_toamtoesMoblieUrl;
		double IMDbRating=0, tomatoesRating=0;		
		boolean  isOut;
		Text story = null;
		
		//SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		Date current = new Date();
		//System.out.println(sdFormat.format(current));
		m.setUpdateDate(current);
		
		String mvUrl = String.format(MV_INFO_URL, atMoviesId);
		m.setAtMoviesMvId(atMoviesId);
		
		System.out.println("*****"+atMoviesId+"******");
		
		m.setOut(false);
		
		try {
			doc = Jsoup.connect(mvUrl).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21").followRedirects(true).timeout( TIMEOUT ).ignoreHttpErrors(true)//time out 20 second
					.get();
			
			if(!doc.hasText())
				return null;
			
			/*20151031
			 * get MoiveState firstRun , secondRun , notYet
			 */
			Elements mvState = doc.select("div#section_nav > a");
			
			if(!mvState.hasText())
				return null;
			
			state = mvState.get(2).text();
			switch (state) {
			case "近期上映":
				state = "notYet";
				break;
			case "本周新片":
				state = "thisWeek";
				break;
			case "本期首輪":
				state = "firstRun";
				break;
			case "本期二輪":
				state = "secondRun";
				break;
			default:
				
				break;
			}
			//System.out.println("status: " + state);
			m.setState(state);
			
			/*1031
			 * get  mvName
			 */
			Elements mvInfo = doc.select("div.filmTitle");
						
			String[] formatMvName = mvInfo.text().split("\\s");//use the whitespace to distinguish name
			mvName = formatMvName[0];//the first one is Chinese movie title.
			//System.out.println(mvName);
			m.setMvName(mvName);

			/*1031
			 * englishName
			 *///"深夜食堂 電影版 Midnight Diner" the status of left might be happened,so not only use the whitespace to distinguish name. 
			String assembleEnName = "";
			for (int i = 1 ; i < formatMvName.length; i++) {
				//System.out.println(i+" "+formatMvName[i]);
				if(formatMvName[i].matches("[\\u4E00-\\u9FA5]+") ){//if it's still including Chinese, see above situation.
					mvName += " "+formatMvName[i];//add Chinese to  behind the mvName(Chinese Name).
					m.setMvName(mvName);
					//System.out.println(mvName);
				}else{
					
					assembleEnName += formatMvName[i]+" ";
				}				
			}
			//Elements mvEnName = doc.select("span.at12b_gray");
			//enName = mvEnName.text();
			enName = assembleEnName.trim();
			System.out.println(enName);
			m.setEnName(enName);

			/*1031
			 * get length and playingDate
			 */
			Elements mvlength_date = doc.select("ul.runtime > li");// 片長：121分&nbsp;
																				// 上映日期：2015/09/25&nbsp;
																				// 廳數
																				// (78)
			for (Element element : mvlength_date) {
				if (element.text().indexOf("片長：") != -1)
					mvlength = element.text().substring(element.text().indexOf("片長：") + 3);
				if (element.text().indexOf("上映日期：") != -1)
					playingDate = element.text().substring(element.text().indexOf("上映日期：")+5);
			}
			

			

			

			//System.out.println(mvlength + "\n" + playingDate);
			m.setMvlength(mvlength);
			m.setPlayingDate(playingDate);

			/*1031
			 * gate: 普遍G, 保護P, 輔導PG, 限制R
			 */
			Elements gateImgLink = doc.select("div.filmTitle > img ");
			//System.out.println("gateImgLink  :" +gateImgLink );
			if(!gateImgLink.isEmpty())
				gate = judgeMvGate(gateImgLink.attr("src"));
			//System.out.println("gate :" +gate);
			m.setGate(gate);

			/*1031
			 * movie imageThumbnail
			 * AtMovies pl = large, pm = middle, ps = small. Following is example:
			 * http://www.photowant.com/photo101/fmen32293640/pl_fmen32293640_0020.jpg
			 */
			Elements mvImgLink = doc.select("div#filmTagBlock > span > a>img");
			imgLink = mvImgLink.attr("src");
			//System.out.println(imgLink);
			m.setImgLink(imgLink);
			
			/*1031
			 * director,writer,actor
			 */
			Element peopleInfo = doc
					.select("div#filmCastDataBlock > ul ").first();
			String peopleStr = peopleInfo.text();
			//System.out.println(peopleStr);
		
			
			if(peopleStr.contains("導演：")){
				if(peopleStr.contains("編劇：")){
					director =  peopleStr.substring(peopleStr.indexOf("導演：")+3, peopleStr.indexOf("編劇："));
				}else if(peopleStr.contains("演員：")){
					director =  peopleStr.substring(peopleStr.indexOf("導演：")+3, peopleStr.indexOf("演員："));
				}else{
					director =  peopleStr.substring(peopleStr.indexOf("導演：")+3);
				}
				
				if(director.contains("more"))
					director = director.replace("more", "");
			}
			if(peopleStr.contains("編劇：")){
				if(peopleStr.contains("演員：")){
					writer =  peopleStr.substring(peopleStr.indexOf("編劇：")+3, peopleStr.indexOf("演員："));
				}else{
					writer =  peopleStr.substring(peopleStr.indexOf("編劇：")+3);
				}
				
				if(writer.contains("more"))
					writer =  writer.replace("more", "");
			}
			if(peopleStr.contains("演員：")){
				actor = peopleStr.substring(peopleStr.indexOf("演員：")+3);
				
				if(actor.contains("more"))
					actor = actor.replace("more", "");
			}
						
			//System.out.println("director=" + director);
			//System.out.println("writer=" + writer);
			//System.out.println("actor=" + actor);
			
			m.setDirector(director);
			m.setActor(actor);
			m.setWriter(writer);

			/*1031 ? I felt this web site will update this shit...(style...)
			 * story
			 */
			Elements storyElement = doc.select("div[style=width:90%;font-size: 1.1em;]");
			//System.out.println(storyElement.text().substring(4).trim());//skip "劇情簡介"
			story = new Text(storyElement.text().substring(4).trim());
			//System.out.println(story);
			m.setStory(story);
			
			
			/* 2015/10/31
			 * IMDbMvId, IMDbRating
			 * mobile site = http://m.imdb.com/title/"IMDbMvId"/
			 */			
			Elements IMDbElement = doc.select("div#filmCastDataBlock > ul > li > a");
			for (Element element : IMDbElement) {
				//System.out.println(element);
				String url = element.attr("href");
				//System.out.println(url);
				if(url.contains("http://us.imdb.com/")){//ex : http://us.imdb.com/Title?1305907
					IMDbMvId = url.substring(url.indexOf("?")+1);
					break;
				}
			}			
			if(IMDbMvId != null){
				//System.out.println(IMDbMvId);
				IMDbRating = getIMDbMvRating(IMDbMvId);
				mv_IMDbMoblieUrl = String.format(IMDbMoblieSiteUrl, IMDbMvId);
				//System.out.println("IMDbRating = "+ IMDbRating +"\nIMDbMoblieUrl = " +mv_IMDbMoblieUrl);
				m.setMv_IMDbMoblieUrl(mv_IMDbMoblieUrl);
				m.setIMDbRating(IMDbRating);
			}
			

			/* 2015/10/16 update
			 * RottenTomatos Rating and mvUrl   //SKIP , BUG !!! ex: FANTASTIC FOUR(2015), three different English name.
			 */
			//Elements mvYearElement = doc.select("div[style*=width:45%;float:left;border: dashed.1px #966;margin:0 10px;padding:10px;height:400px;]");
			Elements mvYearElement = doc.select("div#filmCastDataBlock > ul > li ");
			//System.out.println("!!mvYearElement = "+mvYearElement.text());
			
			int yearIndex;
			String mvYearStr, mvRightData;
			if(mvYearElement.text().contains("影片年份")){
				mvRightData = mvYearElement.text();
				yearIndex = new RegexUtil("(19|20)\\d{2}").getIndexOfStr(mvRightData);//find the index of year, EX: 影片年份：2015 which is 5. //removed directly find "2015"
				System.out.println("mvRightData = "+mvRightData);
				mvYearStr = mvRightData.substring(yearIndex, yearIndex+4);
				System.out.println("mvYearStr = "+mvYearStr);
				if(IMDbMvId != null){//if IMDb is no score on the AtsMovie web site, skip tomatoes.....lazy...ZZ
					String[] tomatoesRating_url = getRottenTomatoesRatingByName(enName, mvYearStr);
					if(tomatoesRating_url !=null){
						
						if(tomatoesRating_url[0]!=null ||!tomatoesRating_url[0].equalsIgnoreCase(""))
							m.setTomatoesRating(ParseDoubleUtil.parseDouble(tomatoesRating_url[0]));
						if(tomatoesRating_url[1]!=null ||!tomatoesRating_url[1].equalsIgnoreCase(""))
							m.setMv_tomatoesMoblieUrl(tomatoesRating_url[1]);
					}
					
				}
			}
			
			
			/* 
			 * get movieVedioUrl
			 */
			// m.setYoutubeUrlList(getYoutubeUrl(atMoviesId));//sometime the url cannot loading, 
			 m.setYoutubeUrlList(getOneYoutubeUrlInMvPage(atMoviesId));
			 
			/*
			 *  MovieShowtime TEXT.
			 *  "AreaName|theaterId|TheaterName|hallName|timeStr"
			 *  ex: 台北|/showtime/t02f23/a02/|三重幸福戲院|Ａ２廳 |10:10 14:00 17:50 21:40
			 */
			 if(!state.equalsIgnoreCase("notYet")){
				 MovieShowtimeParser mvTimeParser = new MovieShowtimeParser(atMoviesId);
			     m.setAllMvThShowtimeList(mvTimeParser.getMovieShowtime());
			 }
				
			

		} catch (IOException e1) {

			e1.printStackTrace();
		}
		
		return m;
	}
	
	/**
	 * There's two way. 
	 * The first is getting search the MVname and directly entering the MV page. 
	 * The Second is search page, comparing to name and years of published.
	 * 
	 * @param englishName  
	 * @param atMovieMvYearOfPublished
	 * @return
	 * @throws IOException
	 */
	
	public String[] getRottenTomatoesRatingByName(String englishName, String atMovieMvYearOfPublished) throws IOException{
		String rottenTomatoes = "http://www.rottentomatoes.com";
		String tomatoesSearchUrl = "http://www.rottentomatoes.com/search/?search=";
		String[] spiltName = englishName.split("\\s");
		String  patternForToamto = "";
		String tomatoMvId = null;
		String rating  ;
		String[] tomatoesRating_url = new String[2];
		
		for (String string : spiltName) {
			 patternForToamto += string+"+";
		}
		
		patternForToamto = patternForToamto.substring(0, patternForToamto.length()-1);
		System.out.println(patternForToamto);
		
		Document doc;
		doc  = Jsoup.connect(tomatoesSearchUrl + patternForToamto).userAgent("Mozilla/5.0").timeout( TIMEOUT )
				.followRedirects(true).get();
		/*
		 * if the results is the SECOND situation.
		 */
		if(doc.baseUri().contains("search")){//in the top of web site url, it's still present a search results page.
		
			
			Element searchResultsElement = doc.select("div#results_all_tab > ul#movie_results_ul > li.media.bottom_divider.clearfix > div.media-body.media-body-alt > div.nomargin.media-heading.bold > a").first();
			//System.out.println(searchResultsElement);
			
			/*
			 * if no results found.
			 */
			if(searchResultsElement == null){
				return null ;
			}
			
			Element mvYearsElement = doc.select("div#results_all_tab > ul#movie_results_ul > li.media.bottom_divider.clearfix > div.media-body.media-body-alt > div.nomargin.media-heading.bold > span.movie_year").first();
			System.out.println(mvYearsElement);
			
			/*依樣有BUG 驚奇四超人 開演與奇摩 的英文不同，上面兩者也與爛番茄不同。skip first 2015/1031 
			 * checking englishName and years of published whether are the same
			 */
			String tomatoYear = mvYearsElement.text().substring(1, 5);//(2015)
			//System.out.println(tomatoYear);
			/*
			 * if the mvName or year from two website is not the same.Then go away.
			 *///!!有BUG 華麗上班族 office 搜尋出來會顯示在第二個
			if(!atMovieMvYearOfPublished.trim().equalsIgnoreCase(tomatoYear.trim())||!searchResultsElement.text().equalsIgnoreCase(englishName) ){
				System.out.println(atMovieMvYearOfPublished.trim() +" = ?"+tomatoYear.trim() );
				return null;
			}		
			
			tomatoMvId = searchResultsElement.attr("href");
			String tomatoesMvInfoUrl = rottenTomatoes + tomatoMvId;
			//System.out.println(tomatoesMvInfoUrl);
			rating = getTomatoesRatingOntheInfoPage(tomatoesMvInfoUrl);
			//System.out.println("A status :" + rating);
			tomatoesRating_url[0] = rating;
			tomatoesRating_url[1] = tomatoesMvInfoUrl;
		}else{
			rating = getTomatoesRatingOntheInfoPage(doc);
			//System.out.println("B status :" + rating);
			tomatoesRating_url[0] = rating;
			tomatoesRating_url[1] = tomatoesSearchUrl + patternForToamto;//not verify!!!!!!!
		}
		
		return tomatoesRating_url;

	}
	
	/**
	 * directly enter info page to implement parsing.
	 * @param tomatoesMvInfoUrl
	 * @return
	 * @throws IOException
	 */
	public String getTomatoesRatingOntheInfoPage(String tomatoesMvInfoUrl) throws IOException{
		String rating = "";
		
		Document doc = Jsoup.connect(tomatoesMvInfoUrl).userAgent("Mozilla/5.0").timeout( TIMEOUT ).followRedirects(true)
				.get();
		Element ratingElement = doc.select("span.meter-value.superPageFontColor > span").first();
		//System.out.println(ratingElement);
		if(ratingElement == null){
			//System.out.println("return 0");
			return "";
		}else{
			rating = ratingElement.text().trim();
			//System.out.println(rating);
		}
		
		return rating;
	}
	
	public String getTomatoesRatingOntheInfoPage(Document doc) throws IOException{
		String rating = "";
		
		
		
		Element ratingElement = doc.select("span.meter-value.superPageFontColor > span").first();
		//System.out.println(ratingElement);
		if(ratingElement == null){
			//System.out.println("return 0");
			return "";
		}else{
			rating =  ratingElement.text().trim();
			System.out.println(rating);
		}
		
		return rating;
	}
	
	public double getIMDbMvRating(String IMDbMvId){
		double rating = 0 ;
		try {
			Document doc =  Jsoup.connect("http://www.imdb.com/title/tt"+IMDbMvId+"/").userAgent("Mozilla/5.0").timeout( TIMEOUT )
					.get();
			Elements ratingElement = doc.select("div.imdbRating > div.ratingValue > strong ");
			System.out.println("ratingElement. = " + ratingElement.text());
			if(ratingElement.text().equalsIgnoreCase("")||ratingElement.text().isEmpty()){
				System.out.println("return 0");
				return 0;
			}else{
				rating = ParseDoubleUtil.parseDouble(ratingElement.text().trim());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(rating);
		return rating;
		
	}

	

	public  List<Text> getYoutubeUrl(String id) {
		String formatUrl = String.format(MV_TRAILER_URL, id);
		List<Text> vedioLinkList = new ArrayList<Text>();
		
		Connection.Response response = null;
		
        try {
            response = Jsoup.connect(formatUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 6.0) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.46 Safari/536.5")
                    .timeout(100000)
                    .ignoreHttpErrors(true) 
                    .execute();
        } catch (IOException e) {
            //System.out.println("io - "+e);
        }
        
        if(response.statusCode()==200){
        	Document doc;    		
    		
    		try {
    			doc = Jsoup.connect(formatUrl).userAgent("Mozilla/5.0")
    					.timeout( TIMEOUT ).get();

    			Elements link = doc.select("div.content.content-left > div > iframe");
    			for (Element e : link) {
    				if (!("" + e).equalsIgnoreCase("")) {
    					String url = e.attr("src");
    					//System.out.println(url);
    					vedioLinkList.add(new Text(url));				

    				}
    			}		    			

    		} catch (IOException e) {    			
    			e.printStackTrace();    			
    		}

        }else if(response.statusCode()==404){        	
        	return getOneYoutubeUrlInMvPage(id);
        }
		
		
		
		return vedioLinkList;
	}
	//開眼太白癡，有時會連結失效，直接抓電影面一部預告連結。
    public  List<Text> getOneYoutubeUrlInMvPage(String id) {
		
		Document doc;
		List<Text> vedioLinkList = new ArrayList<Text>();
		String formatUrl = String.format(MV_INFO_URL, id);
		try {
			doc = Jsoup.connect(formatUrl).userAgent("Mozilla/5.0")
					.timeout( TIMEOUT ).get();

			Elements link = doc.select("div.video_view > iframe ");
			for (Element e : link) {
				if (!("" + e).equalsIgnoreCase("")) {
					String url = e.attr("src");
					//System.out.println(url);
					vedioLinkList.add(new Text(url));				

				}
			}		
			

		} catch (IOException e) {
			e.printStackTrace();
			
		}

		return vedioLinkList;
	}

	/*
	 * gate: 普遍G, 保護P, 輔導PG, 限制R
	 */
	static String judgeMvGate(String link) {
		String gate = null;
		String formatLink = link.substring(link.indexOf("_") + 1,
				link.length() - 4);
		// System.out.println(gate);
		switch (formatLink) {
		case "G":
			gate = "G";
			break;
		case "P":
			gate = "P";
			break;
		case "F2":
			gate = "F2";
			break;
		case "PG":
			gate = "PG";
			break;
		case "R":
			gate = "R";
			break;

		default:
			gate = formatLink ;
			break;
		}
		return gate;
	}
	
	
	
	/**
	 * 
	 * @return the coming soon movie.
	 */
	public List<String> getComingSoonMvId() {

		List<String> fristRunList = null;
		Document doc;
		String formatId;
		String id;
		try {
			doc = Jsoup.connect(comingSoonUrl).userAgent("Mozilla/5.0")
					.timeout( TIMEOUT ).get();
			Elements playingMv = doc.select("div#quickSelect > cfprocessingdirective >form > select > option");
			fristRunList = new ArrayList<>();
			
			for (Element e : playingMv) {
				id = e.attr("value");
				if(id.equalsIgnoreCase("")||id == null){
					continue;
				}				
				
				formatId = id.substring(id.indexOf("movie/") + 6, id.length() - 1);//ex: http://www.atmovies.com.tw/movie/ffen52582502/
				//System.out.println(id);
				//System.out.println(formatId);
				fristRunList.add(formatId);
			}
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		return  fristRunList ;

	}

	/**
	 * 
	 * @return the second run movie.
	 */
	public List<String> getSecondRunMvId() {

		List<String> fristRunList = null;
		Document doc;
		String formatId;
		String id;
		try {
			doc = Jsoup.connect(secondRunUrl).userAgent("Mozilla/5.0")
					.timeout( TIMEOUT ).get();
			Elements playingMv = doc.select("div#quickSelect > cfprocessingdirective >form > select > option");
			fristRunList = new ArrayList<>();
			
			for (Element e : playingMv) {
				id = e.attr("value");
				if(id.equalsIgnoreCase("")||id == null){
					continue;
				}				
				
				formatId = id.substring(id.indexOf("movie/") + 6, id.length() - 1);//ex: http://www.atmovies.com.tw/movie/ffen52582502/
				//System.out.println(id);
				//System.out.println(formatId);
				fristRunList.add(formatId);
			}
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		return  fristRunList ;

	}
	
	/**
	 * 
	 * @return the first run movie. (including this week movie.)
	 */
	public List<String> getFristRunMvId() {

		List<String> fristRunList = null;
		Document doc;
		String formatId;
		String id;
		try {
			doc = Jsoup.connect(firstRunUrl).userAgent("Mozilla/5.0")
					.timeout( TIMEOUT ).get();
			Elements playingMv = doc.select("div#quickSelect > cfprocessingdirective >form > select > option");
			fristRunList = new ArrayList<>();
			
			for (Element e : playingMv) {
				id = e.attr("value");
				if(id.equalsIgnoreCase("")||id == null){
					continue;
				}				
				
				formatId = id.substring(id.indexOf("movie/") + 6, id.length() - 1);//ex: http://www.atmovies.com.tw/movie/ffen52582502/
				//System.out.println(id);
				//System.out.println(formatId);
				fristRunList.add(formatId);
			}
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		return  fristRunList ;

	}

}
