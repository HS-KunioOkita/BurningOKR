package org.burningokr.model.okr;

import lombok.Data;
import lombok.ToString;
import org.burningokr.model.activity.Trackable;
import org.burningokr.model.okr.histories.KeyResultHistory;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
public class KeyResult implements Trackable<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ToString.Exclude
  @ManyToOne
  private Objective parentObjective = null;

  @Column(length = 255)
  @Audited
  private String name;

  @Column(length = 1023)
  private String description;

  @PositiveOrZero
  private long startValue;
  @PositiveOrZero
  private long currentValue;
  @PositiveOrZero
  private long targetValue;

  private Unit unit = Unit.NUMBER;

  @Column
  private int sequence;

  @ToString.Exclude
  @OneToMany(mappedBy = "parentKeyResult", cascade = CascadeType.REMOVE)
  private Collection<NoteKeyResult> notes = new ArrayList<>();

  @ToString.Exclude
  @OneToMany(mappedBy = "parentKeyResult", cascade = CascadeType.REMOVE)
  private Collection<KeyResultMilestone> milestones = new ArrayList<>();

  @ToString.Exclude
  @OneToMany(mappedBy = "keyResult", cascade = CascadeType.REMOVE)
  private Collection<KeyResultHistory> keyResultHistory = new ArrayList<>();
}
