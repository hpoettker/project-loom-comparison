package loomtest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@ResponseBody
@RestController
public class ServletFrontendController {

  private final WebClient webClient;

  ServletFrontendController(WebClient webClient) {
    this.webClient = webClient;
  }

  @GetMapping("/capabilities")
  String capabilities() {
    var token = webClient.get().uri("/authentication").retrieve().bodyToMono(String.class).block();
    var role = webClient.get().uri("/authorization/" + token).retrieve().bodyToMono(String.class).block();
    return webClient.get().uri("/capabilities/" + role).retrieve().bodyToMono(String.class).block();
  }

}
