package Logger.Controller;

import Logger.DAO.Model.LogEntity;
import Logger.DAO.Model.LogType;
import Logger.Service.LogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/logger")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/email")
    public List<LogEntity> getEmailLogs() {
        return logService.getByType(LogType.EMAIL);
    }

    @GetMapping
    public List<LogEntity> getAllLogs() {
        return logService.getAll();
    }
}
