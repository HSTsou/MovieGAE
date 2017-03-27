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

import com.handsome.movie.Theater;

@SuppressWarnings("serial")
public class ThInfoServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ThInfoServlet.class
			.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setCharacterEncoding("UTF-8");

		String id = req.getParameter("id");
		if (id == null) {
			log.severe("Cannot get  is " + id);
			return;
		}

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Gson gson = new Gson();

		try {

			Query st = pm.newQuery(Theater.class);
			st.setFilter("thId == aThId");
			st.declareParameters("String aThId");
			List<Theater> th = (List<Theater>) st.execute("/showtime/"+id);

			if (th == null) {
				resp.getWriter().println("th null");
				return;
			}
				
			resp.getWriter().println(gson.toJson(th));
			
		} catch (Exception e) {

			System.out.println("Cannot find " + id);
			resp.getWriter().println("Exception");
			log.severe("Exception " + e);
		}

		finally {
			pm.close();
		}

	}

}
