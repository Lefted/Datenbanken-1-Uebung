package de.hska.iwii.db1.jpa.bootstrap;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class OracleConnectionWrapper {

	private static final String REMOTE_ORACLE_DB_HOST = "iwi-i-db-01";
	private static final int REMOTE_ORACLE_DB_PORT = 1521;
	private static final String SSH_TUNNEL_HOST = "login.h-ka.de";
	private static final int SSH_TUNNEL_PORT = 22;
	private static final int SSH_TUNNEL_LOCAL_PORT_FORWARD = 22222;

	private static final Logger LOGGER = LoggerFactory.getLogger(OracleConnectionWrapper.class);

	private static OracleConnectionWrapper instance;

	private final String adsName;
	private final String adsPassword;

	private Session sshTunnelSession;

	private OracleConnectionWrapper() {
		adsName = getEnvironmentVariableOrThrow("SSH_TUNNEL_ADS_NAME");
		adsPassword = getEnvironmentVariableOrThrow("SSH_TUNNEL_ADS_PASSWORD");
	}

	public void closeOracleSSHTunnel() {
		sshTunnelSession.disconnect();
	}

	private String getEnvironmentVariableOrThrow(String key) {
		String environmentVariable = System.getenv(key);
		if (environmentVariable == null) {
			throw new OracleConnectionWrapperException(
					String.format("The environment variable for key %s has not been set", key), null);
		}
		return environmentVariable;
	}

	public void establishSSHTunnel() {
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
			this.sshTunnelSession = sshSession;
			LOGGER.info("SSH tunnel is now forwarding requests to port {} to {}:{}", SSH_TUNNEL_LOCAL_PORT_FORWARD,
					REMOTE_ORACLE_DB_HOST, REMOTE_ORACLE_DB_PORT);
		} catch (JSchException e) {
			throw new OracleConnectionWrapperException("Could not establish ssh tunnel", e);
		}
	}

	private Session createSession(JSch jsch) throws JSchException {
		return jsch.getSession(adsName, SSH_TUNNEL_HOST, SSH_TUNNEL_PORT);
	}

	public static OracleConnectionWrapper getInstance() {
		return instance == null ? instance = new OracleConnectionWrapper() : instance; // NOSONAR
	}
}
