package com.alerthub.actionservice.service;

import com.alerthub.actionservice.model.Action;
import com.alerthub.actionservice.enums.RunOnDay;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ActionService {

    // ===== CRUD =====

    Action create(Action action);

    Action update(UUID id, Action updatedAction);

    Action getById(UUID id);

    List<Action> getAll();

    void softDelete(UUID id);

    // ===== ENABLE / DISABLE =====

    Action setEnabled(UUID id, boolean enabled);

    // ===== SCHEDULING =====

    List<Action> getActionsToRun(LocalTime time, RunOnDay day);
}
