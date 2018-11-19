
package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Newspaper;

@Repository
public interface NewspaperRepository extends JpaRepository<Newspaper, Integer> {

	@Query("select n from Newspaper n where n.publisher.id = ?1")
	Page<Newspaper> findByUserId(int userId, Pageable pageable); //Sirve para ver tus newspaper

	@Query("select n from Newspaper n where n IN (select sn.newspaper from SubscriptionNewspaper sn where sn.customer.id=?1) or n IN (select n2 from SubscriptionVolume sv join sv.volume.newspapers n2 where sv.customer.id=?1)")
	Page<Newspaper> findByCustomerId(int customerId, Pageable pageable); //Sirve para ver todas tus subscripciones

	//Lista de que estén publicados y estén públicos
	@Query("select n from Newspaper n where n.publicationDate <=  CURRENT_TIMESTAMP and n.isPrivate=false and n.isPublished=true ")
	Page<Newspaper> findPublicsAndPublicated(Pageable pageable); //Sirve para ver los públicos y publicados (para los anónimos)

	//Todos los newspaper (para los logeados)
	@Query("select n from Newspaper n")
	Page<Newspaper> findAllPaginated(Pageable pageable); //Sirve para ver todos los newspaper, lo usan para añadir artículos o (los no publicados no se verá su display o tendrá muy poca información)

	//Lista de que estén publicados y estén públicos
	@Query("select n from Newspaper n where n.hasTaboo=true")
	Page<Newspaper> findTaboos(Pageable pageable);

	//Lista de newspapers privados y publicados a los que te puedes subscribir
	@Query("select n from Newspaper n where n.id NOT IN (select (s.newspaper.id) from SubscriptionNewspaper s where s.customer.id=?1) and n.isPrivate=true and n.id NOT IN (select (n1) from SubscriptionVolume sv join sv.volume.newspapers n1 where sv.customer.id=?1)")
	Page<Newspaper> findForSubscribe(int customerId, Pageable pageable); //Sirve para suscribirse a un newspaper

	//	@Query(value = "select count(n.id) from Newspaper n where n.id NOT IN (select (s.newspaper_id) from SubscriptionNewspaper s where s.customer_id=?1) and n.isPrivate=true", nativeQuery = true)
	//	Integer countFindForSubscribe(int customerId);

	@Query("select n from Newspaper n where cast((select count(a) from Article a where a.newspaper.id=n.id)as float)>(select avg(cast((select count(a2) from Article a2 where a2.newspaper.id=n2.id)as float))*1.1 from Newspaper n2)")
	Page<Newspaper> find10PercentageMoreAvg(Pageable pageable);

	@Query("select n from Newspaper n where n.publicationDate <= CURRENT_TIMESTAMP and n.isPublished=true and n.isPrivate=false and (n.title like CONCAT('%',?1,'%') or n.description like CONCAT('%',?1,'%'))")
	Page<Newspaper> findPublicsPublishedSearch(String keyWord, Pageable pageable);

	@Query("select n from Newspaper n where n.publicationDate <= CURRENT_TIMESTAMP and n.isPublished=true and (n.title like CONCAT('%',?1,'%') or n.description like CONCAT('%',?1,'%'))")
	Page<Newspaper> findPublishedSearch(String keyWord, Pageable pageable);

	@Query("select n from Newspaper n, Volume v where n.publicationDate <= CURRENT_TIMESTAMP and n.isPublished=true and v.id=?1 and n not member of v.newspapers")
	Page<Newspaper> findAddNewspaper(int volumeId, Pageable pageable);

	@Query("select n from Volume v join v.newspapers n where n.isPrivate=false and v.id=?1")
	Page<Newspaper> findByVolumeAllPublics(int volumeId, Pageable pageable);

	@Query("select DISTINCT n from Newspaper n where n.id NOT IN (select DISTINCT n.id from Newspaper n join n.advertisements a where a.agent.id=?1) ")
	Page<Newspaper> findNewspapersWithNoAdvertisements(int agentId, Pageable pageable);

	@Query("select DISTINCT n from Newspaper n join n.advertisements a where a.agent.id=?1 ")
	Page<Newspaper> findNewspapersWithAdvertisements(int agentId, Pageable pageable);

	@Query("select n from Newspaper n where n.publicationDate <= CURRENT_TIMESTAMP and n.isPublished=true")
	Page<Newspaper> findPublished(Pageable pageable);

	@Query("select n from Newspaper n join n.advertisements a where a.id=?1")
	Collection<Newspaper> findNewspapersToUpdateAdvertisements(int advertisementId);

	@Query("select  avg(cast((select count(n) from Newspaper n where n.publisher.id=u.id) as float )), sqrt(sum((select count(n) from Newspaper n where n.publisher.id=u.id)*(select count(n) from Newspaper n where n.publisher.id=u.id))/(select count(u2) from User u2)-avg(cast((select count(n) from Newspaper n where n.publisher.id=u.id) as float ))*avg(cast((select count(n) from Newspaper n where n.publisher.id=u.id) as float ))) from User u")
	Double[] avgStandarDevNewspapersCreatedPerUser();

	@Query("select n from Newspaper n where cast((select count(a) from Article a where a.newspaper.id=n.id)as float)>(select avg(cast((select count(a2) from Article a2 where a2.newspaper.id=n2.id)as float))*0.9 from Newspaper n2)")
	Page<Newspaper> find10PercentageLessAvg(Pageable pageable);

	@Query("select (cast((select count(n) from Newspaper n where n.isPrivate=true) as float))/count(n2) from Newspaper n2 where n2.isPrivate=false")
	Double ratioPublicVsPrivateNewspaper();

	@Query("select avg(cast((select count(n) from Newspaper n where n.publisher.id = u.id and n.isPrivate = true) as float) / cast((select count(n2) from Newspaper n2 where n2.publisher.id = u.id and n2.isPrivate=false) as float )) from User u")
	Double ratioPrivateVersusPublicNewspaperPerPublisher();

	@Query("select (count(n)*1.0)/(select count(n1)*1.0 from Newspaper n1 where n1.advertisements.size = 0) from Newspaper n where n.advertisements.size > 0")
	Double ratioNewspapersWithOneAdvertisementVsHaventAny();

	@Query("select avg(v.newspapers.size) from Volume v")
	Double averageNewspaperPerVolume();

}
