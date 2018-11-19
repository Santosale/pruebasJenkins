package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import repositories.ChirpRepository;

import domain.Chirp;

@Component
@Transactional
public class StringToChirpConverter implements Converter<String, Chirp>{

	@Autowired
	private ChirpRepository chirpRepository;
	
	@Override
	public Chirp convert(String text) {
		Chirp result;
		int id;
		
		try {
			if(StringUtils.isEmpty(text)){
				result = null;
			}else{
				id = Integer.valueOf(text);
				result = this.chirpRepository.findOne(id);
			}
		} catch(Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		
		return result;
	}
	
}
