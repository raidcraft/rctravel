package de.raidcraft;

import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;
import io.ebeaninternal.dbmigration.DefaultDbMigration;

import java.io.IOException;

public class GenerateDbMigration {

    /**
     * Generate the next "DB schema DIFF" migration.
     * <p>
     * These migration are typically run using FlywayDB, Liquibase
     * or Ebean's own built in migration runner.
     * </p>
     */
    public static void main(String[] args) throws IOException {

        // generate a migration using drops from a prior version
        //System.setProperty("ddl.migration.pendingDropsFor", "1.2");

        DbMigration dbMigration = new DefaultDbMigration();
        dbMigration.setPlatform(Platform.MYSQL);
        dbMigration.setName("initial");
        dbMigration.setVersion("1.0");
        // generate the migration ddl and xml
        // ... with EbeanServer in "offline" mode
        String script = dbMigration.generateMigration();
    }
}