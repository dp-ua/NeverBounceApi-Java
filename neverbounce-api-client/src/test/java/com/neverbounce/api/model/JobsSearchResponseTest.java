package com.neverbounce.api.model;

import static org.junit.Assert.*;

/**
 * Created by lcsontos on 7/28/17.
 */
public class JobsSearchResponseTest extends AbstractResponseTest<JobsSearchResponse> {

  @Override
  protected Class<JobsSearchResponse> getResponseClass() {
    return JobsSearchResponse.class;
  }

  @Override
  protected String getResponseResourceName() {
    return "job_search_response.json";
  }

  @Override
  protected void assertResponse(JobsSearchResponse response) {
    assertNotNull(response);
  }

}
