package marketplace.db.repository;

import marketplace.db.entity.ProjectRecord;
import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<ProjectRecord, Long> {

}
