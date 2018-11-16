
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Configuration;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {

	@Query("select s from Configuration c join c.spamWords s")
	Collection<String> findSpamWords();

	@Query("select c.vat from Configuration c")
	Double findVat();

	@Query("select c.companyName from Configuration c")
	String findCompanyName();

	@Query("select c.banner from Configuration c")
	String findBanner();

	@Query("select c.welcomeMessage from Configuration c")
	String findWelcomeMessage();
	
	@Query("select c.countryCode from Configuration c")
	String findCountryCode();

	@Query("select c.cachedTime from Configuration c")
	Double findCachedTime();

	@Query("select c.finderNumber from Configuration c")
	Integer findFinderNumber();

}
