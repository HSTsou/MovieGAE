package com.handsome.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.TryCatchFinally;

import org.datanucleus.store.appengine.query.JDOCursorHelper;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.handsome.data.PMF;
import com.handsome.movie.Movie;
import com.handsome.movie.Theater;
import com.handsome.parser.TheaterParser;

@SuppressWarnings("serial")
public class UpdateTheaterServlet extends HttpServlet {

	TheaterParser thParser ;
	List<Theater> thList;
	
	private static final Logger log = Logger.getLogger(UpdateComingSoonMovieServlet.class
			.getName());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		
		thParser = new TheaterParser();
		
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query numberQuery = pm.newQuery(Theater.class);
		
		List<Theater> mvIdNumList = (List<Theater>) numberQuery.execute();
		int num = mvIdNumList.size();
		int numLater;
		
		if(num % 2 !=0){
			numLater = (num/2) +1 ;
		}else{
			numLater = num;
		}
		
		log.severe("querySize = " + num);

		try {
			
			List<Theater> allDataList = new ArrayList<Theater>(); 
			
			Query q = pm.newQuery(Theater.class);
			
			q.setRange(0, num/2);

			List<Theater> results = (List<Theater>) q.execute();
			// Use the first 53 results
			log.severe(" results1 = " + results.size());
			
			Cursor cursor = JDOCursorHelper.getCursor(results);
			String cursorString = cursor.toWebSafeString();
			// Store the cursorString

			for (Theater gaeTh : results) {
				log.severe("numQuery1 = " + gaeTh.getThId());
				List<Text> theaterMvShowtime = thParser.getTheaterMvShowtime(gaeTh.getThId());
				gaeTh.setThMvShowtimeList(theaterMvShowtime);
				Date updateTime = new Date();
				gaeTh.setUpdateTime(updateTime);
				allDataList.add(gaeTh);
				
				
				//pm.makePersistent(gaeTh);
			}

			// Query q = the same query that produced the cursor
			// String cursorString = the string from storage
			Cursor cursor2 = Cursor.fromWebSafeString(cursorString);
			Map<String, Object> extensionMap = new HashMap<String, Object>();
			extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor2);
			q.setExtensions(extensionMap);
			q.setRange(0, numLater);

			List<Theater> results2 = (List<Theater>) q.execute();
			log.severe(" results2 = " + results2.size());
			// Use the next 53 results
			for (Theater gaeTh : results2) {
				log.severe("numQuery2 = " + gaeTh.getThId());
				List<Text> theaterMvShowtime = thParser.getTheaterMvShowtime(gaeTh.getThId());
				gaeTh.setThMvShowtimeList(theaterMvShowtime);
				allDataList.add(gaeTh);
				//pm.makePersistent(gaeTh);
			}
			pm.makePersistentAll(allDataList);
				
			
			
			/*log.severe("WTF?" ); //20151106
			To initiate Theater basic information.
			thList = thParser.getTheaterBasic();
			pm.makePersistentAll(thList);
			log.severe("size = " + thList.size());
			*/

		} catch (Exception e) {
			log.severe(" Exception = " + e);
			e.printStackTrace();
		}finally{
			log.info("pm.close();");
			pm.close();
		}
		
	
		
	}

}
