package loomtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(WebClientConfiguration.class);

  @Bean
  WebClient webClient(
      WebClient.Builder builder,
      @Value("${backend.url}") String backendUrl
  ) {
    LOG.info("Using backend URL " + backendUrl);

    var connectionProvider = ConnectionProvider.create("webClient", 20_000);
    var clientHttpConnector = new ReactorClientHttpConnector(HttpClient.create(connectionProvider));

    return builder
        .baseUrl(backendUrl)
        .clientConnector(clientHttpConnector)
        .build();
  }

}
