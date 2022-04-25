package cn.utstarcom.rtca.flink.util;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceUtils {
    private DataSourceUtils() {

    }

    private static class DataSourceSingleton {
        private static final DataSource dataSource;

        static {
            String filePath = System.getProperty("user.dir");
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                filePath += "/src/main";
            }
            System.setProperty("hikaricp.configurationFile", filePath +
                    "/config/hikaricp.properties");
            dataSource = new HikariDataSource();
        }
    }

    public static DataSource getDataSource() {
        return DataSourceSingleton.dataSource;
    }
}
