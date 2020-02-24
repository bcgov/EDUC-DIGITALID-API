package ca.bc.gov.educ.api.digitalid.validator;

import ca.bc.gov.educ.api.digitalid.model.AccessChannelCodeEntity;
import ca.bc.gov.educ.api.digitalid.model.IdentityTypeCodeEntity;
import ca.bc.gov.educ.api.digitalid.service.DigitalIDService;
import ca.bc.gov.educ.api.digitalid.struct.DigitalID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DigitalIDPayloadValidatorTest {
  @Mock
  DigitalIDService service;
  @InjectMocks
  DigitalIDPayloadValidator payloadValidator;

  @Before
  public void before() {
    payloadValidator = new DigitalIDPayloadValidator(service);
  }

  @Test
  public void testValidateLastAccessChannelCode_WhenAccessChannelCodeDoesNotExistInCodeTable_ShouldAddAnErrorTOTheReturnedList() {
    List<FieldError> errorList = new ArrayList<>();
    when(service.findAccessChannelCode("accessCode1")).thenReturn(Optional.empty());
    payloadValidator.validateLastAccessChannelCode(DigitalID.builder().lastAccessChannelCode("accessCode1").build(), errorList);
    assertEquals(1, errorList.size());
    assertEquals("Invalid Last Access Channel Code.", errorList.get(0).getDefaultMessage());
  }

  @Test
  public void testValidateLastAccessChannelCode_WhenAccessChannelCodeExistInCodeTableButEffectiveDateIsFutureDate_ShouldAddAnErrorTOTheReturnedList() {
    final List<FieldError> errorList = new ArrayList<>();
    final AccessChannelCodeEntity code = AccessChannelCodeEntity.builder().effectiveDate(LocalDateTime.MAX).accessChannelCode("a1")
            .displayOrder(1).expiryDate(LocalDateTime.MAX).description("Access Code 1").label("Access Code 1").build();
    when(service.findAccessChannelCode("a1")).thenReturn(java.util.Optional.ofNullable(code));
    payloadValidator.validateLastAccessChannelCode(DigitalID.builder().lastAccessChannelCode("a1").build(), errorList);
    assertEquals(1, errorList.size());
    assertEquals("Last Access Channel Code provided is not yet effective.", errorList.get(0).getDefaultMessage());
  }

  @Test
  public void testValidateLastAccessChannelCode_WhenAccessChannelCodeExistInCodeTableButExpiryDateIsPastDate_ShouldAddAnErrorTOTheReturnedList() {
    final List<FieldError> errorList = new ArrayList<>();
    final AccessChannelCodeEntity code = AccessChannelCodeEntity.builder().effectiveDate(LocalDateTime.now()).accessChannelCode("a1")
            .displayOrder(1).expiryDate(LocalDateTime.MIN).description("Access Code 1").label("Access Code 1").build();
    when(service.findAccessChannelCode("a1")).thenReturn(java.util.Optional.ofNullable(code));
    payloadValidator.validateLastAccessChannelCode(DigitalID.builder().lastAccessChannelCode("a1").build(), errorList);
    assertEquals(1, errorList.size());
    assertEquals("Last Access Channel Code provided has expired.", errorList.get(0).getDefaultMessage());
  }

  @Test
  public void testValidateIdentityTypeCode_WhenIdentityTypeCodeDoesNotExistInCodeTable_ShouldAddAnErrorTOTheReturnedList() {
    List<FieldError> errorList = new ArrayList<>();
    when(service.findIdentityTypeCode("accessCode1")).thenReturn(Optional.empty());
    payloadValidator.validateIdentityTypeCode(DigitalID.builder().identityTypeCode("accessCode1").build(), errorList);
    assertEquals(1, errorList.size());
    assertEquals("Invalid Identity Type Code.", errorList.get(0).getDefaultMessage());
  }

  @Test
  public void testValidateIdentityTypeCode_WhenAccessChannelCodeExistInCodeTableButEffectiveDateIsFutureDate_ShouldAddAnErrorTOTheReturnedList() {
    final List<FieldError> errorList = new ArrayList<>();
    final IdentityTypeCodeEntity code = IdentityTypeCodeEntity.builder().effectiveDate(LocalDateTime.MAX).identityTypeCode("a1")
            .displayOrder(1).expiryDate(LocalDateTime.MAX).description("Identity Code 1").label("Identity Code 1").build();
    when(service.findIdentityTypeCode("a1")).thenReturn(java.util.Optional.ofNullable(code));
    payloadValidator.validateIdentityTypeCode(DigitalID.builder().identityTypeCode("a1").build(), errorList);
    assertEquals(1, errorList.size());
    assertEquals("Identity Type Code provided is not yet effective.", errorList.get(0).getDefaultMessage());
  }

  @Test
  public void testValidateIdentityTypeCode_WhenAccessChannelCodeExistInCodeTableButExpiryDateIsPastDate_ShouldAddAnErrorTOTheReturnedList() {
    final List<FieldError> errorList = new ArrayList<>();
    final IdentityTypeCodeEntity code = IdentityTypeCodeEntity.builder().effectiveDate(LocalDateTime.now()).identityTypeCode("a1")
            .displayOrder(1).expiryDate(LocalDateTime.MIN).description("Identity Code 1").label("Identity Code 1").build();
    when(service.findIdentityTypeCode("a1")).thenReturn(java.util.Optional.ofNullable(code));
    payloadValidator.validateIdentityTypeCode(DigitalID.builder().identityTypeCode("a1").build(), errorList);
    assertEquals(1, errorList.size());
    assertEquals("Identity Type Code provided has expired.", errorList.get(0).getDefaultMessage());
  }
}
