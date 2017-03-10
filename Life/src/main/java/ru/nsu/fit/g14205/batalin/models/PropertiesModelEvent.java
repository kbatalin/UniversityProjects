package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.IEvent;

/**
 * Created by kir55rus on 27.02.17.
 */
public enum PropertiesModelEvent implements IEvent {
    HEX_SIZE_CHANGED,
    FIELD_SIZE_CHANGED,
    IMPACT_VISIBLE_CHANGED,
    PAINTING_MODE_CHANGED,
}
