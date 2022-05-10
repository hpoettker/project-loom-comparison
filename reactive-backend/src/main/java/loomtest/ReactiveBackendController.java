package loomtest;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ReactiveBackendController {

  private static final Duration LATENCY = Duration.ofMillis(300);

  @GetMapping("/authentication")
  Mono<String> authentication() {
    return Mono.delay(LATENCY).then(
        Mono.fromSupplier(() -> UUID.randomUUID().toString())
    );
  }

  @GetMapping("/authorization/{token}")
  Mono<String> authorization(@PathVariable String token) {
    return Mono.delay(LATENCY).then(
        Mono.fromSupplier(() -> ThreadLocalRandom.current().nextBoolean() ? "ADMIN" : "USER")
    );
  }

  @GetMapping("/capabilities/{role}")
  Mono<String> capabilities(@PathVariable String role) {
    return Mono.delay(LATENCY).then(
        Mono.fromSupplier(() -> "ADMIN".equals(role) ? "EVERYTHING" : "READ_ONLY")
    );
  }

}
