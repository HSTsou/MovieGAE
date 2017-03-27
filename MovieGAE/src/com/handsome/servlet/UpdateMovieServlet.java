package com.handsome.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.datanucleus.store.appengine.query.JDOCursorHelper;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.Cursor;
import com.google.gson.Gson;
import com.handsome.data.PMF;
import com.handsome.movie.Movie;
import com.handsome.movie.MvTime;
import com.handsome.movie.Theater;
import com.handsome.parser.MovieParser;
import com.handsome.parser.MovieShowtimeParser;


@SuppressWarnings("serial")
public class UpdateMovieServlet extends HttpServlet {

	private MovieParser mvParser;
	private MovieShowtimeParser mvTimeParser;

	private static final Logger log = Logger.getLogger(UpdateMovieServlet.class
			.getName());

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		mvParser = new MovieParser();

		List<String> comingSoonList = mvParser.getComingSoonMvId();
		List<String> secondRunList = mvParser.getSecondRunMvId();
		List<String> fristRunList = mvParser.getFristRunMvId();
		List<String> mvIdList = new ArrayList<String>();
		mvIdList.addAll(comingSoonList);
		mvIdList.addAll(secondRunList);
		mvIdList.addAll(fristRunList);
		List<String> deleteList = new ArrayList<String>();// remove the movie of
															// "A" word start

		for (String string : mvIdList) {
			if (string.substring(0, 1).contentEquals("A")) {// AtMovies web site
															// "A" express
															// something
															// exhibition, not a
															// movie.
				deleteList.add(string);
			}
		}
		mvIdList.removeAll(deleteList);

		final PersistenceManager pm = PMF.get().getPersistenceManager();

		Query numberQuery = pm.newQuery(Movie.class);
		numberQuery.setFilter("isOut" + " == false");
		List<Movie> mvIdNumList = (List<Movie>) numberQuery.execute();
		int num = mvIdNumList.size();
		int numLater;

		if (num % 2 != 0) {
			numLater = (num / 2) + 1;
		} else {
			numLater = num;
		}

		log.severe("querySize = " + num);
		Future future = null;
		ThreadFactory factory = ThreadManager.currentRequestThreadFactory();

		ExecutorService executor = Executors.newFixedThreadPool(7, factory);
		try {

			if (!(num > 0)) {
				for (String mvId : mvIdList) {
					// System.out.println("Inserting " + mvId + " ...");
					log.severe(" New-Coming Movie is = " + mvId);
					Movie m = insertMovie(pm, mvId);
					// if (m != null)
					// inserShowtimes(pm, m);

				}
			} else {

				Query query = pm.newQuery(Movie.class);
				query.setFilter("isOut" + " == false");
				// query.setRange(0, num / 2);
				List<Movie> notOutMovies = (List<Movie>) query.execute();
				log.severe("notOutMovies1 = " + notOutMovies.size());
				// Cursor cursor = JDOCursorHelper.getCursor(notOutMovies);
				// String cursorString = cursor.toWebSafeString();

				// check GAE's Movie is out or playing. if out,
				// setOut(true)//delete it.
				for (final Movie m : notOutMovies) {
					final String atMoviesId = m.getAtMoviesMvId();
					// check if this movie is the following state.
					log.severe("atMoviesId = " + atMoviesId);
					if (!mvIdList.contains(atMoviesId)) {
						// System.out.println("GAE mv = "+atMoviesId +
						// " was outed in the theater.");
						m.setOut(true);
						log.severe("turn true(isOut) = " + atMoviesId);
					} else {// if exist in GAE before and not be outed, update
							// the State of movie!
							// System.out.println(atMoviesId +
							// " movie update!");
						mvIdList.remove(atMoviesId);
						log.severe("parserId.contains(GAEId) Exist before= "
								+ atMoviesId);

						future = executor.submit(new Callable() {
							public Object call() throws Exception {
								Movie movie = mvParser.getMovie(atMoviesId);

								if (movie != null) {// do parse again to update
													// Mv's
									movie.setKey(m.getKey());// All info is
																// updating, but
																// the key.

									log.severe(" exist in GAE before "
											+ atMoviesId);
									pm.makePersistent(movie);

								}
								return movie;
							}
						});
						

					}

				}

				/*
				 * Cursor cursor2 = Cursor.fromWebSafeString(cursorString);
				 * Map<String, Object> extensionMap = new HashMap<String,
				 * Object>(); extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION,
				 * cursor2); query.setExtensions(extensionMap);
				 * query.setRange(0, numLater);
				 * 
				 * List<Movie> notOutMovies2 = (List<Movie>) query.execute();
				 * log.severe(" notOutMovies2 " + notOutMovies2.size()); for
				 * (Movie m : notOutMovies2) { String atMoviesId =
				 * m.getAtMoviesMvId(); log.severe(" notOutMovies2 id = " +
				 * atMoviesId); if (!mvIdList.contains(atMoviesId)) {
				 * m.setOut(true); } else {
				 * 
				 * mvIdList.remove(atMoviesId); Movie movie =
				 * mvParser.getMovie(atMoviesId, false); if (movie != null) {
				 * movie.setKey(m.getKey());
				 * 
				 * pm.makePersistent(movie); }
				 * 
				 * }
				 * 
				 * }
				 */

				log.severe("the number of New-Coming Movie is "
						+ mvIdList.size());

				for (final String mvId : mvIdList) {

					future = executor.submit(new Callable() {
						public Object call() throws Exception {
							log.severe(" New-Coming Movie is = " + mvId);
							Movie m = insertMovie(pm, mvId);

							if (m != null) {
								log.severe("m!=null  ");
								//inserShowtimes(pm, m);
							} else {
								log.severe("m==null ");
							}
							return m;
						}
					});
					// log.severe("future.get() = "+ future.get());
				}

				executor.shutdown();//After all threads added in, executor stop to be add anymore threads.

			}

		} finally {
			//wait all thread finished jobs then close pm.
			try {
				while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
					
				}
				log.severe("executor is over ");

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				pm.close();
				log.severe("pm.close() ");
			}
		}

	}

	private Movie insertMovie(PersistenceManager pm, String mvId) {
		log.severe("insertMovie! " + mvId);
		Movie m = mvParser.getMovie(mvId);
		if (m == null) {
			log.severe("Cannot get Movie which mvId is " + mvId);
			System.out.println(mvId + " insert failed!");
			return null;
		} else {
			log.severe("insert New-Coming Movie = " + mvId);
			pm.makePersistent(m);
			return m;
		}
	}

	private void inserShowtimes(PersistenceManager pm, Movie m) {
		mvTimeParser = new MovieShowtimeParser(m.getAtMoviesMvId());
		List<String> mvTimeStrList = mvTimeParser.getMovieShowtime();
		Date current = new Date();

		if (mvTimeStrList.isEmpty()) {
			log.severe("Cannot get MvTime which mvId is " + m.getAtMoviesMvId());
		} else {
			MvTime mt = new MvTime();

			mt.setGaeMvId(m.getKey().getId());
			mt.setAllMvThShowtimeList(mvTimeStrList);
			mt.setUpdateDate(current);

			pm.makePersistentAll(mt);
			log.severe("insert MvTime : " + m.getAtMoviesMvId());
		}
	}
}
