
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

	@Query("select cast(count(a) as float)/(select count(a) from Application a) from Application a where a.status='PENDING' and a.trip.publicationDate <= CURRENT_DATE")
	Double ratioApplicationsWithStatusPending();

	@Query("select cast(count(a) as float)/(select count(a) from Application a) from Application a where a.status='DUE' and a.trip.publicationDate <= CURRENT_DATE")
	Double ratioApplicationsWithStatusDue();

	@Query("select cast(count(a) as float)/(select count(a) from Application a) from Application a where a.status='ACCEPTED' and a.trip.publicationDate <= CURRENT_DATE")
	Double ratioApplicationsWithStatusAccepted();

	@Query("select cast(count(a) as float)/(select count(a) from Application a) from Application a where a.status='CANCELLED' and a.trip.publicationDate <= CURRENT_DATE")
	Double ratioApplicationsWithStatusCancelled();

	@Query("select  avg(cast((select count(a) from Application a where a.trip=t) as float )), min(cast((select count(a) from Application a where a.trip=t) as int )), max(cast((select count(a) from Application a where a.trip=t) as int )), sqrt(sum((select count(a) from Application a where a.trip=t)*(select count(a) from Application a where a.trip=t))/(select count(t2) from Trip t2 where t2.publicationDate <= CURRENT_DATE)-avg(cast((select count(a) from Application a where a.trip=t) as float ))*avg(cast((select count(a) from Application a where a.trip=t) as float ))) from Trip t where t.publicationDate <= CURRENT_DATE")
	Double[] avgMinMaxStandardDNumberApplications();

	@Query("select ap from Application ap where ap.applicant.id= ?1")
	Collection<Application> findByExplorerId(int explorerId);

	@Query("select ap from Application ap where ap.trip.id= ?1")
	Collection<Application> findByTripId(int tripId);

	@Query("select ap from Application ap where ap.trip.id= ?1 and ap.applicant.id= ?2")
	Application findByTripIdAndExplorerId(int tripId, int explorerId);

	@Query("select a from Application a where a.trip.manager.id= ?1")
	Collection<Application> findByManagerId(int managerId);
}
