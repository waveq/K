import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;



public class TCPServer implements Connection, Runnable {
	
	static final int PORT = 1234;
	
	private final MessageReceiver msgReceiver;
	
	private final ServerSocket serverSocket;

	private boolean connected;

	private Socket clientSocket;
	
	public TCPServer(MessageReceiver msgReceiver) throws IOException {
		this.msgReceiver = msgReceiver; 
		this.serverSocket = new ServerSocket(PORT);
		// create new Thread
		Executors.newSingleThreadExecutor().submit(this);
	}
	
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void sendMessage(String msg) throws IOException {
		PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
		writer.println("MSG");
		writer.println(msg);
		writer.flush();
	}
	

	@Override
	public void sendCoordinates(String coord) throws IOException {
		PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
		writer.println("COORD");
		writer.println(coord);	
		writer.flush();
	}
	
	
	@Override
	public void disconnect() throws IOException {
		serverSocket.close();
		if (clientSocket != null) {
			clientSocket.close();
		}
	}
	
	
	
	@Override
	public void run() {
		try {
			msgReceiver.gotMessage("Listening on port " + TCPServer.PORT);
			while (!serverSocket.isClosed()) {
				clientSocket = serverSocket.accept();
				connected = true;
				acceptNewClientConnection();
				connected = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			msgReceiver.gotMessage("Error: " + e.toString());
		}
		connected = false;
	}
	
	private void acceptNewClientConnection() throws IOException {
		msgReceiver.gotMessage("Got new connection from " + clientSocket.getInetAddress().getHostAddress());
		msgReceiver.setDrawButtonVisible();

		BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String type;
		while ((type = reader.readLine()) != null) {
			if(type.equals("MSG"))
				msgReceiver.gotMessage("They: " + reader.readLine());
			else if(type.equals("COORD"))
				msgReceiver.gotCoordinates(reader.readLine());
		}
		
		msgReceiver.gotMessage("Client disconnected");
	}
}
