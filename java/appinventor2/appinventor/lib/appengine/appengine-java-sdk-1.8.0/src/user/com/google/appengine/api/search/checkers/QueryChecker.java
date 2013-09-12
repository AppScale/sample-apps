// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.appengine.api.search.checkers;

import com.google.appengine.api.search.SearchQueryException;
import com.google.appengine.api.search.SearchServicePb.SearchParams;
import com.google.appengine.api.search.query.QueryTreeBuilder;
import com.google.apphosting.api.AppEngineInternal;

import org.antlr.runtime.RecognitionException;

import java.io.UnsupportedEncodingException;

/**
 * Checks values of {@link com.google.appengine.api.search.Query}.
 *
 */
@AppEngineInternal
public final class QueryChecker {

  /**
   * Checks that query is not null and is parsable.
   * @param query the query to check
   * @return the checked query
   * @throws SearchQueryException if the query is not parsable
   * @throws IllegalArgumentException if the query is too long
   */
  public static String checkQuery(String query) {
    checkQueryFast(query);
    return checkQueryParses(query);
  }

  /**
   * Checks if the given query parses. This method assumes that the
   * other checks, such as length, non-null, etc., were already
   * performed.
   *
   * @param query the query to check
   * @return the checked query
   * @throws SearchQueryException if the query is not parsable
   */
  private static String checkQueryParses(String query) {
    try {
      new QueryTreeBuilder().parse(query);
    } catch (RecognitionException e) {
      throw new SearchQueryException("Unable to parse query: " + query);
    }
    return query;
  }

  /**
   * Checks the search specification is valid, specifically, has a valid
   * index specification, a non-null query, a non-null number of documents
   * to return specification, a valid cursor if present, valid sort
   * specification list, a valid collection of field names for sorting,
   * and a valid scorer specification.
   *
   * @param params the SearchParams to check
   * @return this checked SearchParams
   * @throws IllegalArgumentException if some part of the specification is
   * invalid
   * @throws SearchQueryException if the query is unparsable
   */
  public static SearchParams checkValid(SearchParams params) {
    checkValidFast(params);
    checkQueryParses(params.getQuery());
    return params;
  }

  /**
   * Performs a fast check of the search parameters. This method does not
   * check if the query stored in the parameters parses.
   *
   * @param params the search parameters to check
   * @return this checked SearchParams
   * @throws IllegalArgumentException if parameters are not valid
   */
  public static SearchParams checkValidFast(SearchParams params) {
    IndexChecker.checkName(params.getIndexSpec().getName());
    checkQueryFast(params.getQuery());
    QueryOptionsChecker.checkValid(params);
    return params;
  }

  /**
   * Checks if the query is not null, and not overly long. This method
   * does not check if the query is parsable.
   *
   * @param query the query to check
   * @return the checked query
   */
  private static String checkQueryFast(String query) {
    Preconditions.checkNotNull(query, "query cannot be null");
    int length;
    try {
      length = query.getBytes("UTF-8").length;
    } catch (UnsupportedEncodingException e){
      throw new IllegalArgumentException("Unsupported encoding UTF-8. Shouldn't  happen, ever!");
    }
    Preconditions.checkArgument(
        length <= SearchApiLimits.MAXIMUM_QUERY_LENGTH,
        String.format("query string must not be longer than %d bytes, was %d",
                      SearchApiLimits.MAXIMUM_QUERY_LENGTH, length));
    return query;
  }
}
