package loomtest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class ReactiveFrontendController {

  private final WebClient webClient;

  ReactiveFrontendController(WebClient webClient) {
    this.webClient = webClient;
  }

  @GetMapping("/capabilities")
  Mono<String> capabilities() {
    return webClient.get().uri("/authentication").retrieve().bodyToMono(String.class)
        .flatMap(token -> webClient.get().uri("/authorization/" + token).retrieve().bodyToMono(String.class))
        .flatMap(role -> webClient.get().uri("/capabilities/" + role).retrieve().bodyToMono(String.class));
  }

}
