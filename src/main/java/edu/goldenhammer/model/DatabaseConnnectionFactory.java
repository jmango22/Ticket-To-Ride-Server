package edu.goldenhammer.model;

import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;
import java.sql.*;
/**
 * Created by devonkinghorn on 2/1/17.
 */
public class DatabaseConnnectionFactory {
    private static final String POSTGRESQL_DRIVER_NAME = "org.postgresql.Driver";
    private String schema;
    private String url;
    private String userName;
    private String password;

    public DatabaseConnnectionFactory(String host, String database, String schema, String userName, String password) {
        this.url = String.format("jdbc:postgresql://%s:5432/%s", host, database);
        this.schema = schema;
        this.userName = userName;
        this.password = password;
        try {
            Class.forName(POSTGRESQL_DRIVER_NAME);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(url, userName, password);
            if (schema != null) {
                conn.setSchema(schema);
            }
            return conn;
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to get connection");
        }
    }
}
