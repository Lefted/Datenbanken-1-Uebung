package de.hska.iwii.db1.jdbc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diese Klasse ist die Basis für Ihre Lösung. Mit Hilfe der Methode
 * reInitializeDB können Sie die beim Testen veränderte Datenbank
 * wiederherstellen.
 */
public class JDBCBikeShop {

	private static final Logger LOGGER = LoggerFactory.getLogger(JDBCBikeShop.class);

	/** @formatter:off
     * Stellt die Datenbank aus der SQL-Datei wieder her.
     * - Alle Tabllen mit Inhalt ohne Nachfrage löschen.
     * - Alle Tabellen wiederherstellen.
     * - Tabellen mit Daten füllen.
     * <p>
     * Getestet mit MsSQL 12, MySql 8.0.8, Oracle 11g, Oracle 18 XE, PostgreSQL 14.
     * <p>
     * Das entsprechende Sql-Skript befindet sich im Ordner ./sql im Projekt.
     * @param connection Geöffnete Verbindung zu dem DBMS, auf dem die
     * 					Bike-Datenbank wiederhergestellt werden soll. 
     * @formatter:on
     */
	public void reInitializeDB(Connection connection) {
		try (Statement statement = connection.createStatement()) {
			LOGGER.info("Initializing DB");
			connection.setAutoCommit(true);
			String productName = connection.getMetaData().getDatabaseProductName();
			boolean isMsSql = productName.equals("Microsoft SQL Server");
			int numStmts = 0;

			// Liest den Inhalt der Datei ein.
			String[] fileContents = new String(Files.readAllBytes(Paths.get("sql/Bike.sql")), StandardCharsets.UTF_8)
					.split(";");

			for (String sqlString : fileContents) {
				// Microsoft kennt den DATE-Operator nicht.
				if (isMsSql) {
					sqlString = sqlString.replace(", DATE '", ", '");
				}

				try { // NOSONAR
					statement.execute(sqlString);
					LOGGER.info(++numStmts % 80 == 0 ? "/\n" : "."); // NOSONAR
				} catch (SQLException e) {
					LOGGER.error("\n" + sqlString.replace('\n', ' ').trim() + ": ", e);
				}
			}
			LOGGER.info("Bike database is reinitialized on {}%nat URL {}", productName,
					connection.getMetaData().getURL());
		} catch (Exception e) {
			LOGGER.error("Failed to reInitialize database", e);
		}
	}
}
