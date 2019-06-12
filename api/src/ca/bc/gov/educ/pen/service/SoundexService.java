package ca.bc.gov.educ.pen.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.codec.language.Soundex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import ca.bc.gov.educ.pen.model.SoundexItem;

@Path("/soundex")
public class SoundexService {
	
	private static final Logger logger = LogManager.getLogger(SoundexService.class);
	
	@Path("{soundexString}")
	@GET
	@Produces("application/json")
	public String soundexEncode(@PathParam("soundexString") String soundexString) {
		logger.error("Entered soundexEncode function with value: " + soundexString);
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