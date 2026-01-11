package Logger.Service;

import Logger.DAO.Model.LogEntity;
import Logger.DAO.Model.LogType;
import Logger.DAO.Repository.LogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {

    private final LogRepository repository;

    public LogService(LogRepository repository) {
        this.repository = repository;
    }

    public List<LogEntity> getByType(LogType type) {
        return repository.findByType(type);
    }

    public List<LogEntity> getAll() {
        return repository.findAll();
    }
}