
package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Configuration;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {

	@Query("select c from Configuration c")
	Configuration findUnique();

	@Query("select c.name from Configuration c")
	String findName();

	@Query("select c.slogan from Configuration c")
	String findSlogan();

	@Query("select c.email from Configuration c")
	String findEmail();

	@Query("select c.banner from Configuration c")
	String findBanner();

	@Query("select c.defaultImage from Configuration c")
	String findDefaultImage();

	@Query("select c.defaultAvatar from Configuration c")
	String findDefaultAvatar();
}
