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
import com.google.gson.Gson;
import com.handsome.data.PMF;
import com.handsome.movie.Movie;



@SuppressWarnings("serial")
public class MvInfoServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(MvInfoServlet.class
			.getName());
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setCharacterEncoding("UTF-8");
		
		String id = req.getParameter("id");
		if (id == null){			
			log.severe("Cannot get Movie which mvId is " + id);
			return;
		}
	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Gson gson = new Gson();
		
		try {
			Key kId = KeyFactory.createKey(Movie.class.getSimpleName(),
					Long.parseLong(id));
			 
			try {
				
				Movie m = pm.getObjectById(Movie.class, kId);
				log.info("Movie m = pm.getObjectById(Movie.class, kId); " );
				if(m == null){
					resp.getWriter().println("m == null");
				}
				//JSONObject result = new JSONObject(m);
				//log.severe("result.toString() " + result.toString());
					
								
				resp.getWriter().println(gson.toJson(m));
				
			} catch (Exception e) {
				System.out.println("Cannot find " +id);
				resp.getWriter().println("Exception e");
				log.severe("Exception " + e);
			}

		} finally {
			pm.close();
		}		
		
		
	}



}
