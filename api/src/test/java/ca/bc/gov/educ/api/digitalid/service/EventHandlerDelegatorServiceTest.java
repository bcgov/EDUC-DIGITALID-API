package ca.bc.gov.educ.api.digitalid.service;

import ca.bc.gov.educ.api.digitalid.constants.EventType;
import ca.bc.gov.educ.api.digitalid.mappers.DigitalIDMapper;
import ca.bc.gov.educ.api.digitalid.messaging.MessagePublisher;
import ca.bc.gov.educ.api.digitalid.model.v1.DigitalIDEntity;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIDRepository;
import ca.bc.gov.educ.api.digitalid.service.v1.EventHandlerDelegatorService;
import ca.bc.gov.educ.api.digitalid.struct.v1.DigitalID;
import ca.bc.gov.educ.api.digitalid.struct.v1.Event;
import ca.bc.gov.educ.api.digitalid.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static ca.bc.gov.educ.api.digitalid.constants.EventOutcome.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class EventHandlerDelegatorServiceTest {

  @Autowired
  DigitalIDRepository digitalIDRepository;

  DigitalIDMapper mapper = DigitalIDMapper.mapper;
  UUID did;
  @Autowired
  MessagePublisher messagePublisher;
  @Autowired
  EventHandlerDelegatorService eventHandlerDelegatorService;
  @Captor
  ArgumentCaptor<byte[]> eventCaptor;

  @Before
  public void setUp() {
    DigitalIDEntity digitalID = mapper.toModel(getDigitalID());
    digitalIDRepository.save(digitalID);
    did = digitalID.getDigitalID();
  }

  @After
  public void cleanup() {
    digitalIDRepository.deleteAll();
    Mockito.clearInvocations(messagePublisher);
  }

  @Test
  public void handleEventUpdateDigitalID_givenDBOperationFailed_shouldNotSendResponseMessageToNATS() throws JsonProcessingException {
    Event event = Event.builder()
      .eventType(EventType.UPDATE_DIGITAL_ID)
      .eventPayload(createDidUpdatePayload("invalid_length_value"))
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    eventHandlerDelegatorService.handleEvent(event);
    verify(messagePublisher, never()).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), eventCaptor.capture());
  }

  @Test
  public void handleEventUpdateDigitalID_givenDBOperationSuccess_shouldSendResponseMessageToNATS() throws JsonProcessingException {
    Event event = Event.builder()
      .eventType(EventType.UPDATE_DIGITAL_ID)
      .eventPayload(createDidUpdatePayload("BCEID"))
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    eventHandlerDelegatorService.handleEvent(event);
    verify(messagePublisher, atLeastOnce()).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), eventCaptor.capture());
    var natsResponse = new String(eventCaptor.getValue());
    assertThat(natsResponse).contains(DIGITAL_ID_UPDATED.toString());
  }

  @Test
  public void handleEventUpdateDigitalID_givenReplayScenarioAndDBOperationSuccess_shouldSendResponseMessageToNATS() throws JsonProcessingException {
    Event event = Event.builder()
      .eventType(EventType.UPDATE_DIGITAL_ID)
      .eventPayload(createDidUpdatePayload("BCEID"))
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    eventHandlerDelegatorService.handleEvent(event);
    eventHandlerDelegatorService.handleEvent(event);
    verify(messagePublisher, atLeast(2)).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), eventCaptor.capture());
    var natsResponse = new String(eventCaptor.getValue());
    assertThat(natsResponse).contains(DIGITAL_ID_UPDATED.toString());
  }

  @Test
  public void handleEventUpdateDigitalID_givenInvalidDIDDBOperationSuccess_shouldSendResponseMessageToNATS() throws JsonProcessingException {
    var payload = getDigitalID("BCEID");
    payload.setDigitalID(UUID.randomUUID().toString());
    Event event = Event.builder()
      .eventType(EventType.UPDATE_DIGITAL_ID)
      .eventPayload(JsonUtil.getJsonStringFromObject(payload))
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    eventHandlerDelegatorService.handleEvent(event);
    verify(messagePublisher, atLeastOnce()).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), eventCaptor.capture());
    var natsResponse = new String(eventCaptor.getValue());
    assertThat(natsResponse).contains(DIGITAL_ID_NOT_FOUND.toString());
  }

  @Test
  public void handleEventGetDigitalID_givenDBOperationSuccess_shouldSendResponseMessageToNATS() {
    Event event = Event.builder()
      .eventType(EventType.GET_DIGITAL_ID)
      .eventPayload(did.toString())
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    eventHandlerDelegatorService.handleEvent(event);
    verify(messagePublisher, atLeastOnce()).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), eventCaptor.capture());
  }

  @Test
  public void handleEventGetDigitalID_givenRandomIDAndDBOperationSuccess_shouldSendResponseMessageToNATS() {
    Event event = Event.builder()
      .eventType(EventType.GET_DIGITAL_ID)
      .eventPayload(UUID.randomUUID().toString())
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    eventHandlerDelegatorService.handleEvent(event);
    verify(messagePublisher, atLeastOnce()).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), eventCaptor.capture());
    var natsResponse = new String(eventCaptor.getValue());
    assertThat(natsResponse).contains(DIGITAL_ID_NOT_FOUND.toString());
  }

  @Test
  public void handleEventGetDigitalID_givenReplayScenarioAndDBOperationSuccess_shouldSendResponseMessageToNATS() {
    Event event = Event.builder()
      .eventType(EventType.GET_DIGITAL_ID)
      .eventPayload(did.toString())
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    eventHandlerDelegatorService.handleEvent(event);
    eventHandlerDelegatorService.handleEvent(event);
    verify(messagePublisher, atLeast(2)).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), eventCaptor.capture());
    var natsResponse = new String(eventCaptor.getValue());
    assertThat(natsResponse).contains(DIGITAL_ID_FOUND.toString());
  }

  @Test
  public void handleEventGetDigitalIDList_givenRandomIDAndDBOperationSuccess_shouldSendResponseMessageToNATS() {
    Event event = Event.builder()
      .eventType(EventType.GET_DIGITAL_ID_LIST)
      .eventPayload(UUID.randomUUID().toString())
      .replyTo("PROFILE_REQUEST_SAGA_TOPIC")
      .sagaId(UUID.randomUUID())
      .build();
    eventHandlerDelegatorService.handleEvent(event);
    verify(messagePublisher, atLeastOnce()).dispatchMessage(eq("PROFILE_REQUEST_SAGA_TOPIC"), eventCaptor.capture());
    var natsResponse = new String(eventCaptor.getValue());
    assertThat(natsResponse).contains(DIGITAL_ID_LIST_RETURNED.toString());
  }

  private String createDidUpdatePayload(String identityTypeCode) throws JsonProcessingException {
    DigitalID payload = getDigitalID(identityTypeCode);
    return JsonUtil.getJsonStringFromObject(payload);
  }

  private DigitalID getDigitalID(String identityTypeCode) {
    DigitalID payload = getDigitalID();
    payload.setDigitalID(did.toString());
    payload.setIdentityTypeCode(identityTypeCode);
    return payload;
  }

  private DigitalID getDigitalID() {
    return DigitalID.builder()
      .identityTypeCode("12")
      .identityValue("123")
      .studentID(UUID.randomUUID().toString())
      .createDate(LocalDateTime.now().toString())
      .updateDate(LocalDateTime.now().toString())
      .createUser("TEST")
      .updateUser("TEST")
      .build();
  }
}
