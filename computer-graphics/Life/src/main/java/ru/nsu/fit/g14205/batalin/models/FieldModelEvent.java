package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.IEvent;

/**
 * Created by kir55rus on 01.03.17.
 */
public enum FieldModelEvent implements IEvent {
    NEXT_STEP,
    CELL_STATE_CHANGED,
    FILED_CLEARED,
}
