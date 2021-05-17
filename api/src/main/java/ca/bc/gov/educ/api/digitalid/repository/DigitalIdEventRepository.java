package ca.bc.gov.educ.api.digitalid.repository;

import ca.bc.gov.educ.api.digitalid.model.v1.DigitalIdEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DigitalIdEventRepository extends CrudRepository<DigitalIdEvent, UUID> {
  Optional<DigitalIdEvent> findBySagaIdAndEventType(UUID sagaId, String eventType);
  List<DigitalIdEvent> findByEventStatus(String toString);
}
