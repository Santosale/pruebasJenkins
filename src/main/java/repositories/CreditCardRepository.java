package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.CreditCard;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {

	@Query("select c from CreditCard c where c.user.userAccount.id = ?1")
	Page<CreditCard> findByUserAccountId(int userAccountId, Pageable pageable);
	
	@Query("select c from CreditCard c where c.user.userAccount.id = ?1")
	Collection<CreditCard> findByUserAccountId(int userAccountId);
	
	@Query("select c from CreditCard c where c.user.userAccount.id = ?1 and ((c.expirationYear = ?2 and c.expirationMonth > ?3) or (c.expirationYear > ?2))")
	Collection<CreditCard> findValidByUserAccountId(int userAccountId, int year, int month);
	
}
