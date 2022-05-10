package loomtest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

class ServletBackendApplicationTests {

  @Nested
  @SpringBootTest(webEnvironment = RANDOM_PORT)
  class PlatformThreadsTest {

    @Test
    void contextLoads() {
    }

  }

  @Nested
  @ActiveProfiles("loom")
  @SpringBootTest(webEnvironment = RANDOM_PORT)
  class VirtualThreadsTest {

    @Test
    void contextLoads() {
    }

  }

}
