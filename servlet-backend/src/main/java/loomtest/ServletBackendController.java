package loomtest;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ResponseBody
@RestController
public class ServletBackendController {

  @GetMapping("/authentication")
  String authentication() {
    return UUID.randomUUID().toString();
  }

  @GetMapping("/authorization/{token}")
  String authorization(@PathVariable String token) {
    return ThreadLocalRandom.current().nextBoolean() ? "ADMIN" : "USER";
  }

  @GetMapping("/capabilities/{role}")
  String capabilities(@PathVariable String role) {
    return "ADMIN".equals(role) ? "EVERYTHING" : "READ_ONLY";
  }

}
