package championshang.wordpress.com.netty.client;

import java.net.InetAddress;

import javax.net.SocketFactory;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.PreSerializationTransformer;

public abstract class NetAppenderBase<E> extends UnsynchronizedAppenderBase<E> {

	/**
	 * The default port number of remote logging server (4560).
	 */
	static final int DEFAULT_PORT = 4560;

	/**
	 * The default reconnection delay (30000 milliseconds or 30 seconds).
	 */
	static final int DEFAULT_RECONNECTION_DELAY = 30000;

	/**
	 * We remember host name as String in addition to the resolved InetAddress
	 * so that it can be returned via getOption().
	 */
	protected String remoteHost;

	protected InetAddress address;
	protected int port = DEFAULT_PORT;

	protected int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;

	protected int counter = 0;

	/**
	 * Start this appender.
	 */
	public void start() {
		int errorCount = 0;
		if (port == 0) {
			errorCount++;
			addError("No port was configured for appender" + name + " For more information, please visit https://github.com/cp149/jactor-logger");
		}

		if (address == null) {
			errorCount++;
			addError("No remote address was configured for appender" + name + " For more information, please visit https://github.com/cp149/jactor-logger");
		}

		// First, close the previous connection if any.
		cleanUp();
		connect(address, port);

		if (errorCount == 0) {
			this.started = true;
		}
	}

	/**
	 * Strop this appender.
	 * 
	 * <p>
	 * This will mark the appender as closed and call then {@link #cleanUp}
	 * method.
	 */
	@Override
	public void stop() {
		if (!isStarted())
			return;

		this.started = false;
		cleanUp();
	}

	/**
	 * Drop the connection to the remote host and release the underlying
	 * connector thread if it has been created
	 */
	public abstract void cleanUp();

	public abstract void connect(InetAddress address, int port);

	/**
	 * Gets the default {@link SocketFactory} for the platform.
	 * <p>
	 * Subclasses may override to provide a custom socket factory.
	 */
	protected SocketFactory getSocketFactory() {
		return SocketFactory.getDefault();
	}

	@Override
	protected void append(E event) {

		if (event == null)
			return;

		if (address == null) {
			addError("No remote host is set for SocketAppender named \"" + this.name + "\". For more information, please visit https://github.com/cp149/jactor-logger");
			return;
		}

	}

	protected abstract PreSerializationTransformer<E> getPST();

	protected static InetAddress getAddressByName(String host) {
		try {
			return InetAddress.getByName(host);
		} catch (Exception e) {
			// addError("Could not find address of [" + host + "].", e);
			return null;
		}
	}

	/**
	 * The <b>RemoteHost</b> property takes the name of of the host where a
	 * corresponding server is running.
	 */
	public void setRemoteHost(String host) {
		address = getAddressByName(host);
		remoteHost = host;
	}

	/**
	 * Returns value of the <b>RemoteHost</b> property.
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * The <b>Port</b> property takes a positive integer representing the port
	 * where the server is waiting for connections.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns value of the <b>Port</b> property.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * The <b>reconnectionDelay</b> property takes a positive integer
	 * representing the number of milliseconds to wait between each failed
	 * connection attempt to the server. The default value of this option is
	 * 30000 which corresponds to 30 seconds.
	 * 
	 * <p>
	 * Setting this option to zero turns off reconnection capability.
	 */
	public void setReconnectionDelay(int delay) {
		this.reconnectionDelay = delay;
	}

	/**
	 * Returns value of the <b>reconnectionDelay</b> property.
	 */
	public int getReconnectionDelay() {
		return reconnectionDelay;
	}

}
