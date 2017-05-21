package pro.batalin.models.properties;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class ApplicationPropertiesImpl implements ApplicationProperties {
    private LoginProperties loginProperties;

    public ApplicationPropertiesImpl(LoginProperties loginProperties) {
        this.loginProperties = loginProperties;
    }

    public LoginProperties getLoginProperties() {
        return null;
    }
}
