package loomtest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

class TomcatFrontendApplicationTests {

  @Nested
  @SpringBootTest(
      properties = "backend.url=http://localhost:8080",
      webEnvironment = RANDOM_PORT
  )
  class PlatformThreadsTest {

    @Test
    void contextLoads() {
    }

  }

  @Nested
  @ActiveProfiles("loom")
  @SpringBootTest(
      properties = "backend.url=http://localhost:8080",
      webEnvironment = RANDOM_PORT
  )
  class VirtualThreadsTest {

    @Test
    void contextLoads() {
    }

  }

}
