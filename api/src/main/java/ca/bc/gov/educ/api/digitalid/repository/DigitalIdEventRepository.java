package ca.bc.gov.educ.api.digitalid.repository;

import ca.bc.gov.educ.api.digitalid.model.v1.DigitalIdEvent;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DigitalIdEventRepository extends CrudRepository<DigitalIdEvent, UUID> {
  Optional<DigitalIdEvent> findBySagaIdAndEventType(UUID sagaId, String eventType);
  List<DigitalIdEvent> findByEventStatus(String toString);

  @Transactional
  @Modifying
  @Query("delete from DigitalIdEvent where createDate <= :createDate")
  void deleteByCreateDateBefore(LocalDateTime createDate);
}
