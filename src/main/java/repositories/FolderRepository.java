package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Folder;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {
    
  @Query("select f from Folder f where f.actor.id = ?1")
  Collection<Folder> findByActorId(int actorId);
  
  @Query("select f from Folder f where f.actor.id = ?1 and f.name = ?2")
  Folder findByActorIdAndFolderName(int actorId, String folderName);
  
  @Query("select f from Folder f where f.actor.userAccount.id = ?1")
  Page<Folder> findByActorUserAccountId(int userAccountId, Pageable pageable);
    
  @Query("select c from Folder f join f.childrenFolders c where f.id = ?1")
  Page<Folder> findChildrenFoldersByFolderId(int folderId, Pageable pageable);
  
}