package com.izzyacademy.data.generators.services;

import com.izzyacademy.data.generators.utils.MySQLUtil;

import java.sql.Connection;

public abstract class BaseMicroService implements DataGeneratorService, AutoCloseable  {

    protected MySQLUtil databaseUtil;

    protected Connection conn;

    public BaseMicroService() {

        this.databaseUtil = new MySQLUtil();

        this.conn = this.databaseUtil.getConnection();
    }

    public BaseMicroService(final String defaultDatabase) {

        this.databaseUtil = new MySQLUtil();

        this.conn = this.databaseUtil.getConnection(defaultDatabase);
    }

    @Override
    public void close() throws Exception {

        this.databaseUtil.close();
    }
}
