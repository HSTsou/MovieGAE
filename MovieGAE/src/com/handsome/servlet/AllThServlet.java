package com.handsome.servlet;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.handsome.data.PMF;

import com.handsome.movie.Theater;

public class AllThServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setCharacterEncoding("UTF-8");
		
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		
		try{
			Query query = pm.newQuery(Theater.class);
			//query.setFilter("isOut" + " == false");
			@SuppressWarnings("unchecked")
			List<Theater> theaterInfo = (List<Theater>) query.execute();
			Gson gson = new Gson();
			resp.getWriter().println(gson.toJson(theaterInfo));
		}finally{
			pm.close();
		}
		
	}

}
