package nl.stil4m.momi;

import nl.stil4m.momi.exceptions.InvalidMigrationFileException;

import java.io.File;
import java.util.List;

public interface MigrationLoader {

    List<String> getMigrationFiles() throws InvalidMigrationFileException;

    File fileForMigration(String filePath);

}
