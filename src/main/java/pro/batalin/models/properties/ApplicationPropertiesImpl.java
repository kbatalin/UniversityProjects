package pro.batalin.models.properties;

import pro.batalin.ddl4j.DatabaseOperationException;
import pro.batalin.ddl4j.model.Schema;
import pro.batalin.ddl4j.platforms.Platform;
import pro.batalin.ddl4j.platforms.PlatformFactory;
import pro.batalin.ddl4j.platforms.PlatformFactoryException;
import pro.batalin.models.db.Schemas;
import pro.batalin.models.observe.ObservableBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ApplicationPropertiesImpl extends ObservableBase implements ApplicationProperties {
    private LoginProperties loginProperties;
    private Platform platform;
    private Connection connection;
    private Schemas schemas;

    public ApplicationPropertiesImpl(LoginProperties loginProperties) throws ClassNotFoundException {
        this.loginProperties = loginProperties;
        this.schemas = new Schemas(this);

        //DB
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }

    @Override
    public LoginProperties getLoginProperties() {
        return loginProperties;
    }

    @Override
    public Schemas getSchemas() {
        return schemas;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if(connection == null) {
            synchronized (ApplicationProperties.class) {
                if (connection == null) {
                    String url = loginProperties.getConnectionString();
                    String username = loginProperties.getUsername();
                    String password = loginProperties.getPassword();
                    connection = DriverManager.getConnection(url, username, password);
                }
            }
        }

        return connection;
    }

    @Override
    public Platform getPlatform() throws SQLException, PlatformFactoryException {
        if(platform == null) {
            synchronized (Platform.class) {
                if (platform == null) {
                    PlatformFactory factory = new PlatformFactory();
                    platform = factory.create("ORACLE", getConnection());
                }
            }
        }

        return platform;
    }
}
