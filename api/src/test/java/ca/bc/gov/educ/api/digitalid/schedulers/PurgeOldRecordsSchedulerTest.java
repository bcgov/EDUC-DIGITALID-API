package ca.bc.gov.educ.api.digitalid.schedulers;

import ca.bc.gov.educ.api.digitalid.constants.EventStatus;
import ca.bc.gov.educ.api.digitalid.model.v1.DigitalIdEvent;
import ca.bc.gov.educ.api.digitalid.repository.DigitalIdEventRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PurgeOldRecordsSchedulerTest {

  @Autowired
  DigitalIdEventRepository studentProfileRequestEventRepository;

  @Autowired
  PurgeOldRecordsScheduler purgeOldRecordsScheduler;


  @Test
  public void testPurgeOldRecords_givenOldRecordsPresent_shouldBeDeleted() {
    final var payload = " {\n" +
        "    \"createUser\": \"test\",\n" +
        "    \"updateUser\": \"test\",\n" +
        "    \"legalFirstName\": \"Jack\"\n" +
        "  }";

    final var yesterday = LocalDateTime.now().minusDays(1);

    this.studentProfileRequestEventRepository.save(this.getDigitalIdEvent(payload, LocalDateTime.now()));

    this.studentProfileRequestEventRepository.save(this.getDigitalIdEvent(payload, yesterday));

    this.purgeOldRecordsScheduler.setEventRecordStaleInDays(1);
    this.purgeOldRecordsScheduler.purgeOldRecords();

    final var servicesEvents = this.studentProfileRequestEventRepository.findAll();
    assertThat(servicesEvents).hasSize(1);
  }


  private DigitalIdEvent getDigitalIdEvent(final String payload, final LocalDateTime createDateTime) {
    return DigitalIdEvent
      .builder()
      .eventPayload(payload)
      .eventStatus(EventStatus.MESSAGE_PUBLISHED.toString())
      .eventType("UPDATE_DIGITAL_ID")
      .sagaId(UUID.randomUUID())
      .eventOutcome("DIGITAL_ID_UPDATED")
      .replyChannel("TEST_CHANNEL")
      .createDate(createDateTime)
      .createUser("DIGITAL-ID-API")
      .updateUser("DIGITAL-ID-API")
      .updateDate(createDateTime)
      .build();
  }
}
