package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import domain.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

	@Query("select count(t) from Ticket t where t.creditCard != null and t.creditCard.id = ?1")
	Integer countByCreditCardId(int creditCardId);
	
	@Query("select count(t) from Ticket t where t.raffle.id = ?1")
	Integer countByRaffleId(int raffleId);
	
	@Query("select count(t) from Ticket t where t.raffle.id = ?1 and t.user.id = ?2")
	Integer countByRaffleIdAndUserId(int raffleId, int userId);
	
	@Query("select t from Ticket t where t.user.userAccount.id = ?1")
	Page<Ticket> findByUserAccountId(int userAccountId, Pageable pageable);
	
	@Query("select t from Ticket t where t.user.userAccount.id = ?2 and t.raffle.id = ?1")
	Page<Ticket> findByRaffleIdAndUserAccountId(int raffleId, int userAccountId, Pageable pageable);
	
	@Query("select t from Ticket t where t.raffle.id = ?1")
	Collection<Ticket> findByRaffleId(int raffleId);
	
	// Dashboard
	@Query("select avg(cast((select count(t) from Ticket t where t.raffle.id=r.id) as float)) from Raffle r")
	Double avgTicketsPurchaseByUsersPerRaffle();
	
}
