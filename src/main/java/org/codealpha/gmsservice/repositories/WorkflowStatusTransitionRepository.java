package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.entities.WorkflowStatusTransition;
import org.springframework.data.repository.CrudRepository;

public interface WorkflowStatusTransitionRepository extends CrudRepository<WorkflowStatusTransition, Long> {

    public WorkflowStatusTransition findByFromStateAndToState(WorkflowStatus from, WorkflowStatus to);
}
