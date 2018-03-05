package fi.solita.clamav;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClamAVProxy {

  @Value("${clamd.host}")
  private String hostname;

  @Value("${clamd.port}")
  private int port;

  @Value("${clamd.timeout}")
  private int timeout;

  private DateFormat df = new SimpleDateFormat("[yyyy-MM-dd'T'HH:mm:ss.SSS z]");
  /**
   * @return Clamd status.
   */
  @RequestMapping("/")
  public String ping() throws IOException {
    System.out.println(df.format(new Date()) + " received ping");
    ClamAVClient a = new ClamAVClient(hostname, port, timeout);
    return "Clamd responding: " + a.ping() + "\n";
  }

  @RequestMapping(value="/file", method=RequestMethod.POST, consumes = "application/octet-stream", produces = {TEXT_PLAIN_VALUE})
  public @ResponseBody String handleFileDirect(InputStream content) throws IOException{
    ClamAVClient a = new ClamAVClient(hostname, port, timeout);
    byte[] r = a.scan(content);
    return "Everything ok : " + ClamAVClient.isCleanReply(r) + "\n";
  }

  /**
   * @return Clamd scan reply
   */
  @RequestMapping(value="/scanReply", method=RequestMethod.POST)
  public @ResponseBody String handleFileUploadReply(@RequestParam("name") String name,
                                                    @RequestParam("file") MultipartFile file) throws IOException{
    if (!file.isEmpty()) {
      ClamAVClient a = new ClamAVClient(hostname, port, timeout);
      return new String(a.scan(file.getInputStream()));
    } else throw new IllegalArgumentException("empty file");
  }
}
