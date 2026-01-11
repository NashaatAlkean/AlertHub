package Logger.DAO.Repository;

import Logger.DAO.Model.LogEntity;
import Logger.DAO.Model.LogType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, Long> {
    List<LogEntity> findByType(LogType type);
}
