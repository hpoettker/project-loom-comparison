package loomtest;

import org.apache.coyote.AbstractProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Profile("loom")
public class LoomTomcatProtocolHandlerCustomizer implements TomcatProtocolHandlerCustomizer<AbstractProtocol<?>> {

  private static final Logger LOG = LoggerFactory.getLogger(LoomTomcatProtocolHandlerCustomizer.class);

  private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

  @Override
  public void customize(AbstractProtocol<?> protocol) {
    LOG.info("Customizing executor to use virtual threads");
    protocol.setExecutor(executorService);
    protocol.setMaxThreads(Integer.MAX_VALUE);
    protocol.setMaxConnections(-1);
  }

}
