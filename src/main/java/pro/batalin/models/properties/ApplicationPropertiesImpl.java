package pro.batalin.models.properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ApplicationPropertiesImpl implements ApplicationProperties {
    private LoginProperties loginProperties;

    public ApplicationPropertiesImpl(LoginProperties loginProperties) {
        this.loginProperties = loginProperties;
    }

    @Override
    public LoginProperties getLoginProperties() {
        return loginProperties;
    }
}
