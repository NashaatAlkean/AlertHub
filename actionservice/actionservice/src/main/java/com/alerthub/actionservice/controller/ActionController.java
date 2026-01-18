package com.alerthub.actionservice.controller;

import com.alerthub.actionservice.dto.ActionRequest;
import com.alerthub.actionservice.dto.ActionResponse;
import com.alerthub.actionservice.model.Action;
import com.alerthub.actionservice.service.ActionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/actions")
@RequiredArgsConstructor
public class ActionController {


    private final ActionService actionService;


    //create a new action
    @PostMapping
    public ActionResponse createAction(
            @Valid @RequestBody ActionRequest request
    ) {
        Action action = mapToEntity(request);
        Action created = actionService.create(action);
        return mapToResponse(created);
    }

    //get action by id
    @GetMapping("/{id}")
    public ActionResponse getActionById(
            @PathVariable UUID id
    ) {
        return mapToResponse(actionService.getById(id));
    }
    //get all
    @GetMapping
    public List<ActionResponse> getAllActions() {
        return actionService.getAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //update actions by {id}

    @PutMapping("/{id}")
    public ActionResponse updateAction(
            @PathVariable UUID id,
            @Valid @RequestBody ActionRequest request
    ) {
        Action updated = mapToEntity(request);
        return mapToResponse(actionService.update(id, updated));
    }
    //  Soft delete an Action.
    @DeleteMapping("/{id}")
    public void deleteAction(
            @PathVariable UUID id
    ) {
        actionService.softDelete(id);
    }

    //Enable or disable an Action.
    @PatchMapping("/{id}/enabled")
    public ActionResponse setEnabled(
            @PathVariable UUID id,
            @RequestParam boolean enabled
    ) {
        return mapToResponse(actionService.setEnabled(id, enabled));
    }

    // =========================
    // Mapping helpers
    // =========================

    /**
     * Converts ActionRequest DTO to Action entity.
     */
    private Action mapToEntity(ActionRequest request) {
        return Action.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .condition(request.getCondition())
                .actionType(request.getActionType())
                .recipient(request.getRecipient())
                .message(request.getMessage())
                .runOnTime(request.getRunOnTime())
                .runonday(request.getRunonday())
                .build();
    }

    /**
     * Converts Action entity to ActionResponse DTO.
     */
    private ActionResponse mapToResponse(Action action) {
        return ActionResponse.builder()
                .id(action.getId())
                .userId(action.getUserId())
                .name(action.getName())
                .condition(action.getCondition())
                .actionType(action.getActionType())
                .recipient(action.getRecipient())
                .message(action.getMessage())
                .runOnTime(action.getRunOnTime())
                .runonday(action.getRunonday())
                .enabled(action.isEnabled())
                .createDate(action.getCreateDate())
                .lastUpdate(action.getLastUpdate())
                .build();
    }



}
