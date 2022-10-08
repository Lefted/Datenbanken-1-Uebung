package de.hska.iwii.db1.jdbc.bootstrap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class OracleBootstrapper {

	private static final String REMOTE_ORACLE_DB_HOST = "iwi-i-db-01";
	private static final int REMOTE_ORACLE_DB_PORT = 1521;
	private static final String SSH_TUNNEL_HOST = "login.h-ka.de";
	private static final int SSH_TUNNEL_PORT = 22;
	private static final int SSH_TUNNEL_LOCAL_PORT_FORWARD = 22222;

	private static final Logger LOGGER = LoggerFactory.getLogger(OracleBootstrapper.class);

	private static OracleBootstrapper instance;

	private final String adsName;
	private final String adsPassword;
	private final String databaseUser;
	private final String databasePassword;
	private final String databaseName;

	private Session sshTunnelSession;
	private Connection oracleDatabaseConnection;

	private OracleBootstrapper() {
		adsName = System.getenv("SSH_TUNNEL_ADS_NAME");
		adsPassword = System.getenv("SSH_TUNNEL_ADS_PASSWORD");
		databaseUser = System.getenv("ORACLE_DATABASE_USER");
		databasePassword = System.getenv("ORACLE_DATABASE_PASSWORD");
		databaseName = System.getenv("ORACLE_DATABASE_NAME");
	}

	public Connection connect() {
		establishSSHTunnel();
		connectToDatabase();
		return oracleDatabaseConnection;
	}
	
	public void close() {
		try {
			oracleDatabaseConnection.close();
		} catch (SQLException e) {
			LOGGER.error("Could not close oracle database connection", e);
			e.printStackTrace();
		}
		sshTunnelSession.disconnect();
	}

	private void establishSSHTunnel() {
		try {
			LOGGER.info("Creating ssh tunnel");
			JSch jsch = new JSch();
			Session sshSession = createSession(jsch);
			sshSession.setPassword(adsPassword);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(config);
			sshSession.connect();
			sshSession.setPortForwardingL(SSH_TUNNEL_LOCAL_PORT_FORWARD, REMOTE_ORACLE_DB_HOST, REMOTE_ORACLE_DB_PORT);
		} catch (JSchException e) {
			throw new OracleBootstrapperException("Could not establish ssh tunnel", e);
		}
	}

	private void connectToDatabase() {
		assertOracleDriverExists();
		oracleDatabaseConnection = createOracleDatabaseConnection();
	}

	private void assertOracleDriverExists() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			throw new OracleBootstrapperException("Could not find class for Oracle Driver", e);
		}
	}

	private Connection createOracleDatabaseConnection() {
		Properties config = new Properties();
		config.put("user", databaseUser);
		config.put("password", databasePassword);
		try {
			return DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:" + SSH_TUNNEL_LOCAL_PORT_FORWARD + ":" + databaseName, config);
		} catch (SQLException e) {
			throw new OracleBootstrapperException("Could not connect to oracle database", e);
		}
	}

	private Session createSession(JSch jsch) throws JSchException {
		return jsch.getSession(adsName, SSH_TUNNEL_HOST, SSH_TUNNEL_PORT);
	}

	public static OracleBootstrapper getInstance() {
		return instance == null ? instance = new OracleBootstrapper() : instance; // NOSONAR
	}
}
