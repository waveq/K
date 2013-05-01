import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;

public class TCPClient implements Connection, Runnable {

	private final MessageReceiver msgReceiver;

	private final String serverHost;

	private boolean connected;

	private Socket socket;
	

	public TCPClient(String serverHost, MessageReceiver msgReceiver) throws IOException {
		this.msgReceiver = msgReceiver;
		this.serverHost = serverHost;
		// create new thread
		Executors.newSingleThreadExecutor().submit(this);
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void sendMessage(String msg) throws IOException {
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		writer.println("MSG");
		writer.println(msg);
		writer.flush();
	}
	
	
	@Override
	public void sendCoordinates(String coord) throws IOException {
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		writer.println("COORD");
		writer.println(coord);
		writer.flush();
	}

	@Override
	public void disconnect() throws IOException {
		if (socket != null) {
			socket.close();
		}
	}

	@Override
	public void run() {
		try {
			msgReceiver.gotMessage("Connecting to " + serverHost + ":" + TCPServer.PORT);
			socket = new Socket(serverHost, TCPServer.PORT);
			msgReceiver.gotMessage("Connected!");
			msgReceiver.setDrawButtonVisible();
			connected = true;
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String type;
			while ((type = reader.readLine()) != null) {
				if(type.equals("MSG"))
					msgReceiver.gotMessage("They: " + reader.readLine());
				else if(type.equals("COORD"))
					msgReceiver.gotCoordinates(reader.readLine());
			}
			msgReceiver.gotMessage("Disconnected");
		} catch (IOException e) {
			e.printStackTrace();
			msgReceiver.gotMessage("Error: " + e.toString());
		}
		connected = false;
	}
}