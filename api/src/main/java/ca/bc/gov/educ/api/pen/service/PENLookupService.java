package ca.bc.gov.educ.api.pen.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("pen")
public class PENLookupService {
	
	private static final Logger logger = LogManager.getLogger(PENLookupService.class);
	
	@RequestMapping("/searches")
	@PreAuthorize("#oauth2.hasAnyScope('READ')")
	public @ResponseBody String search() {
		logger.info("Called this method");
		
		return null;
	}
}