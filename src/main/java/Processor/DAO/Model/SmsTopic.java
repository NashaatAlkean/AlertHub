package Processor.DAO.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.stereotype.Component;

@Component
@Entity
public class SmsTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;


    public String toString() {
    return "";
    }
    public void sendSMS(String id) {
        this.id = id;
    }
}
