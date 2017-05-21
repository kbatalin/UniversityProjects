package pro.batalin.models.properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ApplicationPropertiesImpl implements ApplicationProperties {
    private LoginProperties loginProperties;

    public ApplicationPropertiesImpl(LoginProperties loginProperties) throws ClassNotFoundException {
        this.loginProperties = loginProperties;

        //db
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }

    @Override
    public LoginProperties getLoginProperties() {
        return loginProperties;
    }

    @Override
    public Connection getConnection() throws SQLException {
        String url = loginProperties.getConnectionString();
        String username = loginProperties.getUsername();
        String password = loginProperties.getPassword();

        return DriverManager.getConnection(url, username, password);
    }
}
