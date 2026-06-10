package com.ua.teamconnect.tracker.extension;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class ShedLockExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private JdbcTemplate jdbcTemplate(ExtensionContext context) {
        var applicationContext = SpringExtension.getApplicationContext(context);
        return applicationContext.getBean(JdbcTemplate.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        jdbcTemplate(context).execute("DROP TABLE shedlock");
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        jdbcTemplate(context).execute("""
        CREATE TABLE shedlock (
          name VARCHAR(64) NOT NULL,
          lock_until TIMESTAMP NOT NULL,
          locked_at TIMESTAMP NOT NULL,
          locked_by VARCHAR(255) NOT NULL,
          PRIMARY KEY (name)
        );
        """);
    }
}
