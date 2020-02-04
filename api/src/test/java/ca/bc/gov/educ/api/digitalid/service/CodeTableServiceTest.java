package ca.bc.gov.educ.api.digitalid.service;


import ca.bc.gov.educ.api.digitalid.properties.ApplicationProperties;
import ca.bc.gov.educ.api.digitalid.rest.RestUtils;
import ca.bc.gov.educ.api.digitalid.struct.AccessChannelCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Date;

import static ca.bc.gov.educ.api.digitalid.constant.CodeTableConstants.ACCESS_CHANNEL_CODE;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CodeTableServiceTest {
  public static final String PARAMETERS = "parameters";
  @Mock
  RestTemplate template;

  @Mock
  RestUtils restUtils;

  @Mock
  ApplicationProperties applicationProperties;

  @InjectMocks
  CodeTableService codeTableService;


  @Test
  public void testLoadCodeTableDataToMemory_OnClassLoad_ShouldPopulateTheCodeMaps() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    when(restUtils.getRestTemplate()).thenReturn(template);
    when(applicationProperties.getCodeTableApiURL()).thenReturn("http://localhost:0000");
    when(template.exchange(applicationProperties.getCodeTableApiURL() + ACCESS_CHANNEL_CODE.getValue(), HttpMethod.GET, new HttpEntity<>(PARAMETERS, headers), AccessChannelCode[].class)).thenReturn(createLastAccessChannelCodeResponse());
    codeTableService = new CodeTableService(restUtils, applicationProperties);
    assertNotNull(codeTableService.findAccessChannelCode("access1"));
    assertNotNull(codeTableService.findAccessChannelCode("access2"));
  }

  private ResponseEntity<AccessChannelCode[]> createLastAccessChannelCodeResponse() {
    return ResponseEntity.ok(createDataArray());
  }

  private AccessChannelCode[] createDataArray() {
    AccessChannelCode[] accessChannelCodes = new AccessChannelCode[2];
    accessChannelCodes[0] = AccessChannelCode.builder().accessChannelCode("access1").effectiveDate(new Date()).expiryDate(new Date()).build();
    accessChannelCodes[1] = AccessChannelCode.builder().accessChannelCode("access2").effectiveDate(new Date()).expiryDate(new Date()).build();
    return accessChannelCodes;
  }


}
