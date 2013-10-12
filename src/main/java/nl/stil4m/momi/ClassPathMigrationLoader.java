package nl.stil4m.momi;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import nl.stil4m.momi.exceptions.InvalidMigrationFileException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class ClassPathMigrationLoader implements MigrationLoader {

    public File contextFile;
    private String migrationFile;

    public ClassPathMigrationLoader() {
        this(ClassPathMigrationLoader.class, "/");
    }

    public ClassPathMigrationLoader(String context) {
        this(ClassPathMigrationLoader.class, context);
    }

    public ClassPathMigrationLoader(String context, String migrationFile) {
        this(ClassPathMigrationLoader.class, context, migrationFile);
    }


    public ClassPathMigrationLoader(Class contextClazz, String context) {
        this(contextClazz, context, "migrations.txt");
    }

    public ClassPathMigrationLoader(Class contextClazz, String context, String migrationFile) {
        this.migrationFile = migrationFile;
        try {
            contextFile = new File(contextClazz.getResource(context).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not initialize class path migration loader");
        }
    }


    @Override
    public List<String> getMigrationFiles() throws InvalidMigrationFileException {
        try {
            File file = new File(contextFile, migrationFile);
            String content = Files.toString(file, Charset.availableCharsets().get("UTF-8"));
            return Lists.transform(Arrays.asList(content.split(",")), new Function<String, String>() {
                @Override
                public String apply(java.lang.String s) {
                    return s.trim();
                }
            });
        } catch (IOException e) {
            throw new InvalidMigrationFileException("Could not read migration file");
        }
    }

    @Override
    public File fileForMigration(String filePath) {
        return new File(contextFile, filePath);
    }
}
