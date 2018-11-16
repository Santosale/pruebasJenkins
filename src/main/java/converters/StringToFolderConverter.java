package converters;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import domain.Folder;

import repositories.FolderRepository;

@Component
@Transactional
public class StringToFolderConverter implements Converter<String, Folder>{

	@Autowired
	private FolderRepository folderRepository;
	
	@Override
	public Folder convert(String text) {
		Folder result;
		int id;
		
		try {
			if(StringUtils.isEmpty(text)){
				result = null;
			}else{
				id = Integer.valueOf(text);
				result = this.folderRepository.findOne(id);
			}
		} catch(Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		
		return result;
	}
	
}
