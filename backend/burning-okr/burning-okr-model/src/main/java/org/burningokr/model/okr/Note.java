package org.burningokr.model.okr;

import lombok.Data;
import org.burningokr.model.activity.Trackable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class Note implements Trackable<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull
  private UUID userId;

  @Column(length = 1023)
  private String text;

  @Column(name = "date", nullable = false)
  private LocalDateTime date;

  public Note() {}

  public Note(Note note) {
    this.id = note.getId();
    this.userId = note.getUserId();
    this.text = note.getText();
    this.date = note.getDate();
  }

  @Override
  public String getName() {
    return "";
  }
}
