package lk.icbt.dentalclinic.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Singleton pattern: ensures exactly one connection pool exists
 * for the whole application, avoiding the overhead of repeatedly
 * opening/closing raw JDBC connections.
 */
public class DatabaseConnectionManager {

    private static DatabaseConnectionManager instance;
    private final HikariDataSource dataSource;

    // private constructor -> prevents external instantiation
    private DatabaseConnectionManager() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/dental_clinic_db");
        config.setUsername("root");           
        config.setPassword("37333733");  
        config.setMaximumPoolSize(10);
        this.dataSource = new HikariDataSource(config);
    }

    // global access point - lazy initialization, thread-safe via synchronized
    public static synchronized DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}