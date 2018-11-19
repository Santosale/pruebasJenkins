package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import domain.Actor;
import domain.Raffle;

public interface RaffleRepository extends JpaRepository<Raffle, Integer> {
	
	@Query("select r from Raffle r where r.maxDate > CURRENT_DATE")
	Page<Raffle> findAvailables(Pageable pageable);
	
	@Query("select r from Raffle r where r IN (select t.raffle from Ticket t where t.user.userAccount.id = ?1)")
	Page<Raffle> findByUserAccountId(int userAccountId, Pageable pageable);

	@Query("select r from Raffle r where r.company.userAccount.id = ?1")
	Page<Raffle> findByCompanyAccountId(int userAccountId, Pageable pageable);
	
	@Query("select r from Raffle r where r.maxDate > CURRENT_DATE order by r.maxDate")
	Page<Raffle> findOrderedByMaxDate(Pageable pageable);
	
	@Query("select r from Raffle r")
	Page<Raffle> findAllPaginated(Pageable pageable);
	
	@Query("select t.user from Ticket t where t.raffle.id = ?1 ORDER BY RAND()")
	Page<Actor> toRaffle(int raffleId, Pageable pageable);
	
	@Query("select r from Raffle r where r.maxDate < CURRENT_DATE and (cast((select count(t) from Ticket t where t.raffle.id = r.id) as float)) != 0 and r.winner = null")
	Collection<Raffle> findCanBeRaffled();
	
}
