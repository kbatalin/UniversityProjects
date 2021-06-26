package pro.batalin.models.db.thread;

import pro.batalin.ddl4j.platforms.Platform;
import pro.batalin.models.ThrowableConsumer;

import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class DBThreadTask {
    private ThrowableConsumer<Platform, SQLException> task;
    private Consumer<SQLException> errorConsumer;

    public DBThreadTask() {
    }


    public DBThreadTask(ThrowableConsumer<Platform, SQLException> task) {
        this.task = task;
    }

    public DBThreadTask(ThrowableConsumer<Platform, SQLException> task, Consumer<SQLException> errorConsumer) {
        this.task = task;
        this.errorConsumer = errorConsumer;
    }

    public ThrowableConsumer<Platform, SQLException> getTask() {
        return task;
    }

    public void setTask(ThrowableConsumer<Platform, SQLException> task) {
        this.task = task;
    }

    public Consumer<SQLException> getErrorConsumer() {
        return errorConsumer;
    }

    public void setErrorConsumer(Consumer<SQLException> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }
}
