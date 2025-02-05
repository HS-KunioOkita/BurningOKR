package org.burningokr.model.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AadUser implements User {

  @Id
  private UUID id;
  private String givenName;
  private String surname;
  private String mail;
  private String jobTitle;
  private String department;
  private String mailNickname;
  private String photo;
}
