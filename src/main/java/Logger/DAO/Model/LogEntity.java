package Logger.DAO.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Logs")
@Getter @Setter
public class LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LogType type;

    @Column(nullable = false)
    private String message;

    private LocalDateTime createdAt;

    public LogEntity(LogType type, String message) {
        this.type = type;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public LogEntity() {

    }
}