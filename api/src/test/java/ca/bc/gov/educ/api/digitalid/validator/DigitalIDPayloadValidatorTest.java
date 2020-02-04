package ca.bc.gov.educ.api.digitalid.validator;

import ca.bc.gov.educ.api.digitalid.service.CodeTableService;
import ca.bc.gov.educ.api.digitalid.service.DigitalIDService;
import ca.bc.gov.educ.api.digitalid.struct.AccessChannelCode;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DigitalIDPayloadValidatorTest {
  @Mock
  CodeTableService codeTableService;
  @Mock
  DigitalIDService service;
  @InjectMocks
  DigitalIDPayloadValidator payloadValidator;

  @Before
  public void before() {
    payloadValidator = new DigitalIDPayloadValidator(codeTableService);
  }

  @Test
  public void testValidateLastAccessChannelCode_WhenAccessChannelCodeDoesNotExistInCodeTable_ShouldAddAnErrorTOTheReturnedList() {
    List<FieldError> errorList = new ArrayList<>();
    when(codeTableService.findAccessChannelCode("accessCode1")).thenReturn(null);
    payloadValidator.validateLastAccessChannelCode(DigitalID.builder().lastAccessChannelCode("accessCode1").build(), errorList);
    assertEquals(1, errorList.size());
    assertEquals("Invalid Last Access Channel Code.", errorList.get(0).getDefaultMessage());
  }

  @Test
  public void testValidateLastAccessChannelCode_WhenAccessChannelCodeExistInCodeTableButEffectiveDateIsFutureDate_ShouldAddAnErrorTOTheReturnedList() {
    final List<FieldError> errorList = new ArrayList<>();
    final AccessChannelCode code = AccessChannelCode.builder().effectiveDate(new GregorianCalendar(2099, Calendar.FEBRUARY, 1).getTime()).accessChannelCode("a1")
            .displayOrder(1).expiryDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime()).description("Access Code 1").label("Access Code 1").build();
    when(codeTableService.findAccessChannelCode("a1")).thenReturn(code);
    payloadValidator.validateLastAccessChannelCode(DigitalID.builder().lastAccessChannelCode("a1").build(), errorList);
    assertEquals(1, errorList.size());
    assertEquals("Last Access Channel Code provided is not yet effective.", errorList.get(0).getDefaultMessage());
  }
  @Test
  public void testValidateLastAccessChannelCode_WhenAccessChannelCodeExistInCodeTableButExpiryDateIsPastDate_ShouldAddAnErrorTOTheReturnedList() {
    final List<FieldError> errorList = new ArrayList<>();
    final AccessChannelCode code = AccessChannelCode.builder().effectiveDate(new GregorianCalendar(2000, Calendar.FEBRUARY, 1).getTime()).accessChannelCode("a1")
            .displayOrder(1).expiryDate(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime()).description("Access Code 1").label("Access Code 1").build();
    when(codeTableService.findAccessChannelCode("a1")).thenReturn(code);
    payloadValidator.validateLastAccessChannelCode(DigitalID.builder().lastAccessChannelCode("a1").build(), errorList);
    assertEquals(1, errorList.size());
    assertEquals("Last Access Channel Code provided has expired.", errorList.get(0).getDefaultMessage());
  }
}