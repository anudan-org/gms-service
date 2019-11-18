package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Workflow;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WorkflowRepository extends CrudRepository<Workflow, Long> {

    public List<Workflow> findByGranterAndObject(Organization granter, WorkflowObject object);
}
