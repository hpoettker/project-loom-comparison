package loomtest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("loom")
public class LoomWebServerFactoryCustomizer implements WebServerFactoryCustomizer<JettyServletWebServerFactory> {

  private static final Logger LOG = LoggerFactory.getLogger(LoomWebServerFactoryCustomizer.class);

  @Override
  public void customize(JettyServletWebServerFactory server) {
    LOG.info("Customizing thread pool with virtual threads");
    server.setThreadPool(new VirtualThreadPool());
  }

  static class VirtualThreadPool implements ThreadPool {

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void join() throws InterruptedException {
      Thread.sleep(Long.MAX_VALUE);
    }

    @Override
    public int getThreads() {
      return Integer.MAX_VALUE;
    }

    @Override
    public int getIdleThreads() {
      return Integer.MAX_VALUE;
    }

    @Override
    public boolean isLowOnThreads() {
      return false;
    }

    @Override
    public void execute(Runnable command) {
      executorService.execute(command);
    }
  }

}
