package org.hoverla.bibernate.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EntityActionPriority {
    INSERT(1), UPDATE(2), DELETE(3);
    private final int priority;
}
