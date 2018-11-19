package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import repositories.CompanyRepository;

import domain.Company;

@Component
@Transactional
public class StringToCompanyConverter implements Converter<String, Company>{

	@Autowired
	private CompanyRepository companyRepository;
	
	@Override
	public Company convert(String text) {
		Company result;
		int id;
		
		try {
			if(StringUtils.isEmpty(text)){
				result = null;
			}else{
				id = Integer.valueOf(text);
				result = this.companyRepository.findOne(id);
			}
		} catch(Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		
		return result;
	}
	
}
