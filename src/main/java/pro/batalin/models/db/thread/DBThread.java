package pro.batalin.models.db.thread;

import pro.batalin.ddl4j.platforms.Platform;
import pro.batalin.ddl4j.platforms.PlatformFactory;
import pro.batalin.ddl4j.platforms.PlatformFactoryException;
import pro.batalin.models.ThrowableConsumer;
import pro.batalin.models.properties.ApplicationProperties;
import pro.batalin.models.properties.LoginProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class DBThread extends Thread {
    private Platform platform;
    private Connection connection;
    private LinkedBlockingQueue<DBThreadTask> tasks = new LinkedBlockingQueue<>();

    public DBThread() throws ClassNotFoundException, SQLException, PlatformFactoryException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }

    public void connect(LoginProperties loginProperties) throws SQLException, PlatformFactoryException {
        if(connection == null) {
            synchronized (DBThread.class) {
                if (connection == null) {
                    reconnect(loginProperties);
                }
            }
        }
    }

    public void reconnect(LoginProperties loginProperties) throws SQLException, PlatformFactoryException {
        String url = loginProperties.getConnectionString();
        String username = loginProperties.getUsername();
        String password = loginProperties.getPassword();
        connection = DriverManager.getConnection(url, username, password);

        platform = getPlatform();
    }

    public boolean addTask(ThrowableConsumer<Platform, SQLException> task) {
        return addTask(new DBThreadTask(task));
    }

    public boolean addTask(ThrowableConsumer<Platform, SQLException> task, Consumer<SQLException> errorConsumer) {
        return addTask(new DBThreadTask(task, errorConsumer));
    }

    private boolean addTask(DBThreadTask task) {
        return tasks.offer(task);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            DBThreadTask task = null;
            try {
                task = tasks.take();
            } catch (InterruptedException e) {
                if (isInterrupted()) {
                    return;
                }
            }

            if (task == null) {
                continue;
            }

            try {
                task.getTask().accept(platform);
            } catch (SQLException e) {
                if (task.getErrorConsumer() != null) {
                    task.getErrorConsumer().accept(e);
                }
            }

            //todo: remove
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Platform getPlatform() throws SQLException, PlatformFactoryException {
        if(platform == null) {
            synchronized (Platform.class) {
                if (platform == null) {
                    PlatformFactory factory = new PlatformFactory();
                    platform = factory.create("ORACLE", connection);
                }
            }
        }

        return platform;
    }

}