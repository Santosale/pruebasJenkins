
package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Volume;

@Repository
public interface VolumeRepository extends JpaRepository<Volume, Integer> {

	@Query("select v from Volume v where v.user.userAccount.id = ?1")
	Page<Volume> findByUserAccountId(int id, Pageable page);

	@Query("select v from Volume v join v.newspapers n where n.id=?1")
	Collection<Volume> findByNewspaperId(int newspaperId);

	@Query("select s.volume from SubscriptionVolume s join s.volume.newspapers n where n.id=?2 and s.customer.id=?1")
	Collection<Volume> findByCustomerIdAndNewspaperId(int customerId, int newspaperId);

	@Query("select v from Volume v")
	Page<Volume> findAllPaginated(Pageable page);
}
