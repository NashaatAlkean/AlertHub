package Processor.DAO;

import Processor.DAO.Model.EmailTopic;
import org.springframework.data.jpa.repository.JpaRepository;

interface EmailTopicRepository extends JpaRepository<EmailTopic,Integer> {

     void SendEmailToSinglePerson(EmailTopic email);
}
