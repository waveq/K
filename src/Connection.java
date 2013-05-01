import java.io.IOException;

public interface Connection {
	boolean isConnected();

	void sendMessage(String msg) throws IOException;

	void disconnect() throws IOException;
	
	void sendCoordinates(String coord) throws IOException;
}