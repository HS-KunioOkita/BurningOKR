package org.burningokr.controller.okrUnit;

import lombok.RequiredArgsConstructor;
import org.burningokr.annotation.RestApiController;
import org.burningokr.dto.okr.OkrTopicDraftDto;
import org.burningokr.dto.okrUnit.OkrBranchDto;
import org.burningokr.dto.okrUnit.OkrDepartmentDto;
import org.burningokr.mapper.interfaces.DataMapper;
import org.burningokr.model.okr.okrTopicDraft.OkrTopicDraft;
import org.burningokr.model.okrUnits.OkrBranch;
import org.burningokr.model.okrUnits.OkrDepartment;
import org.burningokr.model.users.User;
import org.burningokr.service.exceptions.DuplicateTeamMemberException;
import org.burningokr.service.okrUnit.OkrUnitService;
import org.burningokr.service.okrUnit.OkrUnitServiceFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@RestApiController
@RequiredArgsConstructor
public class OkrBranchController {

  private final OkrUnitServiceFactory<OkrBranch> okrBranchOkrUnitServiceFactory;
  private final DataMapper<OkrDepartment, OkrDepartmentDto> departmentMapper;
  private final DataMapper<OkrBranch, OkrBranchDto> okrBranchMapper;
  private final DataMapper<OkrTopicDraft, OkrTopicDraftDto> okrTopicDraftMapper;

  /**
   * API Endpoint to add a sub-OkrDepartment to an existing OKR_BRANCH.
   *
   * @param unitId           a long value
   * @param okrDepartmentDto a {@link OkrDepartmentDto} object
   * @param user             an {@link User} object
   * @return a {@link ResponseEntity} ok with the added sub-OkrDepartment
   */
  @PostMapping("/branch/{unitId}/department")
  @PreAuthorize("@authorizationService.isAdmin()")
  public ResponseEntity<OkrDepartmentDto> addSubDepartmentToBranch(
    @PathVariable long unitId,
    @Valid
    @RequestBody
    OkrDepartmentDto okrDepartmentDto,
    User user
  )
    throws DuplicateTeamMemberException {
    OkrUnitService<OkrBranch> branchService =
      okrBranchOkrUnitServiceFactory.getRoleServiceForDepartment(unitId);
    OkrDepartment okrDepartment = departmentMapper.mapDtoToEntity(okrDepartmentDto);
    okrDepartment.setId(null);
    okrDepartment = (OkrDepartment) branchService.createChildUnit(unitId, okrDepartment, user);
    return ResponseEntity.ok(departmentMapper.mapEntityToDto(okrDepartment));
  }

  /**
   * API Endpoint to add a sub-OKR_BRANCH to an existing OKR_BRANCH.
   *
   * @param unitId       a long value
   * @param okrBranchDTO a {@link OkrBranchDto} object
   * @param user         an {@link User} object
   * @return a {@link ResponseEntity} ok with the added sub-OKR_BRANCH
   */
  @PostMapping("/branch/{unitId}/branch")
  @PreAuthorize("@authorizationService.isAdmin()")
  public ResponseEntity<OkrBranchDto> addSubBranchToBranch(
    @PathVariable long unitId,
    @Valid
    @RequestBody
    OkrBranchDto okrBranchDTO,
    User user
  )
    throws DuplicateTeamMemberException {
    OkrUnitService<OkrBranch> branchService =
      okrBranchOkrUnitServiceFactory.getRoleServiceForDepartment(unitId);
    OkrBranch okrBranch = okrBranchMapper.mapDtoToEntity(okrBranchDTO);
    okrBranch.setId(null);
    okrBranch = (OkrBranch) branchService.createChildUnit(unitId, okrBranch, user);
    return ResponseEntity.ok(okrBranchMapper.mapEntityToDto(okrBranch));
  }
}
