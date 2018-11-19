package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	@Query("select c from Customer c where c.userAccount.id = ?1")
	Customer findByUserAccountId(int userAccountId);
	
	@Query("select n, (select (count(s)+0.0)/(select count(c3) from Customer c3) from SubscriptionNewspaper s where s.newspaper.id=n.id) from Newspaper n")
	Collection<Object[]> ratioSuscribersPerPrivateNewspaperVersusNumberCustomers();

}
  