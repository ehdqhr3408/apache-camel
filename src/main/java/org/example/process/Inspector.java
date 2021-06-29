package org.example.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Inspector implements Processor
{
  private static final Logger logger = LoggerFactory.getLogger(Inspector.class);
  @Override
  public void process(Exchange exchange) throws Exception {
    logger.info("headres: {}", exchange.getIn().getHeaders());
    logger.info("body: {}", exchange.getIn().getBody(String.class));
  }
}
