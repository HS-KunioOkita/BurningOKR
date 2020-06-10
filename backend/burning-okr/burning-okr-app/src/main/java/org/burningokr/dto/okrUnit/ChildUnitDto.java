package org.burningokr.dto.okrUnit;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javax.validation.constraints.NotNull;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "__okrUnitType",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = OkrDepartmentDto.class, name = "DEPARTMENT"),
  @JsonSubTypes.Type(value = OkrBranchDTO.class, name = "OKR_BRANCH")
})
public abstract class ChildUnitDto extends OkrUnitDto {

  protected ChildUnitDto(UnitType unitType) {
    this.__okrUnitType = unitType;
  }

  protected UnitType __okrUnitType;

  @NotNull protected Long parentUnitId;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  protected boolean isActive;

  public boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(boolean active) {
    isActive = active;
  }

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private boolean isParentUnitAOkrBranch;

  public boolean getIsParentUnitAOkrBranch() {
    return isParentUnitAOkrBranch;
  }

  public void setIsParentUnitAOkrBranch(boolean parentUnitADepartment) {
    isParentUnitAOkrBranch = parentUnitADepartment;
  }
}
