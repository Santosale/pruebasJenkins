
package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.Identifier;

@Repository
public interface IdentifierRepository extends JpaRepository<Identifier, Integer> {

}
