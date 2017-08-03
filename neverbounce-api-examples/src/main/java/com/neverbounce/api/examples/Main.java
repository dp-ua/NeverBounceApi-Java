package com.neverbounce.api.examples;

import com.neverbounce.api.client.NeverbounceClient;
import com.neverbounce.api.client.NeverbounceClientFactory;
import com.neverbounce.api.client.exception.NeverbounceApiException;
import com.neverbounce.api.internal.JsonUtils;
import com.neverbounce.api.model.AccountInfoRequest;
import com.neverbounce.api.model.AccountInfoResponse;
import com.neverbounce.api.model.JobsCreateResponse;
import com.neverbounce.api.model.JobsDeleteResponse;
import com.neverbounce.api.model.JobsParseResponse;
import com.neverbounce.api.model.JobsResultsResponse;
import com.neverbounce.api.model.JobsSearchResponse;
import com.neverbounce.api.model.JobsStartResponse;
import com.neverbounce.api.model.JobsStatusResponse;
import com.neverbounce.api.model.SingleCheckResponse;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * @author Laszlo Csontos
 * @since 4.0.0
 */
public class Main {

  /**
   * Main method.
   *
   * @param args command line arguments
   * @throws Exception Exception thrown upon errors
   */
  public static void main(String... args) throws Exception {
    Options options = new Options();
    options.addOption("a", "api-key", true, "API Key");
    CommandLineParser commandLineParser = new DefaultParser();
    CommandLine commandLine = commandLineParser.parse(options, args);

    if (!commandLine.hasOption("a")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("Main", options);
      return;
    }

    NeverbounceClient neverbounceClient =
        NeverbounceClientFactory.create(commandLine.getOptionValue("a"));

    // Account info
    AccountInfoRequest accountInfoRequest = neverbounceClient.createAccountInfoRequest();
    AccountInfoResponse accountInfoResponse = accountInfoRequest.execute();
    printJson("AccountInfoResponse", accountInfoResponse);

    // Single check
    SingleCheckResponse singleCheckResponse = neverbounceClient
            .prepareSingleCheckRequest()
            .withEmail("github@laszlocsontos.com")
            .withAddressInfo(true)
            .withCreditsInfo(true)
            .withTimeout(300)
            .build()
            .execute();

    printJson("SingleCheckResponse", singleCheckResponse);

    // Job creation
    JobsCreateResponse jobsCreateResponse = neverbounceClient
        .prepareJobsCreateWithSuppliedJsonRequest()
        .addInput("github@laszlocsontos.com", "Laszlo Csontos")
        .withAutoParse(false)
        .withAutoStart(false)
        .withFilename("test.csv")
        .build()
        .execute();

    printJson("JobsCreateResponse", jobsCreateResponse);

    long jobId = jobsCreateResponse.getJobId();

    // Job status

    JobsStatusResponse jobsStatusResponse = neverbounceClient
        .prepareJobsStatusRequest()
        .withJobId(jobId)
        .build()
        .execute();

    printJson("JobsStatusResponse", jobsStatusResponse);

    // Job parse

    JobsParseResponse jobsParseResponse = neverbounceClient
        .prepareJobsParseRequest()
        .withJobId(jobId)
        .withAutoStart(false)
        .build()
        .execute();

    printJson("JobsParseResponse", jobsParseResponse);

    // Job start

    while (true) {
      JobsStartResponse jobsStartResponse = null;

      // Workaround for "This job is not in a state which can be ran"
      try {
        jobsStartResponse = neverbounceClient
            .prepareJobsStartRequest()
            .withJobId(jobId)
            .build()
            .execute();
      } catch (NeverbounceApiException nae) {
        System.out.println("JobsStartResponse: " + nae.getMessage());

        // Sleep
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        // Try it again
        continue;
      }

      printJson("JobsStartResponse", jobsStartResponse);
      break;
    }

    // Job results

    while (true) {
      JobsResultsResponse jobsResultsResponse = null;

      // Workaround for "Results are not currently available for this job"
      try {
        jobsResultsResponse = neverbounceClient
            .prepareJobsResultsRequest()
            .withJobId(jobId)
            .build()
            .execute();
      } catch (NeverbounceApiException nae) {
        System.out.println("JobsResultsResponse: " + nae.getMessage());

        // Sleep
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        // Try it again
        continue;
      }

      printJson("JobsResultsResponse", jobsResultsResponse);
      break;
    }

    // Job search

    JobsSearchResponse jobsSearchResponse = neverbounceClient
        .prepareJobsSearchRequest()
        .withJobId(jobId)
        .build()
        .execute();

    printJson("JobsSearchResponse", jobsSearchResponse);

    // Job delete
    JobsDeleteResponse jobsDeleteResponse = neverbounceClient
        .prepareJobsDeleteRequest()
        .withJobId(jobId)
        .build()
        .execute();

    printJson("JobsDeleteResponse", jobsDeleteResponse);
  }

  private static void printJson(String callName, Object response) throws Exception {
    System.out.print(callName + ": ");
    JsonUtils.printJson(response);
  }

}
