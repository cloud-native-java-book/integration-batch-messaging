package io.nodom.cnj.integration.transformer;


import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;


@Setter
@AllArgsConstructor
public class FileMessageToJobRequest {


  private Job job;
  private static final String FILE_NAME = "file";

  @Transformer
  public JobLaunchRequest fileMessageJobLaunchRequest(Message<File> message) {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FILE_NAME, message.getPayload().getAbsolutePath()).toJobParameters();

    return new JobLaunchRequest(this.job, jobParameters);
  }
}
