package com.handsome.servlet;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.handsome.data.PMF;

/**
 * 
 * @author CHT
 * @input id
 * @output
 */
@SuppressWarnings("serial")
public class GetMvTimeServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		resp.setCharacterEncoding("UTF-8");
		
		String id = req.getParameter("id");
		if(id == null) {
			return;
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
		/*	Query q = pm.newQuery(Showtime.class);
			q.setFilter("thId == aThId");
			q.declareParameters("String aThId");
			List<Showtime> list = (List<Showtime>) q.execute(id);

			JSONArray array = new JSONArray();
			for(Showtime st : list) {
				JSONObject obj = new JSONObject(st);
				obj.remove("key");
				obj.remove("class");
				
				array.put(obj);
			}
			
			try {
				resp.getWriter().println(array.toString(2));
			} catch (JSONException e) {
				e.printStackTrace();
			}*/
		} finally {
			pm.close();
		}
	}
}
