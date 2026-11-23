package cat.iesesteveterradas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cat.iesesteveterradas.utils.UtilsSQLite;

public class MainTest {

    @TempDir
    Path tempDir;

    @Test
    public void testConnect() throws SQLException {
        Path dbPath = tempDir.resolve("test.db");

        try (Connection conn = UtilsSQLite.connect(dbPath.toString());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA foreign_keys")) {

            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }
    }

    @Test
    public void testInsertarPersonatge() throws SQLException {
        Path dbPath = tempDir.resolve("test.db");

        try (Connection conn = UtilsSQLite.connect(dbPath.toString())) {

            UtilsSQLite.queryUpdate(conn,
                    "CREATE TABLE Faccio (id INTEGER PRIMARY KEY AUTOINCREMENT, nom VARCHAR(15))");

            UtilsSQLite.queryUpdate(conn,
                    "CREATE TABLE Personatge (id INTEGER PRIMARY KEY AUTOINCREMENT, nom VARCHAR(15), idFaccio INTEGER, FOREIGN KEY (idFaccio) REFERENCES Faccio(id))");

            UtilsSQLite.queryUpdatePS(conn,
                    "INSERT INTO Faccio (nom) VALUES (?)", "Demacia");

            UtilsSQLite.queryUpdatePS(conn,
                    "INSERT INTO Personatge (nom, idFaccio) VALUES (?, ?)", "Galio", 1);

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT nom FROM Personatge")) {

                assertTrue(rs.next());
                assertEquals("Galio", rs.getString("nom"));
            }

            assertThrows(SQLException.class, () ->
                    UtilsSQLite.queryUpdatePS(conn,
                            "INSERT INTO Personatge (nom, idFaccio) VALUES (?, ?)", "Lux", 99));
        }
    }
}
