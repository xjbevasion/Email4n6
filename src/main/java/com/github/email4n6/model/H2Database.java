/*
 * This file is part of Email4n6.
 * Copyright (C) 2018  Marten4n6
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.email4n6.model;

import com.github.email4n6.model.casedao.Case;
import com.github.email4n6.utils.OSUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.application.Platform;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database thread pool with HikariCP (not sure if this is over-kill). <br/>
 * Since autoCommit is set to false make sure to commit. <br/>
 * Note: Even SELECT queries initiate transactions and acquire locks.
 *
 * @author Marten4n6
 * @see <a href="https://github.com/brettwooldridge/HikariCP">HikariCP</a>
 */
@Slf4j
public class H2Database {

    private HikariDataSource dataSource;

    public H2Database(String caseName) {
        HikariConfig config = new HikariConfig();
        config.setAutoCommit(false);
        config.setJdbcUrl("jdbc:h2:" + OSUtils.getCasePath(caseName) + File.separator + "Email4n6");

        dataSource = new HikariDataSource(config);

        try {
            @Cleanup Connection connection = dataSource.getConnection();
            @Cleanup Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS Bookmarks(ID VARCHAR(100) PRIMARY KEY)");
            statement.execute("CREATE TABLE IF NOT EXISTS Tags(ID VARCHAR(100) PRIMARY KEY, Tag VARCHAR(100))");
            statement.execute("CREATE TABLE IF NOT EXISTS Emails(Whatever VARCHAR(100) PRIMARY KEY)");
            connection.commit();
        } catch (SQLException ex) {
            log.error(ex.getMessage(), ex);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // TODO - Is a shutdown hook the best way? Probably not.
            dataSource.close();
        }));
    }

    /**
     * @return The HikariCP data source.
     */
    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
