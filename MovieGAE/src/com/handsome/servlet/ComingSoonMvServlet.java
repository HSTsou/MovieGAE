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



import com.google.gson.Gson;
import com.handsome.data.PMF;
import com.handsome.movie.Movie;
import com.handsome.movie.NotPlayingMovie;



@SuppressWarnings("serial")
public class ComingSoonMvServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(UpdateMovieServlet.class
			.getName());
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setCharacterEncoding("UTF-8");
		
	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		
		try{
			Query query = pm.newQuery(NotPlayingMovie.class);
			query.setFilter("isOut" + " == false");
			@SuppressWarnings("unchecked")
			List<Movie> notOutMovies = (List<Movie>) query.execute();
			Gson gson = new Gson();
			resp.getWriter().println(gson.toJson(notOutMovies));
		}finally{
			pm.close();
		}
		
	}

}