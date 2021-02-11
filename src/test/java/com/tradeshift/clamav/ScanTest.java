package com.tradeshift.clamav;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

/**
 * These tests assume clamav-rest is running and responding locally. 
 */
public class ScanTest {

  /*
   * Scan a simple file and expect that everything is ok
   */
  @Test
  public void testFile() {
    RestTemplate t = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/octet-stream");
    byte[] octetFile = "some text".getBytes(StandardCharsets.US_ASCII);
    HttpEntity req = new HttpEntity<String>(octetFile.toString(), headers);

    String s = t.postForObject("http://localhost:8080/file", req, String.class);
    assertEquals(s, "Everything ok : true\n");
  }
}
