package Processor.DAO;

import Processor.DAO.Model.EmailTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

interface EmailTopicRepository extends JpaRepository<EmailTopic,Integer> {


}
