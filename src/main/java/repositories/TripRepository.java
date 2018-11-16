
package repositories;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Trip;

@Repository
public interface TripRepository extends JpaRepository<Trip, Integer> {

	//Explorer finder
	//	@Query("select t from Trip t where t.price <= ?1 and t.price >= ?2 and t.startDate >= ?3 and t.endDate <= ?4 and t.publicationDate <= CURRENT_DATE and (t.title like CONCAT('%',?5,'%') or t.description like CONCAT('%',?5,'%') or t.ticker like CONCAT('%',?5,'%'))")
	//	Collection<Trip> findByPriceRangeAndDateRangeAndKeyword(Double maxPrice, Double minPrice, Date startDate, Date finishDate, String keyWord);

	// Para paginar el finder
	@Query("select t from Trip t where t.price <= ?1 and t.price >= ?2 and t.startDate >= ?3 and t.endDate <= ?4 and t.publicationDate <= CURRENT_DATE and (t.title like CONCAT('%',?5,'%') or t.description like CONCAT('%',?5,'%') or t.ticker like CONCAT('%',?5,'%'))")
	Page<Trip> findByPriceRangeAndDateRangeAndKeyword(Double maxPrice, Double minPrice, Date startDate, Date finishDate, String keyWord, Pageable pageable);

	@Query("select  avg(cast((select count(t) from Trip t where t.manager=m and t.publicationDate <= CURRENT_DATE) as float )), min(cast((select count(t) from Trip t where t.manager=m and t.publicationDate <= CURRENT_DATE) as int )), max(cast((select count(t) from Trip t where t.manager=m and t.publicationDate <= CURRENT_DATE) as int )), sqrt(sum((select count(t) from Trip t where t.manager=m and t.publicationDate <= CURRENT_DATE)*(select count(t) from Trip t where t.manager=m and t.publicationDate <= CURRENT_DATE))/(select count(m2) from Manager m2)-avg(cast((select count(t) from Trip t where t.manager=m and t.publicationDate <= CURRENT_DATE) as float ))*avg(cast((select count(t) from Trip t where t.manager=m and t.publicationDate <= CURRENT_DATE) as float ))) from Manager m")
	Double[] avgMinMaxStandardDTripsPerManager();

	@Query("select avg(t.price), min(t.price), max(t.price), sqrt(sum(t.price*t.price)/count(t.price) - (avg(t.price) * avg(t.price))) from Trip t where t.publicationDate <= CURRENT_DATE")
	Double[] avgMinMaxStandardDPriceOfTrips();

	@Query("select  avg(cast((select count(t) from Trip t where t.ranger=r and t.publicationDate <= CURRENT_DATE) as float )), min(cast((select count(t) from Trip t where t.ranger=r and t.publicationDate <= CURRENT_DATE) as int )), max(cast((select count(t) from Trip t where t.ranger=r and t.publicationDate <= CURRENT_DATE) as int )), sqrt(sum((select count(t) from Trip t where t.ranger=r and t.publicationDate <= CURRENT_DATE)*(select count(t) from Trip t where t.ranger=r and t.publicationDate <= CURRENT_DATE))/(select count(r2) from Ranger r2)-avg(cast((select count(t) from Trip t where t.ranger=r and t.publicationDate <= CURRENT_DATE) as float ))*avg(cast((select count(t) from Trip t where t.ranger=r and t.publicationDate <= CURRENT_DATE) as float ))) from Ranger r")
	Double[] avgMinMaxStandardDTripsPerRanger();

	@Query("select cast(count(t) as float) / (select count(t) from Trip t where t.publicationDate <= CURRENT_DATE) from Trip t where t.cancellationReason is not null and t.publicationDate <= CURRENT_DATE")
	Double ratioTripCancelledVsTotal();

	//@Query("select t, (select count(a) from Application a where t IN a.trip) as tam from Trip t where (select count(a) from Application a where t IN a.trip) > (select count(a) from Application a) / (select count(t) from Trip t)*1.1 ORDER BY tam ASC")
	@Query("select t, (select count(a) from Application a where t IN a.trip) as tam from Trip t where t.publicationDate <= CURRENT_DATE and (select count(a) from Application a where t IN a.trip) > (select count(a) from Application a where a.trip.publicationDate <= CURRENT_DATE) / (select count(t) from Trip t where t.publicationDate <= CURRENT_DATE)*1.1 ORDER BY tam ASC")
	List<Object[]> findTenPerCentMoreApplicationThanAverage();

	@Query("select count(t)/((select count(t2) from Trip t2 where t2.publicationDate <= CURRENT_DATE)+0.0) from Trip t where cast((select count(a) from Audit a where a.trip=t) as int )=1 and t.publicationDate <= CURRENT_DATE")
	Double ratioTripOneAuditRecordVsTotal();

	//All visible trips
	@Query("select t from Trip t where t.publicationDate <= CURRENT_DATE")
	Collection<Trip> findAllVisible();

	// A+
	@Query("select t from Trip t where t.publicationDate <= CURRENT_DATE")
	Page<Trip> findAllVisible(Pageable pageable);

	//Finder not authenticate
	@Query("select t from Trip t where (t.title like CONCAT('%',?1,'%') or t.description like CONCAT('%',?1,'%') or t.ticker like CONCAT('%',?1,'%')) and t.publicationDate <= CURRENT_DATE")
	Collection<Trip> findByKeyWord(String keyWord);

	@Query("select t from Trip t where t.legalText.id= ?1")
	Collection<Trip> findByLegalTextId(int legalTextId);

	@Query("select t.legalText, count(t) from Trip t where t.publicationDate <= CURRENT_DATE group by t.legalText")
	List<Object[]> countLegalTextReferences();

	@Query("select t from Trip t where t.manager.userAccount.id= ?1")
	Collection<Trip> findByManagerUserAccountId(int managerUserAccountId);

	@Query("select t from Trip t where t.ranger.userAccount.id= ?1")
	Collection<Trip> findByRangerUserAccountId(int rangerUserAccountId);

	@Query("select t from Trip t where t.category.id= ?1 and t.publicationDate <= CURRENT_DATE")
	Collection<Trip> findByCategoryId(int categoryId);

	@Query("select t from Trip t where t.category.id= ?1")
	Collection<Trip> findByCategoryIdAllTrips(int categoryId);

}
