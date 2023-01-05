package de.hska.iwii.db1.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hska.iwii.db1.jdbc.bootstrap.OracleConnectionWrapper;
import de.hska.iwii.db1.jdbc.bootstrap.OracleConnectionWrapperException;
import de.hska.iwii.db1.jdbc.utils.DBUtils;

public class CleanupDatabase {

	private static final Logger LOGGER = LoggerFactory.getLogger(CleanupDatabase.class);

	public static void main(String[] args) {
		try (Connection con = OracleConnectionWrapper.getInstance().connect()) {
			JDBCBikeShop.getInstance().reInitializeDB(con);
		} catch (SQLException e) {
			DBUtils.dumpSQLException(e);
		} catch (OracleConnectionWrapperException e) {
			LOGGER.error("Oracle connection error: ", e);
		} finally {
			OracleConnectionWrapper.getInstance().closeOracleSSHTunnel();
		}
	}
}
