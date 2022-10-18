package de.hska.iwii.db1.jdbc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hska.iwii.db1.jdbc.bootstrap.OracleConnectionWrapper;
import de.hska.iwii.db1.jdbc.bootstrap.OracleConnectionWrapperException;
import de.hska.iwii.db1.jdbc.utils.DBUtils;

/**
 * Diese Klasse ist die Basis für Ihre Lösung. Mit Hilfe der Methode
 * reInitializeDB können Sie die beim Testen veränderte Datenbank
 * wiederherstellen.
 */
public class JDBCBikeShop {

	private static final Logger LOGGER = LoggerFactory.getLogger(JDBCBikeShop.class);

	private static final JDBCBikeShop INSTANCE = new JDBCBikeShop();

	public static void main(String[] args) {
		try (Connection con = OracleConnectionWrapper.getInstance().connect()) {
			INSTANCE.logJDBCDatabaseInfo(con);
			INSTANCE.logJDBCDriverInfo(con);
			INSTANCE.selectPersonal(con);
			INSTANCE.selectKunden(con);
		} catch (SQLException e) {
			DBUtils.dumpSQLException(e);
		} catch (OracleConnectionWrapperException e) {
			LOGGER.error("Oracle connection error: ", e);
		} finally {
			OracleConnectionWrapper.getInstance().closeOracleSSHTunnel();
		}
	}

	private void logJDBCDatabaseInfo(Connection con) throws SQLException {
		DatabaseMetaData metaData = con.getMetaData();
		LOGGER.info("Database: {} {}", metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
	}

	private void logJDBCDriverInfo(Connection con) throws SQLException {
		DatabaseMetaData metaData = con.getMetaData();
		LOGGER.info("Database driver: {} {}", metaData.getDriverName(), metaData.getDriverVersion());
	}

	private void selectPersonal(Connection con) {
		LOGGER.info("Selecting personal:");
		try (Statement statement = con.createStatement()) {
			ResultSet resultSet = statement.executeQuery("SELECT persnr, name, ort, aufgabe FROM personal");
			logResultSet(resultSet);
		} catch (SQLException e) {
			LOGGER.error("Failed to select personal", e);
		}
	}

	private void selectKunden(Connection con) {
		LOGGER.info("Selecting kunden:");
		try (Statement statement = con.createStatement()) {
			ResultSet resultSet = statement.executeQuery("SELECT * FROM kunde");
			logResultSet(resultSet);
		} catch (SQLException e) {
			LOGGER.error("Failed to select knuden", e);
		}
	}

	private void logResultSet(ResultSet resultSet) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();

		// head labels
		String pattern = builderFormattingPattern(metaData, columnCount);
		String formattedLabels = String.format(pattern, (Object[]) getColumnLabels(metaData));
		String formattedTypes = String.format(pattern, (Object[]) getColumnTypeNames(metaData));
		LOGGER.info(formattedLabels);
		LOGGER.info(formattedTypes);

		// separator
		String separator = buildSeparator(metaData, columnCount);
		LOGGER.info(separator);

		// body
		while (resultSet.next()) {
			String formattedRow = buildFormatatedRow(resultSet, metaData, columnCount);
			LOGGER.info(formattedRow);
		}
	}

	private String builderFormattingPattern(ResultSetMetaData metaData, int columnCount) throws SQLException {
		StringBuilder patternBuilder = new StringBuilder();
		for (int currentColumn = 1; currentColumn <= columnCount; currentColumn++) {
			int colWidth = metaData.getColumnDisplaySize(currentColumn);
			patternBuilder.append("%-");
			patternBuilder.append(colWidth);
			patternBuilder.append("s");
			if (currentColumn != columnCount) {
				patternBuilder.append(" | ");
			}
		}
		return patternBuilder.toString();
	}

	private String[] getColumnLabels(ResultSetMetaData metaData) throws SQLException {
		String[] result = new String[metaData.getColumnCount()];
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			result[i - 1] = metaData.getColumnLabel(i);
		}
		return result;
	}

	private String[] getColumnTypeNames(ResultSetMetaData metaData) throws SQLException {
		String[] result = new String[metaData.getColumnCount()];
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			result[i - 1] = metaData.getColumnTypeName(i);
		}
		return result;
	}

	private String buildSeparator(ResultSetMetaData metaData, int columnCount) throws SQLException {
		StringBuilder builder = new StringBuilder();
		for (int currentColumn = 1; currentColumn <= columnCount; currentColumn++) {
			int colWidth = metaData.getColumnDisplaySize(currentColumn);
			int gap = currentColumn != columnCount ? 3 : 0;
			String formattingPattern = String.format("%%-%ds", (colWidth + gap));
			String cellText = String.format(formattingPattern, "-").replace(' ', '-');
			builder.append(cellText);
		}
		return builder.toString();
	}

	private String buildFormatatedRow(ResultSet resultSet, ResultSetMetaData metaData, int columnCount)
			throws SQLException {
		StringBuilder builder = new StringBuilder();
		for (int currentColumn = 1; currentColumn <= columnCount; currentColumn++) {
			String formattingPattern;
			formattingPattern = buildFormattingPatternForCell(metaData, currentColumn);
			Object cellValue = resultSet.getObject(currentColumn);
			builder.append(String.format(formattingPattern, cellValue));
			if (currentColumn != columnCount) {
				builder.append(" | ");
			}
		}
		return builder.toString();
	}

	private String buildFormattingPatternForCell(ResultSetMetaData metaData, int currentColumn) throws SQLException {
		int colWidth = metaData.getColumnDisplaySize(currentColumn);
		String patternForPattern = isRightBound(currentColumn) ? "%%%d%c" : "%%-%d%c";
		return String.format(patternForPattern, colWidth, getTypeChar(metaData, currentColumn));
	}

	private boolean isRightBound(int columnIndex) {
		return columnIndex == 1;
	}

	private char getTypeChar(ResultSetMetaData metaData, int currentColumn) throws SQLException {
		return switch (metaData.getColumnType(currentColumn)) {
		case Types.DOUBLE, Types.FLOAT, Types.BIGINT -> 'd';
		default -> 's';
		};
	}

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
	@SuppressWarnings("unused")
	private void reInitializeDB(Connection connection) { // SONAR
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
