package cat.iesesteveterradas;

import cat.iesesteveterradas.utils.UtilsSQLite;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class PR210LoL {

    public static Scanner teclat = new Scanner(System.in);

    public static void main(String[] args) {
        String filePath = System.getProperty("user.dir") + File.separator + "data" + File.separator + "database.db";

        try {
            new File(filePath).getAbsoluteFile().getParentFile().mkdirs();

            if (!new File(filePath).exists()) {
                inicialitzarBD(filePath);
            }

            try (Connection conn = UtilsSQLite.connect(filePath)) {
                menu(conn);
            }

        } catch (SQLException e) {
            System.out.println("Error en la base de dades: " + e.getMessage());
        }
    }

    public static void inicialitzarBD(String filePath) throws SQLException {

        try (Connection conn = UtilsSQLite.connect(filePath)) {

            UtilsSQLite.queryUpdate(conn, "CREATE TABLE Faccio ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "nom VARCHAR(15) NOT NULL, "
                    + "resum VARCHAR(500))");

            UtilsSQLite.queryUpdate(conn, "CREATE TABLE Personatge ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "nom VARCHAR(15) NOT NULL, "
                    + "atac REAL, "
                    + "defensa REAL, "
                    + "idFaccio INTEGER, "
                    + "FOREIGN KEY (idFaccio) REFERENCES Faccio(id))");

            inserirDadesInicials(conn);
        }
    }

    public static void inserirDadesInicials(Connection conn) throws SQLException {

        String sqlFaccio = "INSERT INTO Faccio (nom, resum) VALUES (?, ?)";

        UtilsSQLite.queryUpdatePS(conn, sqlFaccio, "Demacia",
                "Regne orgullós amb una gran tradició militar, que posa la justícia, l’honor i el deure per sobre de tot. Desconfia de la màgia i la controla amb duresa dins de les seves fronteres. És el gran rival de Noxus.");

        UtilsSQLite.queryUpdatePS(conn, sqlFaccio, "Noxus",
                "Imperi poderós i expansionista que valora la força i l’ambició. Qualsevol persona hi pot arribar al poder si demostra prou talent, sigui quin sigui el seu origen. Les seves legions han conquerit molts territoris de Runeterra.");

        UtilsSQLite.queryUpdatePS(conn, sqlFaccio, "Ionia",
                "Arxipèlag on la màgia, la natura i l’espiritualitat conviuen en equilibri. La seva cultura busca l’harmonia en totes les coses, però la invasió de Noxus va obligar molts ionians a agafar les armes per defensar la seva terra.");

        String sqlPersonatge = "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES (?, ?, ?, ?)";

        UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Galio", 1, 3, 1);
        UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Lux", 2, 2, 1);
        UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Garen", 2, 3, 1);

        UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Darius", 3, 3, 2);
        UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Katarina", 2, 1, 2);
        UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Swain", 1, 2, 2);

        UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Yasuo", 3, 2, 3);
        UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Ahri", 2, 1, 3);
        UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Irelia", 3, 2, 3);
    }

    public static void menu(Connection conn) throws SQLException {
        int opcio = 0;

        while (opcio != 5) {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1. Mostrar contingut d'una taula");
            System.out.println("2. Mostrar personatges d'una faccio");
            System.out.println("3. Personatge amb mes atac d'una faccio");
            System.out.println("4. Personatge amb mes defensa d'una faccio");
            System.out.println("5. Sortir");
            System.out.print("Escull una opcio: ");

            opcio = llegirEnter();

            switch (opcio) {
                case 1:
                    mostrarTaula(conn);
                    break;
                case 2:
                    mostrarPersonatgesFaccio(conn);
                    break;
                case 3:
                    mostrarMillorPersonatge(conn, "atac");
                    break;
                case 4:
                    mostrarMillorPersonatge(conn, "defensa");
                    break;
                case 5:
                    System.out.println("Fins aviat!");
                    break;
                default:
                    System.out.println("Opcio no valida.");
            }
        }
    }

    public static int llegirEnter() {
        try {
            return Integer.parseInt(teclat.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void mostrarTaula(Connection conn) throws SQLException {
        System.out.println("\n1. Faccio");
        System.out.println("2. Personatge");
        System.out.print("Escull una taula: ");

        int opcio = llegirEnter();
        String taula;

        if (opcio == 1) {
            taula = "Faccio";
        } else if (opcio == 2) {
            taula = "Personatge";
        } else {
            System.out.println("Opcio no valida.");
            return;
        }

        String sql = "SELECT * FROM " + taula;

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnes = meta.getColumnCount();

            for (int i = 1; i <= columnes; i++) {
                System.out.print(meta.getColumnName(i) + "\t");
            }

            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= columnes; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        }
    }

    public static int seleccionarFaccio(Connection conn) throws SQLException {
        String sql = "SELECT id, nom FROM Faccio";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n===== FACCIONS =====");

            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("nom"));
            }
        }

        System.out.print("Escull una faccio: ");
        return llegirEnter();
    }

    
    public static void mostrarPersonatgesFaccio(Connection conn) throws SQLException {
        int idFaccio = seleccionarFaccio(conn);

        String sql = "SELECT Personatge.nom, Personatge.atac, Personatge.defensa, Faccio.nom AS faccio "
                + "FROM Personatge "
                + "INNER JOIN Faccio ON Personatge.idFaccio = Faccio.id "
                + "WHERE Faccio.id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFaccio);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean trobat = false;

                while (rs.next()) {
                    trobat = true;

                    System.out.println("Personatge: " + rs.getString("nom")
                            + " | Atac: " + rs.getDouble("atac")
                            + " | Defensa: " + rs.getDouble("defensa")
                            + " | Faccio: " + rs.getString("faccio"));
                }

                if (!trobat) {
                    System.out.println("No s'han trobat personatges per a aquesta faccio.");
                }
            }
        }
    }

    public static void mostrarMillorPersonatge(Connection conn, String atribut) throws SQLException {
        int idFaccio = seleccionarFaccio(conn);

        String sql = "SELECT Personatge.nom, Personatge." + atribut
                + ", Faccio.nom AS faccio "
                + "FROM Personatge "
                + "INNER JOIN Faccio ON Personatge.idFaccio = Faccio.id "
                + "WHERE Faccio.id = ? "
                + "ORDER BY Personatge." + atribut + " DESC "
                + "LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFaccio);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Personatge: " + rs.getString("nom")
                            + " | " + atribut + ": " + rs.getDouble(atribut)
                            + " | Faccio: " + rs.getString("faccio"));
                } else {
                    System.out.println("No s'han trobat personatges.");
                }
            }
        }
    }
}
