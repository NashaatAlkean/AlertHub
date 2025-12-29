package Processor.Utils;

import org.springframework.stereotype.Service;

import java.lang.reflect.Array;

@Service
class ThreshholdCheckerService {
    public boolean checkThreshhold(String message) {


        String[] Actions =   {"1","2","3"};
            for (int i=0; i<Actions.length; i++) {
                if (!Actions[i].equals(message)) {
                    return false;
                }
            }
        return true;
    }
}
