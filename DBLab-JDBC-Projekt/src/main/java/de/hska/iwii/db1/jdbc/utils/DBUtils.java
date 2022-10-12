package de.hska.iwii.db1.jdbc.utils;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(DBUtils.class);

	private DBUtils() {
	}

	/**
	 * Gibt einen SQL-Fehler auf der Konsole aus.
	 * 
	 * @param ex SQL-Exception.
	 */
	public static void dumpSQLException(SQLException ex) {
		LOGGER.error("SQLException: {}", ex.getLocalizedMessage());
		SQLException nextException = ex.getNextException();
		while (nextException != null) {
			LOGGER.error("SQLException: {}", nextException.getLocalizedMessage());
			nextException = nextException.getNextException();
		}
		ex.printStackTrace();
	}
}
