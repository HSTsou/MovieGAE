package com.handsome.movie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jdo.PersistenceManager;















import com.google.appengine.api.ThreadManager;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.gson.Gson;
import com.handsome.data.PMF;
import com.handsome.parser.MovieParser;
import com.handsome.parser.MovieShowtimeParser;
import com.handsome.parser.TheaterParser;
import com.handsome.util.LocationTransformer;
import com.handsome.util.RegexUtil;


public class TestParser {

	static MovieShowtimeParser mvTimeParser;

	public static void main(String[] args) throws IOException {

		//List<String> playingList = getPlayingMvId();
		//List<String> notYetReleasedList = getNotYetReleasedMvId();
		/*
		 * for (String id : playingList) { getMvInfo(String.format(mvInfoUrl,
		 * id)); getYoutubeUrl(id); System.out.println("_____"); }
		 */
		//Movie movie = new Movie();
		
		
		 //all MV information .
		/*MovieParser mvParser = new MovieParser();
		Movie m = mvParser.getMvInfo("fren21663202");//fwjp76651135
		Gson gson = new Gson();	
		System.out.println(gson.toJson(m));
	*/
		
		
		final MovieParser mvParser = new MovieParser();
		List<String> comingSoonList =  mvParser.getComingSoonMvId();
		List<String> secondRunList =  mvParser.getSecondRunMvId();
		List<String> fristRunList = mvParser.getFristRunMvId();
		List<String> mvIdList = new ArrayList<String>();
		mvIdList.addAll(comingSoonList);
		mvIdList.addAll(secondRunList);
		mvIdList.addAll(fristRunList);
		List<String> deleteList = new ArrayList<String>();
		for (String string : mvIdList) {
			if(string.substring(0,1).contentEquals("A")){//關於影展的電影皆為A開頭
				deleteList.add(string);
			}
		}
		mvIdList.removeAll(deleteList);
		System.out.println(comingSoonList.size());
		System.out.println(secondRunList.size());
		System.out.println(fristRunList.size());
		System.out.println("- " +deleteList.size());
		System.out.println(mvIdList.size());
		
		for (String string : mvIdList) {
			Movie m = mvParser.getMovie(string);
			Gson gson = new Gson();	
			System.out.println(gson.toJson(m));
		}
		
		/*BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();       
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.DAYS, queue);
     
        for (String string : mvIdList) {
        	 final String s = string;
            executor.execute(new Runnable() {
                public void run() {
                    Movie m = mvParser.getMovie(s);
					Gson gson = new Gson();	
					System.out.println(gson.toJson(m));
                    
                }
            });
        }
        executor.shutdown();
    */
       
        
		
		
		//mvParser.getRottenTomatoesRatingByName("Another Woman", "2015");
		
		
		//TheaterParser thParser = new TheaterParser();
		//thParser.getAllAreaUrl();
		//thParser.getTheaterUrl("/showtime/a02/");		
		//thParser.getTheaterMvShowtime("/showtime/t02g04/a01/");//http://www.atmovies.com.tw/showtime/t02e03/a02/
		//thParser.getAllThTime();
		
		/*List<Theater> thL = thParser.getTheaterBasic();
		Gson g = new Gson();
		System.out.print(g.toJson(thL));
		*/
		
		
		
		/*print all mv json
		List<String> notYetReleasedList =  mvParser.getNotYetReleasedMvId();
		List<String> playingMvIdList = mvParser.getPlayingMvId();
		playingMvIdList.addAll(notYetReleasedList);//allMvId include now and future .
		System.out.println("total:"+playingMvIdList.size());
		Gson gson = new Gson();
		for (String string : playingMvIdList) {
			Movie movie = mvParser.getMovie(string);
			System.out.println(gson.toJson(movie));
		}*/
	}

	
}
