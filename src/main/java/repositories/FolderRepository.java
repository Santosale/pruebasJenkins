package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Folder;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {
    
//  @Query("select f from Folder f, Actor a where f MEMBER a.folders and a.id = ?1")
  @Query("select f from Folder f where f.actor.id = ?1")
  Collection<Folder> findByActorId(int actorId);
  
//  @Query("select f from Folder f, Actor a where f MEMBER a.folders and a.id = ?1 and f.name = ?2")
  @Query("select f from Folder f where f.actor.id = ?1 and f.name = ?2")
  Folder findByActorIdAndFolderName(int actorId, String folderName);
    
}