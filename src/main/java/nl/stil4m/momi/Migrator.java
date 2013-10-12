package nl.stil4m.momi;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

import nl.stil4m.momi.exceptions.HashingException;
import nl.stil4m.momi.exceptions.InvalidHashException;
import nl.stil4m.momi.exceptions.InvalidMigrationFileException;
import nl.stil4m.momi.exceptions.MigrationException;
import nl.stil4m.momi.exceptions.MoMiException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;


public class Migrator {

    private final DB db;
    private final MigrationLoader migrationLoader;
    private final DBCollection migrationsCollection;

    public Migrator(DB db, MigrationLoader migrationLoader) {
        this(db, migrationLoader, "MoMiMigrations");
    }

    public Migrator(DB db, MigrationLoader migrationLoader, String collectionName) {
        this.db = db;
        this.migrationLoader = migrationLoader;
        migrationsCollection = db.getCollection(collectionName);
    }

    public void migrate() throws MoMiException {
        List<String> files = migrationLoader.getMigrationFiles();
        for (int i = 0; i < files.size(); i++) {
            String fileName = files.get(i);
            DBObject migration = getMigrationAtIndex(i);
            if (migration != null) {
                checkHash(fileName, migration);
            } else {
                executeMigration(fileName, i);
            }

        }
    }

    private void executeMigration(String fileName, int migrationIndex) throws HashingException, MigrationException, InvalidMigrationFileException {
        File file = migrationLoader.fileForMigration(fileName);
        String fileContent;
        if (!file.exists()) {
            throw new InvalidMigrationFileException(String.format("Migration file does not exist '%s'", fileName));
        }
        try {

            fileContent = Files.toString(file, Charset.availableCharsets().get("UTF-8"));
        } catch (IOException e) {
            throw new InvalidMigrationFileException(String.format("Could not load migration '%s'", fileName));
        }
        String hash = createHash(fileName);
        CommandResult output = db.doEval(fileContent);
        if (output.ok()) {
            persistMigration(hash, migrationIndex);
        } else {
            throw new MigrationException(String.format("Migration of file '%s' at index '%d' failed", fileContent, migrationIndex));
        }
    }

    private void persistMigration(String hash, int migrationIndex) {
        DBObject dbObject = BasicDBObjectBuilder.start().add("hash", hash).add("migrationIndex", migrationIndex).get();
        migrationsCollection.save(dbObject);
    }

    private void checkHash(String fileName, DBObject migration) throws HashingException, InvalidHashException {
        String hash = createHash(fileName);
        if (!hash.equals(migration.get("hash"))) {
            throw new InvalidHashException(String.format("The hash created for '%s' is invalid to the previous known migration", fileName));
        }
    }

    private String createHash(String fileName) throws HashingException {
        String hash;
        File file = migrationLoader.fileForMigration(fileName);
        try {
            HashCode hashedFileContent = Files.hash(file, Hashing.sha1());
            hash = hashedFileContent.toString();
        } catch (IOException e) {
            throw new HashingException("Could not calculate hash for migration-file: " + fileName);
        }
        return hash;
    }

    private DBObject getMigrationAtIndex(int migrationIndex) {
        return migrationsCollection.findOne(QueryBuilder.start("migrationIndex").is(migrationIndex).get());
    }
}
