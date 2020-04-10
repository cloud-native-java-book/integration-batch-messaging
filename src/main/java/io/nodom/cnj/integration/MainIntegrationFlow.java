package io.nodom.cnj.integration;


import io.nodom.cnj.integration.transformer.FileMessageToJobRequest;
import java.io.File;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
public class MainIntegrationFlow {


  @Bean("mainFileBatchIntegrationFlow")
  public IntegrationFlow fileBatchIntegrationFlow(@Value("${in.file}") File dir,
      @Qualifier("flatJob") Job job,
      @Qualifier("validChannel") MessageChannel validChannel,
      JobLauncher jobLauncher) {
    return IntegrationFlows
        .from(Files.inboundAdapter(dir).autoCreateDirectory(true),
            spec -> spec.poller(poller -> poller.fixedRate(5, TimeUnit.SECONDS)))
        .handle(new FileMessageToJobRequest(job))
        .handle(new JobLaunchingGateway(jobLauncher))
        .routeToRecipients(
            recipientListRouterSpec -> recipientListRouterSpec.recipient(validChannel))
        .get();
  }

  @Bean("validFileBatchIntegrationFlow")
  public IntegrationFlow validFileBatchIntegrationFlow(
      @Qualifier("validChannel") MessageChannel validChannel) {
    return IntegrationFlows.from(validChannel)
        .handle(this)
        .get();
  }

  @ServiceActivator
  public void validFilesHandler(Message<JobExecution> jobExecutionMessage) {
    log.info("====== job execution status {}", jobExecutionMessage.getPayload().getExitStatus());
  }

  @Bean("validChannel")
  public MessageChannel messageChannel() {
    return MessageChannels.direct().get();
  }
}
