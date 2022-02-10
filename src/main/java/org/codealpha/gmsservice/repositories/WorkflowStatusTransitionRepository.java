package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Workflow;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.entities.WorkflowStatusTransition;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WorkflowStatusTransitionRepository extends CrudRepository<WorkflowStatusTransition, Long> {

    public WorkflowStatusTransition findByFromStateAndToState(WorkflowStatus from, WorkflowStatus to);

    public List<WorkflowStatusTransition> findByWorkflow(Workflow workflow);

    public List<WorkflowStatusTransition> findByFromState(WorkflowStatus fromState);

    public List<WorkflowStatusTransition> findByToState(WorkflowStatus toState);
}
