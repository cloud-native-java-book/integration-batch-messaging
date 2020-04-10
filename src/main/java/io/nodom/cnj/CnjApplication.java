package io.nodom.cnj;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@EnableIntegration
@EnableBatchProcessing
@SpringBootApplication
public class CnjApplication {

  public static void main(String[] args) {
    SpringApplication.run(CnjApplication.class, args);
  }

}
