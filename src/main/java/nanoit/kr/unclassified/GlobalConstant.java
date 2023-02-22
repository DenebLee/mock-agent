package nanoit.kr.unclassified;

import java.text.SimpleDateFormat;

public final class GlobalConstant {
    // TPS
    public static String SMS_TPS = "80";


    // DRIVER
    public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
    public static final String ORACLE_DRIVER = "com.mysql.jdbc.Driver";
    public static final String MYSQL_DRIVER = ""; // not yet
    public static final String MSSQL_DRIVER = ""; // not yet


    // FILE
    public static final String CONFIG_FILE_PATH = System.getProperty("user.dir") + "/config";
    public static final SimpleDateFormat DATE_FOMAT = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
    public static final String FILE_FORMAT = ".xml";
}
