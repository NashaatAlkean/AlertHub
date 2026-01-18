package com.alerthub.actionservice.service;
import com.alerthub.actionservice.enums.RunOnDay;
import com.alerthub.actionservice.exception.ActionNotFoundException;
import com.alerthub.actionservice.exception.InvalidActionException;
import com.alerthub.actionservice.model.Action;
import com.alerthub.actionservice.enums.RunOnDay;

import com.alerthub.actionservice.repository.ActionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService{

    private final ActionRepository actionRepository;

    @Override
    public Action create(Action action) {
        validateAction(action);
        return actionRepository.save(action);

    }

    @Override
    public Action update(UUID id, Action updatedAction) {
        Action existing = getById(id);
        existing.setName(updatedAction.getName());
        existing.setCondition(updatedAction.getCondition());
        existing.setActionType(updatedAction.getActionType());
        existing.setRecipient(updatedAction.getRecipient());
        existing.setMessage(updatedAction.getMessage());
        existing.setRunOnTime(updatedAction.getRunOnTime());
        existing.setRunonday(updatedAction.getRunonday());

        validateAction(existing);

        return actionRepository.save(existing);

    }


    @Override
    public Action getById(UUID id) {
        return actionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ActionNotFoundException("Action not found with id: " + id)
                );
    }

    @Override
    public List<Action> getAll() {
        return actionRepository.findByDeletedFalse();
    }

    @Override
    public void softDelete(UUID id) {
        Action action=getById(id);
        action.setDeleted(true);
        actionRepository.save(action);

    }

    @Override
    public Action setEnabled(UUID id, boolean enabled) {
        return null;
    }
    // ===== SCHEDULING =====

    @Override
    public List<Action> getActionsToRun(LocalTime time, RunOnDay day) {
        return actionRepository.findActionsToRun(time, day);
    }

    // ===== VALIDATION =====

    private void validateAction(Action action) {

        if (action.getRunOnTime().getMinute() % 30 != 0) {
            throw new InvalidActionException(
                    "runOnTime must be on a full or half hour"
            );
        }

        if (action.getCondition() == null || action.getCondition().isBlank()) {
            throw new InvalidActionException("Condition matrix cannot be empty");
        }
    }

}
