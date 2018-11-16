
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

	@Query("select c from Contact c where c.explorer.id= ?1")
	Collection<Contact> findByExplorerId(int explorerId);

}
