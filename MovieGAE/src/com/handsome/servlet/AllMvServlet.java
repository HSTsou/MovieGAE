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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handsome.data.PMF;
import com.handsome.movie.Movie;
import com.handsome.util.GsonExclusion;



@SuppressWarnings("serial")
public class AllMvServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(UpdateMovieServlet.class
			.getName());
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setCharacterEncoding("UTF-8");
		String v = req.getParameter("v");
		
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		
		try{
			Query query = pm.newQuery(Movie.class);
			query.setFilter("isOut" + " == false");
			@SuppressWarnings("unchecked")
			
			//Gson gson = new GsonBuilder().setExclusionStrategies(new GsonExclusion()).create();
			
			GsonBuilder gsonBuilder = new GsonBuilder();		
			
			if(v == null) {
				gsonBuilder.setVersion(1.0);
				log.severe("v == null");			
			}else if(v.equalsIgnoreCase("1.1")){
				gsonBuilder.setVersion(1.1);
			}else if(v.equalsIgnoreCase("1.0")){
				gsonBuilder.setVersion(1.0);
			}else{
				gsonBuilder.setVersion(1.0);
			}
			
			Gson gson = gsonBuilder.create();
			List<Movie> notOutMovies = (List<Movie>) query.execute();
			
			resp.getWriter().println(gson.toJson(notOutMovies));
		}finally{
			pm.close();
		}
		
	}
	
	

}