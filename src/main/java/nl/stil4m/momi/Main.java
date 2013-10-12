package nl.stil4m.momi;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import nl.stil4m.momi.exceptions.MoMiException;

import java.net.UnknownHostException;

public class Main {

    public static void main(String[] argv) throws MoMiException, UnknownHostException {

        MongoClient client = new MongoClient();
        DB db = client.getDB("mig_test");
        ClassPathMigrationLoader classPathMigrationLoader = new ClassPathMigrationLoader();
        Migrator migrator = new Migrator(db, classPathMigrationLoader);
        migrator.migrate();
    }
}
