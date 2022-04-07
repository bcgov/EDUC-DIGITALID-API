package ca.bc.gov.educ.api.digitalid.schedulers;

import ca.bc.gov.educ.api.digitalid.repository.DigitalIdEventRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
public class PurgeOldRecordsScheduler {
  @Getter(PRIVATE)
  private final DigitalIdEventRepository digitalIdEventRepository;

  @Value("${purge.records.event.after.days}")
  @Setter
  @Getter
  Integer eventRecordStaleInDays;

  public PurgeOldRecordsScheduler(final DigitalIdEventRepository digitalIdEventRepository) {
    this.digitalIdEventRepository = digitalIdEventRepository;
  }


  /**
   * run the job based on configured scheduler(a cron expression) and purge old records from DB.
   */
  @Scheduled(cron = "${scheduled.jobs.purge.old.event.records.cron}")
  @SchedulerLock(name = "PurgeOldEventRecordsLock",
      lockAtLeastFor = "PT1H", lockAtMostFor = "PT1H") //midnight job so lock for an hour
  @Transactional
  public void purgeOldRecords() {
    LockAssert.assertLocked();
    final LocalDateTime createDateToCompare = this.calculateCreateDateBasedOnStaleEventRecordInDays();
    this.digitalIdEventRepository.deleteByCreateDateBefore(createDateToCompare);
    log.info("Purged old event records");
  }

  private LocalDateTime calculateCreateDateBasedOnStaleEventRecordInDays() {
    final LocalDateTime currentTime = LocalDateTime.now();
    return currentTime.minusDays(this.getEventRecordStaleInDays());
  }
}
