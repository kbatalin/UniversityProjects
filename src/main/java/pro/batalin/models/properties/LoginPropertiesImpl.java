package pro.batalin.models.properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Kirill Batalin (kir55rus).
 */
public class LoginPropertiesImpl implements LoginProperties {
    private String hostname;
    private String port;
    private String sid;
    private String username;
    private String password;

    public LoginPropertiesImpl() throws ClassNotFoundException {
        this("localhost", "1521", "xe", null, null);
    }

    public LoginPropertiesImpl(String hostname, String port, String sid, String username, String password) throws ClassNotFoundException {
        this.hostname = hostname;
        this.port = port;
        this.sid = sid;
        this.username = username;
        this.password = password;

        //db
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String getPort() {
        return port;
    }

    @Override
    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String getSid() {
        return sid;
    }

    @Override
    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getConnectionString() {
        return "jdbc:oracle:thin:@" + hostname + ":" + port + ":" + sid;
    }

    @Override
    public Connection getConnection() throws SQLException {
        String url = getConnectionString();
        return DriverManager.getConnection(url, username, password);
    }
}
