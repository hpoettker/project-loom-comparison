package loomtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@ResponseBody
@RestController
public class ServletFrontendController {

  private static final Logger LOG = LoggerFactory.getLogger(ServletFrontendController.class);

  private final RestTemplate restTemplate;

  ServletFrontendController(
      RestTemplateBuilder builder,
      @Value("${backend.url}") String backendUrl
  ) {
    LOG.info("Using base URL " + backendUrl);
    restTemplate = builder.rootUri(backendUrl).build();
  }

  @GetMapping("/capabilities")
  String capabilities() {
    var token = restTemplate.getForObject("/authentication", String.class);
    var role = restTemplate.getForObject("/authorization/" + token, String.class);
    return restTemplate.getForObject("/capabilities/" + role, String.class);
  }

}
