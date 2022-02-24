package ca.bc.gov.educ.api.digitalid.service.v1;

import ca.bc.gov.educ.api.digitalid.constants.EventOutcome;
import ca.bc.gov.educ.api.digitalid.constants.EventType;
import ca.bc.gov.educ.api.digitalid.mappers.DigitalIDMapper;
import ca.bc.gov.educ.api.digitalid.model.v1.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.model.v1.DigitalIdEvent;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIDRepository;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIdEventRepository;
import ca.bc.gov.educ.api.digitalid.struct.v1.DigitalID;
import ca.bc.gov.educ.api.digitalid.struct.v1.Event;
import ca.bc.gov.educ.api.digitalid.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ca.bc.gov.educ.api.digitalid.constants.EventStatus.MESSAGE_PUBLISHED;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
public class EventHandlerService {

  public static final String NO_RECORD_SAGA_ID_EVENT_TYPE = "no record found for the saga id and event type combination, processing.";
  public static final String RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE = "record found for the saga id and event type combination, might be a duplicate or replay," +
          " just updating the db status so that it will be polled and sent back again.";
  public static final String EVENT_LOG = "event is :: {}";
  @Getter(PRIVATE)
  private final DigitalIDRepository digitalIDRepository;
  private static final DigitalIDMapper mapper = DigitalIDMapper.mapper;
  @Getter(PRIVATE)
  private final DigitalIdEventRepository digitalIdEventRepository;

  /**
   * The Ob mapper.
   */
  private final ObjectMapper obMapper = new ObjectMapper();

