package io.nodom.cnj.batch;


import io.nodom.cnj.batch.dto.Contact;
import io.nodom.cnj.batch.exception.InvalidEmailException;
import io.nodom.cnj.batch.service.SimpleEmailValidationService;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.client.HttpStatusCodeException;


@Slf4j
@Configuration
public class BatchConfig {


  private static final String INSERT_SQL_QUERY =
      "insert into CONTACT( first_name, last_name, email, valid_email ) "
          + "values ( :firstName, :lastName, :email, :validEmail )";


  private StepBuilderFactory stepBuilderFactory;
  private JobBuilderFactory jobBuilderFactory;


  @Autowired
  public BatchConfig(JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean("flatJob")
  public Job flatJob(@Qualifier("flatStep") Step flatStep) {
    return this.jobBuilderFactory.get("flatJob")
        .incrementer(new RunIdIncrementer())
        .start(flatStep)
        .build();
  }

  @Bean("flatStep")
  public Step flatStep(
      @Qualifier("contactFlatItemReader") FlatFileItemReader<Contact> contactFlatFileItemReader,
      @Qualifier("contactContactItemProcessor") ItemProcessor<Contact, Contact> contactContactItemProcessor,
      @Qualifier("contactJdbcBatchItemWriter") JdbcBatchItemWriter<Contact> contactJdbcBatchItemWriter) {
    return this.stepBuilderFactory.get("faltStep")
        .<Contact, Contact>chunk(10)
        .reader(contactFlatFileItemReader)
        .processor(contactContactItemProcessor)
        .writer(contactJdbcBatchItemWriter)
        .faultTolerant()
        .skipPolicy((Throwable t, int skipCount) -> {
          log.info("skipping ");
          return t.getClass().isAssignableFrom(InvalidEmailException.class);
        }).retry(HttpStatusCodeException.class)
        .retryLimit(2)
        .build();
  }

  @StepScope
  @Bean("contactFlatItemReader")
  public FlatFileItemReader<Contact> contactFlatItemReader(@Value("file://#{jobParameters['file']}")
      Resource pathToFile) {
    return new FlatFileItemReaderBuilder<Contact>().name("contact-file-reader")
        .resource(pathToFile)
        .targetType(Contact.class)
        .delimited()
        .names("firstName,lastName,email".split(","))
        .build();
  }

  @Bean("contactContactItemProcessor")
  public ItemProcessor<Contact, Contact> contactContactItemProcessor(
      SimpleEmailValidationService emailValidationService) {
    return item -> {
      boolean isValidEmail = emailValidationService.isEmailValid(item.getEmail());
      item.setValidEmail(isValidEmail);
      if (!isValidEmail) {
        throw new InvalidEmailException(item.getEmail());
      }
      return item;
    };
  }

  @Bean("contactJdbcBatchItemWriter")
  public JdbcBatchItemWriter<Contact> contactJdbcBatchItemWriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Contact>().dataSource(dataSource)
        .beanMapped()
        .sql(INSERT_SQL_QUERY)
        .build();
  }
}
