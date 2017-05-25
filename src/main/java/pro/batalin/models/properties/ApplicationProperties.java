package pro.batalin.models.properties;


import pro.batalin.models.db.Schemas;
import pro.batalin.models.db.TableData;
import pro.batalin.models.db.Tables;
import pro.batalin.models.db.thread.DBThread;
import pro.batalin.models.observe.Observable;
import pro.batalin.models.observe.ObserveEvent;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public interface ApplicationProperties extends Observable {
    LoginProperties getLoginProperties();

    Schemas getSchemas();

    Tables getTables();

    TableData getTableData();

    DBThread getDBThread();

    enum Event implements ObserveEvent {
    }
}
