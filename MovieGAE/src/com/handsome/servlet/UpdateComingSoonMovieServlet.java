package com.handsome.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.handsome.data.PMF;
import com.handsome.movie.Movie;
import com.handsome.movie.NotPlayingMovie;
import com.handsome.parser.MovieParser;

/**
 * The API call urlfetch.Fetch() was cancelled because the overall HTTP request deadline was reached.
 * In GAE, the deadline time is 600 seconds. So split notYetRealeased Movie and playing Movie to two servlet.
 * @author handsome
 *
 */
@SuppressWarnings("serial")
public class UpdateComingSoonMovieServlet extends HttpServlet{
	
	private MovieParser mvParser ;
	private static final Logger log = Logger.getLogger(UpdateComingSoonMovieServlet.class
			.getName());
	
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		mvParser = new MovieParser();
		
		List<String> comingSoonList =  mvParser.getComingSoonMvId();
		
		List<String> mvIdList = new ArrayList<String>();
		mvIdList.addAll(comingSoonList);		
		List<String> deleteList = new ArrayList<String>();//remove the movie of  "A" word start
		
		for (String string : mvIdList) {
			if(string.substring(0,1).contentEquals("A")){//AtMovies web site "A" express something exhibition, not a movie.
				deleteList.add(string);
			}
		}
		mvIdList.removeAll(deleteList);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		
		try{
			Query query = pm.newQuery(Movie.class);
			query.setFilter("isOut" + " == false");
		
			List<Movie> notOutMovies = (List<Movie>) query.execute();
			System.out.println("The number of the notOutMovies in the gae is "
					+ notOutMovies.size());
			
			//check GAE's Movie is out or playing/notYetReleased. if out, setOut(true)//delete it.
			for (Movie m : notOutMovies) {
				String atMoviesId = m.getAtMoviesMvId();
				if(m.getState().equalsIgnoreCase("notYet")){
					if (!mvIdList.contains(atMoviesId)) {//express this atMoviesId's Movie turn into Playing!!
						System.out.println("GAE mv = "+atMoviesId + " was playing in the theater.");
						m.setOut(true);
						
					}else{//if exist in GAE before and not yet play, update the info of movie!
						System.out.println(atMoviesId + " movie update!");			
						mvIdList.remove(atMoviesId);
						Movie movie = mvParser.getMovie(atMoviesId);
						if(movie != null) {//do parse again to update Mv's state
							movie.setKey(m.getKey());//All info is updating, but the key.
							
												
							pm.makePersistent(movie);
						}
						
					}
				}
				
			}
			
			
			System.out.println("the number of New-Coming Movie is " + mvIdList.size());
				
			for (String mvId : mvIdList) {
				System.out.println("Inserting " + mvId + " ...");
				Movie m = insertMovie(pm, mvId);
				//if (m != null)
					//inserShowtimes(pm, m);

			}

		}finally{
			pm.close();
		}
	
		
	}
	
	private Movie insertMovie(PersistenceManager pm, String mvId) {
		Movie m = mvParser.getMovie(mvId);
		if (m == null) {
			log.severe("Cannot get Movie which mvId is " + mvId);
			System.out.println(mvId + " insert failed!");
			return null;
		} else {
			pm.makePersistent(m);
			return m;
		}
	}
}
