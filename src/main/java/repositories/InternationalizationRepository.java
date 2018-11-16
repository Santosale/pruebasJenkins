package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Internationalization;

@Repository
public interface InternationalizationRepository extends JpaRepository<Internationalization, Integer>{
	
	@Query("select i from Internationalization i where i.countryCode = ?1 AND i.messageCode = ?2")
	public Internationalization findByCountryCodeMessageCode(final String countryCode, final String messageCode);

}
