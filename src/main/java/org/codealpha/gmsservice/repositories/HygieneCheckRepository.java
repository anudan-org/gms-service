package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.HygieneCheck;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HygieneCheckRepository extends CrudRepository<HygieneCheck,Long> {

    @Query(value = "select h.*,m.message,m.subject from hygiene_checks h inner join messages m on m.id=h.message_id",nativeQuery = true)
    public List<HygieneCheck> getHygieneChecks();
}
