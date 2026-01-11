package Logger.DAO.Model;

public record LogMessage(
        LogType type,
        String message
) {}