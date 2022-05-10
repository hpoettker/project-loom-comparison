package loomtest;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.stereotype.Component;

@Component
public class BackendDelayFilter implements Filter {

  private static final long LATENCY_IN_MILLIS = 300L;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      Thread.sleep(LATENCY_IN_MILLIS);
    } catch (InterruptedException e) {
      throw new ServletException(e);
    }
    chain.doFilter(request, response);
  }

}
