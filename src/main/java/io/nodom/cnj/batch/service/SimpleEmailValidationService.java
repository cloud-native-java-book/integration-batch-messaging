package io.nodom.cnj.batch.service;


import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class SimpleEmailValidationService {


  /**
   * email validator that checks whether a given email is valid or not - valid mean: String not
   * empty, it's length > 1 and it contains the @ symbol
   *
   * @param email to validate
   * @return a boolean indicating whether an email is valid or not
   */
  public boolean isEmailValid(String email) {
    Objects.requireNonNull(email, "email could not be null");
    boolean emailIsValid = StringUtils.hasText(email) && email.length() > 1 && email.contains("@");

    log.debug("====== email is Valid : {}", emailIsValid);
    return emailIsValid;
  }
}
