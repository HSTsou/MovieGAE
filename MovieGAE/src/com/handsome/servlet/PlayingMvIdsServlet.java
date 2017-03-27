package com.handsome.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.handsome.data.PMF;
import com.handsome.movie.Movie;


@SuppressWarnings("serial")
public class PlayingMvIdsServlet extends HttpServlet {
	@SuppressWarnings("unchecked")
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query q = pm.newQuery(Movie.class);
			q.setFilter("isOut == false");
			List<Movie> list = (List<Movie>) q.execute();
			List<String> idList = new ArrayList<String>();
			for(Movie m : list) {
				idList.add(m.getKey().getId()+"");
			}
			JSONArray result = new JSONArray(idList);
			resp.getWriter().println(result);
		} finally {
			pm.close();
		}
	}

}
