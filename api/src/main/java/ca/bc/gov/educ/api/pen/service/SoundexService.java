package ca.bc.gov.educ.api.pen.service;

import org.apache.commons.codec.language.Soundex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import ca.bc.gov.educ.api.pen.model.SoundexItem;

@Controller
@RequestMapping("soundex")
public class SoundexService {
	
	private static final Logger logger = LogManager.getLogger(SoundexService.class);
	
	@RequestMapping("/{soundexString}")
	@PreAuthorize("#oauth2.hasAnyScope('abc')")
	public @ResponseBody String soundexEncode(@PathVariable String soundexString) {
		logger.info("Entered soundexEncode function with value: " + soundexString);
		if(soundexString != null) {
			SoundexItem item = new SoundexItem(soundexString);
			Soundex soundEnc = new Soundex();
			String encValue = soundEnc.encode(soundexString);
			if(encValue != null) {
				item.setEncodedValue(encValue);
			}
			
			return new Gson().toJson(item);
		}

		return null;
	}
}