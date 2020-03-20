package ca.bc.gov.educ.api.digitalid.service;

import ca.bc.gov.educ.api.digitalid.constants.EventOutcome;
import ca.bc.gov.educ.api.digitalid.mappers.DigitalIDMapper;
import ca.bc.gov.educ.api.digitalid.model.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.model.DigitalIdEvent;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIDRepository;

import ca.bc.gov.educ.api.digitalid.repository.DigitalIdEventRepository;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import ca.bc.gov.educ.api.digitalid.struct.Event;
import ca.bc.gov.educ.api.digitalid.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static ca.bc.gov.educ.api.digitalid.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.digitalid.constants.EventStatus.MESSAGE_PUBLISHED;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EventHandlerService {

  public static final String NO_RECORD_SAGA_ID_EVENT_TYPE = "no record found for the saga id and event type combination, processing. {}";
  public static final String RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE = "record found for the saga id and event type combination, might be a duplicate or replay," +
          " just updating the db status so that it will be polled and sent back again. {}";
  @Getter(PRIVATE)
  private final DigitalIDRepository digitalIDRepository;
  private static final DigitalIDMapper mapper = DigitalIDMapper.mapper;
  @Getter(PRIVATE)
  private final DigitalIdEventRepository digitalIdEventRepository;

  @Autowired
  public EventHandlerService(final DigitalIDRepository digitalIDRepository, final DigitalIdEventRepository digitalIdEventRepository) {
    this.digitalIDRepository = digitalIDRepository;
    this.digitalIdEventRepository = digitalIdEventRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleEvent(Event event) {
    try {
      switch (event.getEventType()) {
        case DIGITAL_ID_EVENT_OUTBOX_PROCESSED:
          log.info("received outbox processed event :: " + event.getEventPayload());
          handleDigitalIdOutboxProcessedEvent(event.getEventPayload());
          break;
        case UPDATE_DIGITAL_ID:
          log.info("received update digital id event :: " + event.getEventPayload());
          handleUpdateDigitalIdEvent(event);
          break;
        default:
          log.info("silently ignoring other events.");
          break;
      }
    } catch (final Exception e) {
      log.error("Exception", e);
    }
  }

  private void handleUpdateDigitalIdEvent(Event event) throws JsonProcessingException, IllegalAccessException {
    val digitalIdEventOptional = getDigitalIdEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    DigitalIdEvent digitalIdEvent;
    if (!digitalIdEventOptional.isPresent()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE, event);
      DigitalIDEntity entity = mapper.toModel(JsonUtil.getJsonObjectFromString(DigitalID.class, event.getEventPayload()));
      val optionalDigitalIDEntity = getDigitalIDRepository().findById(entity.getDigitalID());
      if (optionalDigitalIDEntity.isPresent()) {
        val attachedEntity = optionalDigitalIDEntity.get();
        updateValuesInAttachedEntity(entity, attachedEntity);
        getDigitalIDRepository().save(attachedEntity);
        event.setEventPayload(JsonUtil.getJsonStringFromObject(attachedEntity));
        event.setEventOutcome(EventOutcome.DIGITAL_ID_UPDATED);
      } else {
        event.setEventOutcome(EventOutcome.DIGITAL_ID_NOT_FOUND);
      }
      digitalIdEvent = createDigitalIdEventRecord(event);
    } else {
      log.info(RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE, event);
      digitalIdEvent = digitalIdEventOptional.get();
      digitalIdEvent.setEventStatus(DB_COMMITTED.toString());
    }

    getDigitalIdEventRepository().save(digitalIdEvent);
  }
  /**
   * this method will update the fields with not null values from entity to attached entity.
   */
  private void updateValuesInAttachedEntity(DigitalIDEntity entity, DigitalIDEntity attachedEntity) throws IllegalAccessException {
    for (Field field : attachedEntity.getClass().getDeclaredFields()) {
      if (!field.isAccessible()) {
        field.setAccessible(true);
        Object value = field.get(entity);
        if (value != null) {
          field.set(attachedEntity, value);
        }
      }
    }
    attachedEntity.setUpdateDate(LocalDateTime.now());
  }

  private void handleDigitalIdOutboxProcessedEvent(String digitalIdEventId) {
    val digitalIdEvent = getDigitalIdEventRepository().findById(UUID.fromString(digitalIdEventId));
    if (digitalIdEvent.isPresent()) {
      val digIdEvent = digitalIdEvent.get();
      digIdEvent.setEventStatus(MESSAGE_PUBLISHED.toString());
      getDigitalIdEventRepository().save(digIdEvent);
    }
  }


  private DigitalIdEvent createDigitalIdEventRecord(Event event) {
    return DigitalIdEvent.builder()
            .createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now())
            .createUser(event.getEventType().toString()) //need to discuss what to put here.
            .updateUser(event.getEventType().toString())
            .eventPayload(event.getEventPayload())
            .eventType(event.getEventType().toString())
            .sagaId(event.getSagaId())
            .eventStatus(DB_COMMITTED.toString())
            .eventOutcome(event.getEventOutcome().toString())
            .replyChannel(event.getReplyTo())
            .build();
  }
}
