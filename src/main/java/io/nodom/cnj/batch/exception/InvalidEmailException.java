package io.nodom.cnj.batch.exception;


import lombok.Getter;

@Getter
public class InvalidEmailException extends RuntimeException {

  private String email;

  public InvalidEmailException(String email) {
    super(String.format("%s not a valid email", email));
    this.email = email;
  }
}
