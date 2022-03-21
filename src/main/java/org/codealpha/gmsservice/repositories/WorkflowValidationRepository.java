package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.WorkflowValidation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WorkflowValidationRepository extends CrudRepository<WorkflowValidation,Long> {

    @Query(value = "SELECT a.*,message FROM public.workflow_validations a left join messages m on m.id=a.message_id where active=true and object=?1",nativeQuery = true)
    public List<WorkflowValidation> getActiveValidationsByObject(String forObject);
}
