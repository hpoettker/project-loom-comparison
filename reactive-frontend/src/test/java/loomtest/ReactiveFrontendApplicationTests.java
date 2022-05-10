package loomtest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
    properties = "backend.url=http://localhost:8080",
    webEnvironment = RANDOM_PORT
)
class ReactiveFrontendApplicationTests {

  @Test
  void contextLoads() {
  }

}