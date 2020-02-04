package ca.bc.gov.educ.api.digitalid.service;

import ca.bc.gov.educ.api.digitalid.properties.ApplicationProperties;
import ca.bc.gov.educ.api.digitalid.rest.RestUtils;
import ca.bc.gov.educ.api.digitalid.struct.AccessChannelCode;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static ca.bc.gov.educ.api.digitalid.constant.CodeTableConstants.*;

@Service
public class CodeTableService {

  public static final String PARAMETERS = "parameters";
  @Getter(AccessLevel.PRIVATE)
  private final RestUtils restUtils;
  private static Map<String, AccessChannelCode> lastAccessChannelCodeMap = new ConcurrentHashMap<>();
  private final ApplicationProperties props;

  @PreDestroy
  public void close() {
    lastAccessChannelCodeMap.clear();
  }

  @Autowired
  public CodeTableService(final RestUtils restUtils, ApplicationProperties props) {
    this.restUtils = restUtils;
    this.props = props;
  }

  @PostConstruct
  public void loadCodeTableDataToMemory() {
    loadAccessChannelCodes();
  }

  public void loadAccessChannelCodes() {
    RestTemplate restTemplate = restUtils.getRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    ResponseEntity<AccessChannelCode[]> accessChannelCodeResponse;
    accessChannelCodeResponse = restTemplate.exchange(props.getCodetableApiURL() + ACCESS_CHANNEL_CODE.getValue(), HttpMethod.GET, new HttpEntity<>(PARAMETERS, headers), AccessChannelCode[].class);
    if (accessChannelCodeResponse.getBody() != null) {
      lastAccessChannelCodeMap.putAll(Arrays.stream(accessChannelCodeResponse.getBody()).collect(Collectors.toMap(AccessChannelCode::getAccessChannelCode, dataSource -> dataSource)));
    }
  }


  @Cacheable("accessChannelCodeCache")
  public AccessChannelCode findAccessChannelCode(String accessChannelCode) {
    if (lastAccessChannelCodeMap.containsKey(accessChannelCode)) {
      return lastAccessChannelCodeMap.get(accessChannelCode);
    }
    loadAccessChannelCodes();
    return lastAccessChannelCodeMap.get(accessChannelCode);
  }
}