  @Autowired
  public EventHandlerService(final DigitalIDRepository digitalIDRepository, final DigitalIdEventRepository digitalIdEventRepository) {
    this.digitalIDRepository = digitalIDRepository;
    this.digitalIdEventRepository = digitalIdEventRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleGetDigitalIdEvent(final Event event) throws JsonProcessingException {
    val digitalIdEventOptional = this.getDigitalIdEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    final DigitalIdEvent digitalIdEvent;
    if (digitalIdEventOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace("Event is {}", event);
      val digitalId = UUID.fromString(event.getEventPayload());
      val optionalDigitalIDEntity = this.getDigitalIDRepository().findById(digitalId);
      if (optionalDigitalIDEntity.isPresent()) {
        val attachedEntity = optionalDigitalIDEntity.get();
        event.setEventPayload(JsonUtil.getJsonStringFromObject(mapper.toStructure(attachedEntity))); //update the event with payload, need to convert to structure MANDATORY otherwise jackson will break.
        event.setEventOutcome(EventOutcome.DIGITAL_ID_FOUND);
      } else {
        event.setEventOutcome(EventOutcome.DIGITAL_ID_NOT_FOUND);
      }
      digitalIdEvent = this.createDigitalIdEventRecord(event);
    } else {
      digitalIdEvent = this.getExistingDigitalIdEvent(event, digitalIdEventOptional.get());
    }

    this.getDigitalIdEventRepository().save(digitalIdEvent);
    return this.createResponseEvent(digitalIdEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleGetDigitalIdListEvent(final Event event) throws JsonProcessingException {
    val digitalIdEventOptional = this.getDigitalIdEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    final DigitalIdEvent digitalIdEvent;
    if (digitalIdEventOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace("Event is {}", event);
      val studentID = UUID.fromString(event.getEventPayload());
      val digitalIDEntityList = this.getDigitalIDRepository().findAllByStudentID(studentID);
      event.setEventPayload(JsonUtil.getJsonStringFromObject(mapper.toStructure(digitalIDEntityList))); //update the event with payload, need to convert to structure MANDATORY otherwise jackson will break.
      event.setEventOutcome(EventOutcome.DIGITAL_ID_LIST_RETURNED);

      digitalIdEvent = this.createDigitalIdEventRecord(event);
    } else {
      digitalIdEvent = this.getExistingDigitalIdEvent(event, digitalIdEventOptional.get());
    }

    this.getDigitalIdEventRepository().save(digitalIdEvent);
    return this.createResponseEvent(digitalIdEvent);
  }

  private DigitalIdEvent getExistingDigitalIdEvent(final Event event, final DigitalIdEvent digitalIdEvent) {
    log.info(RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE);
    log.trace(EVENT_LOG, event);
    digitalIdEvent.setUpdateDate(LocalDateTime.now());
    return digitalIdEvent;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleUpdateDigitalIdEvent(final Event event) throws JsonProcessingException {
    val digitalIdEventOptional = this.getDigitalIdEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    final DigitalIdEvent digitalIdEvent;
    if (digitalIdEventOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_LOG, event);
      final DigitalIDEntity entity = mapper.toModel(JsonUtil.getJsonObjectFromString(DigitalID.class, event.getEventPayload()));
      val optionalDigitalIDEntity = this.getDigitalIDRepository().findById(entity.getDigitalID());
      if (optionalDigitalIDEntity.isPresent()) {
        val attachedEntity = optionalDigitalIDEntity.get();
        BeanUtils.copyProperties(entity, attachedEntity);
        attachedEntity.setUpdateDate(LocalDateTime.now());
        this.getDigitalIDRepository().save(attachedEntity);
        event.setEventPayload(JsonUtil.getJsonStringFromObject(mapper.toStructure(attachedEntity)));// need to convert to structure MANDATORY otherwise jackson will break.
        event.setEventOutcome(EventOutcome.DIGITAL_ID_UPDATED);
      } else {
        event.setEventOutcome(EventOutcome.DIGITAL_ID_NOT_FOUND);
      }
      digitalIdEvent = this.createDigitalIdEventRecord(event);
    } else {
      digitalIdEvent = this.getExistingDigitalIdEvent(event, digitalIdEventOptional.get());
    }
    this.getDigitalIdEventRepository().save(digitalIdEvent);
    return this.createResponseEvent(digitalIdEvent);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public byte[] handleUpdateDigitalIdListEvent(final Event event) throws JsonProcessingException {
    val digitalIdEventOptional = this.getDigitalIdEventRepository().findBySagaIdAndEventType(event.getSagaId(), event.getEventType().toString());
    final DigitalIdEvent digitalIdEvent;
    if (digitalIdEventOptional.isEmpty()) {
      log.info(NO_RECORD_SAGA_ID_EVENT_TYPE);
      log.trace(EVENT_LOG, event);
      final List<DigitalID> digitalIDList = this.obMapper.readValue(event.getEventPayload(), new TypeReference<>() {
      });
      List<DigitalIDEntity> entities = new ArrayList<>(digitalIDList.size());

      for(var entity : digitalIDList) {
        val optionalDigitalIDEntity = this.getDigitalIDRepository().findById(UUID.fromString(entity.getDigitalID()));
        if (optionalDigitalIDEntity.isPresent()) {
          val attachedEntity = optionalDigitalIDEntity.get();
          BeanUtils.copyProperties(entity, attachedEntity);
          attachedEntity.setUpdateDate(LocalDateTime.now());
          attachedEntity.setDigitalID(getUUIDValue(entity.getDigitalID()));
          attachedEntity.setStudentID(getUUIDValue(entity.getStudentID()));

          if(log.isDebugEnabled()) {
            log.debug("About to save digital ID with payload {}", JsonUtil.getJsonStringFromObject(attachedEntity));
          }
          this.getDigitalIDRepository().save(attachedEntity);
          entities.add(attachedEntity);
        }
      }

      event.setEventPayload(JsonUtil.getJsonStringFromObject(mapper.toStructure(entities)));// need to convert to structure MANDATORY otherwise jackson will break.
      event.setEventOutcome(EventOutcome.DIGITAL_ID_LIST_UPDATED);
      digitalIdEvent = this.createDigitalIdEventRecord(event);
    } else {
      digitalIdEvent = this.getExistingDigitalIdEvent(event, digitalIdEventOptional.get());
    }
    this.getDigitalIdEventRepository().save(digitalIdEvent);
    return this.createResponseEvent(digitalIdEvent);
  }

  private UUID getUUIDValue(String value) {
    if(StringUtils.isEmpty(value)) {
      return null;
    }
    return UUID.fromString(value);
  }

  private byte[] createResponseEvent(final DigitalIdEvent digitalIdEvent) throws JsonProcessingException {
    val responseEvent = Event.builder()
      .sagaId(digitalIdEvent.getSagaId())
      .eventType(EventType.valueOf(digitalIdEvent.getEventType()))
      .eventOutcome(EventOutcome.valueOf(digitalIdEvent.getEventOutcome()))
      .eventPayload(digitalIdEvent.getEventPayload()).build();
    return JsonUtil.getJsonSBytesFromObject(responseEvent);
  }

  private DigitalIdEvent createDigitalIdEventRecord(final Event event) {
    return DigitalIdEvent.builder()
            .createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now())
            .createUser(event.getEventType().toString()) //need to discuss what to put here.
            .updateUser(event.getEventType().toString())
            .eventPayload(event.getEventPayload())
            .eventType(event.getEventType().toString())
            .sagaId(event.getSagaId())
            .eventStatus(MESSAGE_PUBLISHED.toString())
            .eventOutcome(event.getEventOutcome().toString())
            .replyChannel(event.getReplyTo())
            .build();
  }
}
