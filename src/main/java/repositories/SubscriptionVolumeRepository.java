
package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.SubscriptionVolume;

@Repository
public interface SubscriptionVolumeRepository extends JpaRepository<SubscriptionVolume, Integer> {

	@Query("select s from SubscriptionVolume s where s.customer.id = ?1")
	Page<SubscriptionVolume> findByCustomerId(int id, Pageable page);

	@Query("select s from SubscriptionVolume s where s.customer.id = ?1 and s.volume.id = ?2")
	SubscriptionVolume findByCustomerIdAndVolumeId(int customerId, int volumeId);

	@Query("select s from SubscriptionVolume s where s.volume.id = ?1")
	Collection<SubscriptionVolume> findByVolumeId(int id);

	@Query("select (cast((select count(sv) from SubscriptionVolume sv) as float))/count(sn) from SubscriptionNewspaper sn")
	Double ratioSubscritionVolumeVsSubscriptionNewspaper();

}
