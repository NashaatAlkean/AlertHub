package Processor.Utils;

import org.springframework.stereotype.Service;

import java.lang.reflect.Array;

@Service
class ThreshholdCheckerService {
    public String[] checkThreshhold(String[] Actions) {

            for (int i=0; i<Actions.length; i++) {
//                if (Actions[i].threshold < 1) {
//                    Actions[i].pop();
//                }
            }
        return Actions;
    }
}
