import java.io.*;
import java.net.Socket;

/**
 * A ServerConnection handles the low level connection to a server and provides safe read and write functions.
 *
 * @author Henrik Nilsson
 */
public class ServerConnection {
    private String ip;
    private int port;

    private Socket connection;
    private BufferedReader in;
    private DataOutputStream out;

    private boolean alive = false;

    /**
     * ServerConnection constructor
     * @param ip IP address to connect to
     * @param port Port number to connect to
     * @throws IOException If the server connection could not be made
     * @throws java.net.UnknownHostException If the given host could not be found
     */
    public ServerConnection(String ip, int port) throws IOException, java.net.UnknownHostException {
        this.ip = ip;
        this.port = port;

        this.connection = new Socket(ip, port);
        this.in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        this.out = new DataOutputStream(connection.getOutputStream());

        this.alive = true;
    }

    /**
     * Write to the connected server.
     * @param str String to write
     * @throws IOException If the write could not be made
     * @throws ConnectionClosedException If the current connection is closed
     */
    public void write(String str) throws IOException, ConnectionClosedException {
        write(str.getBytes());
    }


    /**
     * Write to the connected server.
     * @param buf Char[] to write
     * @throws IOException If the write could not be made
     * @throws ConnectionClosedException If the current connection is closed
     */
    public void write(char[] buf) throws IOException, ConnectionClosedException {
        write(new String(buf));
    }

    /**
     * Write to the connected server.
     * @param bytes Byte[] to write
     * @throws IOException If the write could not be made
     * @throws ConnectionClosedException If the current connection is closed
     */
    public void write(byte[] bytes) throws IOException, ConnectionClosedException {
        if (!alive) throw new ConnectionClosedException("Connection not alive.");

        try {
            out.write(bytes);
        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    /**
     * Read from the current connection.
     * @return Read the next null-terminated string
     * @throws IOException If the read could not be made
     * @throws ConnectionClosedException It the current connection is closed.
     */
    public String read() throws IOException, ConnectionClosedException {
        if (!alive) throw new ConnectionClosedException("Connection not alive.");

        StringBuilder strBuild = new StringBuilder();

        try {
            for (char c = (char) in.read(); c != '\0'; c = (char) in.read()) {
                if (c == Character.MAX_VALUE) throw new IOException("Connection died.");

                strBuild.append(c);
            }
        } catch (IOException e) {
            disconnect();
            throw e;
        }
        return strBuild.toString();
    }

    /**
     * Connect to the current IP and Port.
     * @throws IOException If the connection could not be made
     * @throws java.net.UnknownHostException If the current IP and Port is an unknown host
     */
    public void connect() throws IOException, java.net.UnknownHostException {
        if (alive) return;

        connection = new Socket(ip, port);
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        out = new DataOutputStream(connection.getOutputStream());


        alive = true;
    }

    /**
     * Close all streams and disconnect from the server.
     * @throws IOException If the server could not be disconnected
     */
    public void disconnect() throws IOException {
        if (!alive) return;

        in.close();
        out.close();
        connection.close();

        alive = false;
    }

    /**
     * Get current IP
     * @return IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * Set a new IP. This disconnects the current server connection.
     * @param ip New IP
     * @throws IOException If the current connection could not be disconnected
     */
    public void setIp(String ip) throws IOException {
        disconnect();
        this.ip = ip;
    }

    /**
     * Get Port number
     * @return Current Port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Set a new Port. This disconnects the current server connection.
     * @param port New Port number
     * @throws IOException If the current connection could not be disconnected
     */
    public void setPort(int port) throws IOException {
        disconnect();
        this.port = port;
    }

    /**
     * Is the connection alive.
     * @return Alive status
     */
    public boolean isAlive() {
        return alive;
    }
}
