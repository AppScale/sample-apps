// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.appengine.api.search;

import java.util.concurrent.Future;

/**
 * An Index allows synchronous and asynchronous adding and deleting of
 * {@link Document Documents} as well as synchronous and asynchronous
 * searching for Documents for a given {@link Query}. The following
 * code fragment shows how to add documents, then search the index for
 * documents matching a query.
 *<p>
 *<pre>
 *  // Get the SearchService for the default namespace
 *  SearchService searchService = SearchServiceFactory.getSearchService();
 *  // Get the index. If not yet created, create it.
 *  Index index = searchService.getIndex(
 *      IndexSpec.newBuilder().setIndexName("indexName"));
 *
 *  // Create a document.
 *  Document document = Document.newBuilder()
 *      .setId("documentId")
 *      .addField(Field.newBuilder().setName("subject").setText("my first email"))
 *      .addField(Field.newBuilder().setName("body")
 *           .setHTML("&lt;html&gt;some content here&lt;/html&gt;")
 *      .build();
 *
 *  // Put the document.
 *  try {
 *    index.put(document);
 *  } catch (PutException e) {
 *    if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
 *      // retry putting document
 *    }
 *  }
 *
 *  // Query the index.
 *  try {
 *    Results&lt;ScoredDocument&gt; results =
 *        index.search(Query.newBuilder().build("subject:first body:here"));
 *
 *    // Iterate through the search results.
 *    for (ScoredDocument document : results) {
 *      // display results
 *    }
 *  } catch (SearchException e) {
 *    if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
 *      // retry
 *    }
 *  }
 *</pre>
 *
 */
public interface Index {

  /**
   * @return the name of the index
   */
  String getName();

  /**
   * @return the namespace of the index name
   */
  String getNamespace();

  /**
   * @see #delete(String...)
   */
  Future<Void> deleteAsync(String... documentId);

  /**
   * @see #delete(String...)
   */
  Future<Void> deleteAsync(Iterable<String> documentIds);

  /**
   * @see #deleteSchema()
   * @deprecated as of 1.7.4
   */
  @Deprecated
  Future<Void> deleteSchemaAsync();

  /**
   * @see #put(Document...)
   */
  Future<PutResponse> putAsync(Document... document);

  /**
   * @see #put(Document...)
   */
  Future<PutResponse> putAsync(Document.Builder... document);

  /**
   * @see #put(Document...)
   */
  Future<PutResponse> putAsync(Iterable<Document> documents);

  /**
   * @see #search(String)
   */
  Future<Results<ScoredDocument>> searchAsync(String query);

  /**
   * @see #search(Query)
   */
  Future<Results<ScoredDocument>> searchAsync(Query query);

  /**
   * @see #getRange(GetRequest)
   */
  Future<GetResponse<Document>> getRangeAsync(GetRequest request);

  /**
   * @see #getRange(GetRequest)
   */
  Future<GetResponse<Document>> getRangeAsync(GetRequest.Builder builder);

  /**
   * Delete documents for the given document ids from the index if
   * they are in the index.
   *
   * @param  documentIds the ids of documents to delete
   * @throws DeleteException if there is a failure in the search
   * service deleting documents
   * @throws IllegalArgumentException if some document id is invalid
   */
  void delete(String... documentIds);

  /**
   * @see #delete(String...)
   */
  void delete(Iterable<String> documentIds);

  /**
   * Delete the schema from the index. A possible use may be that there are
   * typed fields which are no longer required. Make sure that you re-index
   * some or all of your documents to enable search again. A sample of
   * documents is required that uses named fields to rebuild the schema.
   *
   * @throws DeleteException if there is a failure in the search service
   *   deleting the schema
   * @deprecated as of 1.7.4
   */
  @Deprecated
  void deleteSchema();

  /**
   * Put the documents into the index, updating any document that is already
   * present.
   *
   * @param  documents the documents to put into the index
   * @return an {@link PutResponse} containing the result of
   * the put operations indicating success or failure as well as the document
   * ids. The search service will allocate document ids for documents which
   * have none provided
   * @throws PutException if there is a failure in the search
   * service putting documents
   * @throws IllegalArgumentException if some document is invalid or
   * more than {@link IndexChecker#MAXIMUM_DOCS_PER_REQUEST} documents
   * requested to be put into the index
   */
  PutResponse put(Document... documents);

  /**
   * @see #put(Document...)
   */
  PutResponse put(Document.Builder... builders);

  /**
   * @see #put(Document...)
   */
  PutResponse put(Iterable<Document> documents);

  /**
   * Gets a {@link Document} for the given document Id.
   *
   * @param documentId the identifier for the document to retrieve
   * @return the associated {@link Document}. can be null
   */
  Document get(String documentId);

  /**
   * @see #search(Query)
   */
  Results<ScoredDocument> search(String query);

  /**
   * Search the index for documents matching the query. The query
   * must specify a query string, and optionally, how many documents
   * are requested, how the results are to be sorted, scored and
   * which fields are to be returned.
   *
   * @param  query the fully specified {@link Query} object
   * @return a {@link Results} containing
   * {@link ScoredDocument ScoredDocuments}
   * @throws IllegalArgumentException if the query is invalid
   * @throws SearchQueryException if the query string is invalid
   * @throws SearchException if there is a failure in the search service
   * performing the search
   */
  Results<ScoredDocument> search(Query query);

  /**
   * Get an index's documents, in document Id order.
   *
   * @param  request contains various options restricting which documents are
   * returned.
   * @return a {@link GetResponse} containing a list of
   * documents from the index
   * @throws IllegalArgumentException if the get request is invalid
   */
  GetResponse<Document> getRange(GetRequest request);

  /**
   * @see #getRange(GetRequest)
   */
  GetResponse<Document> getRange(GetRequest.Builder builder);

  /**
   * @return the {@link Schema} describing supported document field names and
   * {@link Field.FieldType}s supported for those field names. This schema
   * will only be populated if the {@link GetIndexesRequest#isSchemaFetched}
   * is set to true on an {@link SearchService#getIndexes} request
   */
  Schema getSchema();
}
