package com.project.qa.utils.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.project.qa.utils.db.DbExecutor.connect;

/**
 * Created by Evgeny.Gurinovich on 09.06.2016.
 */
public class SqlRequestSender {

    static Logger LOGGER = LoggerFactory.getLogger(SqlRequestSender.class);
    private static volatile Boolean procedureUpdated = false;
    static Object objectMonitor = new Object();

    public static String getSomeResult() {
        String request = "Some SQL request";
        Map<String, List<String>> results = connect().to(DbExecutor.DB.READ_NATURALLY)
                .getValuesFromResultSet(request, "columnName");
        return (results.size() > 0) ? results.get("columnName").get(0) : null;
    }

}
