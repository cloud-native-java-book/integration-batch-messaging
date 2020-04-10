package io.nodom.cnj.batch.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contact {


  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private boolean isValidEmail;
}
