
package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.IdentifierRepository;
import domain.Identifier;

@Service
@Transactional
public class IdentifierService {

	@Autowired
	private IdentifierRepository	identifierRepository;


	// Constructor
	public IdentifierService() {
		super();
	}

	public Identifier save(final Identifier identifier) {
		Identifier result;

		Assert.notNull(identifier);

		result = this.identifierRepository.save(identifier);

		return result;
	}

	//Other business methods
	public Identifier findIdentifier() {
		Identifier result;

		result = (Identifier) this.identifierRepository.findAll().toArray()[0];

		return result;

	}

}
