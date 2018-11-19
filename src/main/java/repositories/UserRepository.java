
package repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select u from User u where u.userAccount.id = ?1")
	User findByUserAccountId(int id);
	
	@Query("select f from User u join u.followers f where u.id = ?1")
	Page<User> findFollowersByUserId(int userId, Pageable page);
	
	@Query("select u from User u where (select u2 from User u2 where u2.id = ?1) member of u.followers")
	Page<User> findFollowedsByUserId(int userId, Pageable page);

	@Query("select u.followers.size from User u where u.id = ?1")
	Integer countFollowersByUserId(int userId);
	
	@Query("select count(u) from User u where (select u2 from User u2 where u2.id = ?1) member of u.followers")
	Integer countFollowedsByUserId(int userId);
	
	@Query("select u from User u")
	Page<User> findAllPaginated(Pageable page);
	
	@Query("select avg(cast((select count(a) from Article a where a.writer.id=u.id) as float) / cast((select count(u2) from User u2) as float )) from User u)")
	Double ratioUserWhoHaveWrittenArticle();
	
	@Query("select cast(count(u) as float)/(select count(u3) from User u3) from User u where cast((select count(c) from Chirp c where c.user.id=u.id)as float)>(select avg(cast((select count(c2) from Chirp c2 where c2.user.id=u2.id)as float))*1.75 from User u2)")
	Double ratioUserWhoHavePostedAbove75Chirps();
	
	@Query("select cast((count(distinct n.publisher)) as float)/(select count(u) from User u) from Newspaper n")
	Double ratioUsersWhoHaveCreatedNewspaper();
	
}
