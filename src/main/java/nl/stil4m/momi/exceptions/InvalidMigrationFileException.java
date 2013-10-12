package nl.stil4m.momi.exceptions;

public class InvalidMigrationFileException extends MoMiException {

    public InvalidMigrationFileException(String reason) {
        super(reason);
    }

}
