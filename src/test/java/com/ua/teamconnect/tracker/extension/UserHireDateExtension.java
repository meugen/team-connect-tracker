package com.ua.teamconnect.tracker.extension;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class UserHireDateExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private JdbcTemplate jdbcTemplate(ExtensionContext context) {
        var applicationContext = SpringExtension.getApplicationContext(context);
        return applicationContext.getBean(JdbcTemplate.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        jdbcTemplate(context).execute("DROP VIEW user_hire_date");
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        jdbcTemplate(context).execute("""
        CREATE VIEW user_hire_date AS SELECT user_id, 
            min(start_date) hire_date
            FROM user_position GROUP BY user_id;
        """);
    }
}
