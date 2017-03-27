package com.handsome.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.handsome.movie.Movie;

public class GsonExclusion implements ExclusionStrategy {

	
	
	  public boolean shouldSkipClass(Class<?> arg0) {
          return false;
      }

	  @Override
      public boolean shouldSkipField(FieldAttributes f) {

          return (f.getDeclaringClass() == Movie.class && f.getName().equals("allMvThShowtimeList"))||
          (f.getDeclaringClass() == Movie.class && f.getName().equals("story"));
      }

	
}
