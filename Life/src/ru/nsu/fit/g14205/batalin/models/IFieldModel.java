package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.IObservable;

/**
 * Created by kir55rus on 27.02.17.
 */
public interface IFieldModel extends IObservable {
    IField getActiveField();
}
