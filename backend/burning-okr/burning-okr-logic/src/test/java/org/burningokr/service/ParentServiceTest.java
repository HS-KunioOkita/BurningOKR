package org.burningokr.service;

import org.burningokr.model.okr.Objective;
import org.burningokr.model.okrUnits.OkrBranch;
import org.burningokr.model.okrUnits.OkrCompany;
import org.burningokr.model.okrUnits.OkrDepartment;
import org.burningokr.model.okrUnits.OkrUnit;
import org.burningokr.service.okrUnitUtil.ParentService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ParentServiceTest {

  OkrCompany treeOkrCompany;
  OkrBranch treeDepartment1;
  OkrBranch treeDepartment2;
  OkrBranch treeDepartment22;
  OkrDepartment treeOkrDepartment221;
  Objective treeObjectiveCompany;
  Objective treeObjectiveD1;
  Objective treeObjectiveD2;
  Objective treeObjectiveD22;
  Objective treeObjectiveD221;
  Objective treeOrigin;
  @Mock
  private ParentService mockParentService;
  @InjectMocks
  private ParentService parentService;

  @Before
  public void reset() {
    parentService = new ParentService();
    buildTestCompany();
  }

  ///////////////////////////////
  /////   Tree-Structures  //////
  // ////////////////////////////
  /////   For a visualisation look at JTK_Backend/ObjectiveServiceTest_TreeStructure
  // ////////////////////////////

  @Test
  public void test_validateParentObjective_expectsNothing() {
    when(mockParentService.isParentObjectiveLegal(any(Objective.class), any(Objective.class)))
      .thenReturn(true);
    doCallRealMethod()
      .when(mockParentService)
      .validateParentObjective(any(Objective.class), any(Objective.class));

    Objective testObjective = new Objective();
    mockParentService.validateParentObjective(testObjective, testObjective);

    verify(mockParentService).isParentObjectiveLegal(any(Objective.class), any(Objective.class));
  }

  @Test
  public void test_validateParentObjective_expectsFalse() {
    when(mockParentService.isParentObjectiveLegal(any(Objective.class), any(Objective.class)))
      .thenReturn(false);
    doCallRealMethod()
      .when(mockParentService)
      .validateParentObjective(any(Objective.class), any(Objective.class));

    Objective testObjective = new Objective();
    try {
      mockParentService.validateParentObjective(testObjective, testObjective);
      Assert.fail();
    } catch (Exception ex) {
      assertThat(
        "Should only throw IllegalArgumentException.",
        ex,
        instanceOf(IllegalArgumentException.class)
      );
    }

    verify(mockParentService).isParentObjectiveLegal(any(Objective.class), any(Objective.class));
  }

  @Test
  public void test_isParentObjectiveLegal_expectsTrue() {
    when(mockParentService.isUnitAChildUnit(any(OkrUnit.class), any(OkrUnit.class)))
      .thenReturn(true);
    doCallRealMethod()
      .when(mockParentService)
      .isParentObjectiveLegal(any(Objective.class), any(Objective.class));

    Objective testObjective = new Objective();
    OkrUnit testParentOkrUnit = new OkrDepartment();
    testObjective.setParentOkrUnit(testParentOkrUnit);
    assertTrue(mockParentService.isParentObjectiveLegal(testObjective, testObjective));

    verify(mockParentService).isUnitAChildUnit(any(OkrUnit.class), any(OkrUnit.class));
  }

  @Test
  public void test_isParentObjectiveLegal_expectsFalse() {
    when(mockParentService.isUnitAChildUnit(any(OkrUnit.class), any(OkrUnit.class)))
      .thenReturn(false);
    doCallRealMethod()
      .when(mockParentService)
      .isParentObjectiveLegal(any(Objective.class), any(Objective.class));

    Objective testObjective = new Objective();
    OkrUnit testParentOkrUnit = new OkrDepartment();
    testObjective.setParentOkrUnit(testParentOkrUnit);
    Assert.assertFalse(mockParentService.isParentObjectiveLegal(testObjective, testObjective));

    verify(mockParentService).isUnitAChildUnit(any(OkrUnit.class), any(OkrUnit.class));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToObjCompany_expectsTrue() {
    assertTrue(parentService.isParentObjectiveLegal(treeOrigin, treeObjectiveCompany));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToObjD2_expectsTrue() {
    assertTrue(parentService.isParentObjectiveLegal(treeOrigin, treeObjectiveD2));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToObjD22_expectsTrue() {
    assertTrue(parentService.isParentObjectiveLegal(treeOrigin, treeObjectiveD22));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToObjD1_expectsFalse() {
    Assert.assertFalse(parentService.isParentObjectiveLegal(treeOrigin, treeObjectiveD1));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToObjD22_expectsFalse() {
    Assert.assertFalse(parentService.isParentObjectiveLegal(treeOrigin, treeObjectiveD221));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToOrigin_expectsFalse() {
    Assert.assertFalse(parentService.isParentObjectiveLegal(treeOrigin, treeOrigin));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToObjD221_expectsFalse() {
    Assert.assertFalse(parentService.isParentObjectiveLegal(treeOrigin, treeObjectiveD221));
  }

  private void buildTestCompany() {
    treeOkrCompany = new OkrCompany();

    treeObjectiveCompany = new Objective();
    treeOkrCompany.getObjectives().add(treeObjectiveCompany);
    treeObjectiveCompany.setParentOkrUnit(treeOkrCompany);

    treeDepartment1 = new OkrBranch();
    treeOkrCompany.getOkrChildUnits().add(treeDepartment1);
    treeDepartment1.setParentOkrUnit(treeOkrCompany);

    treeObjectiveD1 = new Objective();
    treeDepartment1.getObjectives().add(treeObjectiveD1);
    treeObjectiveD1.setParentOkrUnit(treeDepartment1);

    treeDepartment2 = new OkrBranch();
    treeOkrCompany.getOkrChildUnits().add(treeDepartment2);
    treeDepartment2.setParentOkrUnit(treeOkrCompany);

    treeObjectiveD2 = new Objective();
    treeDepartment2.getObjectives().add(treeObjectiveD2);
    treeObjectiveD2.setParentOkrUnit(treeDepartment2);

    treeDepartment22 = new OkrBranch();
    treeDepartment2.setOkrChildUnits(Arrays.asList(treeDepartment22));
    treeDepartment22.setParentOkrUnit(treeDepartment2);

    treeObjectiveD22 = new Objective();
    treeDepartment22.getObjectives().add(treeObjectiveD22);
    treeObjectiveD22.setParentOkrUnit(treeDepartment22);

    treeOkrDepartment221 = new OkrDepartment();
    treeDepartment22.setOkrChildUnits(Arrays.asList(treeOkrDepartment221));
    treeOkrDepartment221.setParentOkrUnit(treeDepartment22);

    treeObjectiveD221 = new Objective();
    treeOkrDepartment221.getObjectives().add(treeObjectiveD221);
    treeObjectiveD221.setParentOkrUnit(treeOkrDepartment221);

    treeOrigin = new Objective();
    treeOkrDepartment221.getObjectives().add(treeOrigin);
    treeOrigin.setParentOkrUnit(treeOkrDepartment221);
  }
}
