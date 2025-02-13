package org.burningokr.service.okr;

import lombok.RequiredArgsConstructor;
import org.burningokr.model.activity.Action;
import org.burningokr.model.okr.OkrTopicDescription;
import org.burningokr.model.okrUnits.OkrDepartment;
import org.burningokr.model.users.LocalUser;
import org.burningokr.model.users.User;
import org.burningokr.repositories.okr.OkrTopicDescriptionRepository;
import org.burningokr.repositories.okrUnit.OkrDepartmentRepository;
import org.burningokr.service.activity.ActivityService;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OkrTopicDescriptionService implements PostDeleteEventListener {

  private final OkrTopicDescriptionRepository okrTopicDescriptionRepository;
  private final OkrDepartmentRepository okrDepartmentRepository;
  private final ActivityService activityService;
  private final EntityManagerFactory entityManagerFactory;
  private final Logger logger = LoggerFactory.getLogger(OkrTopicDescriptionService.class);

  @PostConstruct
  private void init() {
    SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
    EventListenerRegistry registry =
      sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
    registry.getEventListenerGroup(EventType.POST_COMMIT_DELETE).appendListener(this);
  }

  /**
   * finds am OkrTopicDescription by its id
   *
   * @param id the id of the OkrTopicDescription
   * @return an {@link OkrTopicDescription} object
   */
  public OkrTopicDescription findById(long id) {
    return okrTopicDescriptionRepository.findByIdOrThrow(id);
  }

  /**
   * Updates an OkrTopicDescription
   *
   * @param okrTopicDescription the {@link OkrTopicDescription} to update
   * @return the updated OkrTopicDescription
   */
  public OkrTopicDescription updateOkrTopicDescription(
    OkrTopicDescription okrTopicDescription, User user
  ) {
    OkrTopicDescription existing = findById(okrTopicDescription.getId());

    existing.setName(okrTopicDescription.getName());
    existing.setStakeholders(okrTopicDescription.getStakeholders());
    existing.setStartTeam(okrTopicDescription.getStartTeam());
    existing.setHandoverPlan(okrTopicDescription.getHandoverPlan());
    existing.setResources(okrTopicDescription.getResources());
    existing.setDependencies(okrTopicDescription.getDependencies());
    existing.setBeginning(okrTopicDescription.getBeginning());
    existing.setDelimitation(okrTopicDescription.getDelimitation());
    existing.setContributesTo(okrTopicDescription.getContributesTo());
    existing.setDescription(okrTopicDescription.getDescription());
    existing.setInitiatorId(okrTopicDescription.getInitiatorId());

    existing = okrTopicDescriptionRepository.save(existing);

    logger.info(
      "Updated OkrTopicDescription " + existing.getName() + "(id:" + existing.getId() + ")");
    activityService.createActivity(user, existing, Action.EDITED);

    return existing;
  }

  /**
   * gets all departments that reference an okrTopicDescription
   *
   * @param okrTopicDescriptionId the referenced okrTopicDescription id
   * @return a {@link List} of {@link OkrDepartment}
   */
  public List<OkrDepartment> getOkrDepartmentsWithTopicDescription(Long okrTopicDescriptionId) {
    ArrayList<OkrDepartment> okrDepartments = new ArrayList<>();

    okrDepartmentRepository
      .findAll()
      .forEach(
        department -> {
          if (department.getOkrTopicDescription().getId().equals(okrTopicDescriptionId)) {
            okrDepartments.add(department);
          }
        });

    return okrDepartments;
  }

  /**
   * Safely deletes an OkrTopicDescription. The OkrTopicDescription will not be deleted, when it is
   * still referenced by (multiple) departments.
   *
   * @param okrTopicDescriptionId the id of the okrTopicDescription to delete.
   */
  @Transactional
  public void safeDeleteOkrTopicDescription(Long okrTopicDescriptionId, User user) {
    long count = (long) getOkrDepartmentsWithTopicDescription(okrTopicDescriptionId).size();

    if (count == 0) {
      deleteOkrTopicDescription(okrTopicDescriptionId, user);
    } else {
      logger.info(
        "OkrTopicDescription with Id "
          + okrTopicDescriptionId
          + " was not deleted, because it is referenced by"
          + count
          + " departments.");
    }
  }

  /**
   * This method is called by hibernate, when an object is being deleted. If the deleted Object is
   * an OkrDepartment, then this method will call safeDeleteOkrTopicDescription() to delete the
   * OkrTopicDescription of the Department, if neccessary.
   *
   * @param event a {@link PostDeleteEvent}
   */
  @Override
  public void onPostDelete(PostDeleteEvent event) {
    if (event.getEntity() instanceof OkrDepartment) {
      OkrDepartment department = (OkrDepartment) event.getEntity();
      safeDeleteOkrTopicDescription(department.getOkrTopicDescription().getId(), new LocalUser());
    }
  }

  /**
   * This method must return true, otherwise the onPostDelete method will not be called.
   *
   * @param entityPersister an {@link EntityPersister}
   * @return true
   */
  @Override
  public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
    return true;
  }

  /**
   * This method must return true, otherwise the onPostDelete method will not be called.
   *
   * @param persister an {@link EntityPersister}
   * @return true
   */
  @Override
  public boolean requiresPostCommitHandling(EntityPersister persister) {
    return true;
  }

  private void deleteOkrTopicDescription(Long okrTopicDescriptionId, User user) {

    // (R.J. 15.01.2020)
    // For some reason, we cannot use the okrTopicDescriptionRepository
    // to delete the OkrTopicDescription here.
    //
    // When this method is called by the onPostDelete Method, the okrTopicDescriptionRepository
    // does not delete the OkrTopicDescription when the onPostDelete Method was called due to a
    // cascading deletion.
    // For Example when a department gets deleted, because the company was deleted.

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();
    try {
      transaction.begin();
      OkrTopicDescription existing =
        entityManager.find(OkrTopicDescription.class, okrTopicDescriptionId);
      entityManager.remove(existing);
      entityManager.flush();
      transaction.commit();
      logger.info("Deleted OkrTopicDescription with Id " + existing.getId());
      activityService.createActivity(user, existing, Action.DELETED);
    } catch (Exception e) {
      logger.error("Unable to delete OkrTopicDescription with Id " + okrTopicDescriptionId);
    } finally {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      entityManager.close();
    }
  }
}
