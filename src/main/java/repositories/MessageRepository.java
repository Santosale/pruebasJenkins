package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

	@Query("SELECT m FROM Message m WHERE m.sender.id = ?1 AND m.folder.actor.id = ?1")
	Collection<Message> findBySenderId(int senderId);
	
	@Query("SELECT m FROM Message m WHERE m.folder.id = ?1")
	Collection<Message> findByFolderId(int folderId);
	
	@Query("select m from Message m where m.folder.actor.id = ?1")
	Collection<Message> findByActorId(int actorId);
	
	@Query("select m from Message m where m.folder.actor.userAccount.id = ?1")
	Page<Message> findByActorUserAccountId(int userAccountId, Pageable pageable);
	
	@Query("SELECT m FROM Message m WHERE m.folder.id = ?1")
	Page<Message> findByFolderIdPaginated(int folderId, Pageable pageable);
	
}
