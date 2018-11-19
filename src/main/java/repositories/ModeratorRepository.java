package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Moderator;

@Repository
public interface ModeratorRepository extends JpaRepository<Moderator, Integer> {

	@Query("select m from Moderator m where m.userAccount.id = ?1")
	Moderator findByUserAccountId(final int userAccountId);
	
}
