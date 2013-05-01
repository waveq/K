import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

public class KFrame extends JFrame implements ActionListener, MessageReceiver, DrawPointReceiver {
	
	public static final int DEF_WIDTH = 800;
	
	public static final int DEF_HEIGHT = 600;
	
	private final JButton hostButton;
	
	private final JButton joinButton;
	
	private final JButton exitButton;
	
	private final JButton beDrawerButton;
	
	private final JButton wipeButton;
	
	private final JTextField textField;
	
	private final JTextArea textArea;
	
	private final JPanel paintButtons = new JPanel();
	
	private Connection connection;
	
	private final DrawArea drawArea = new DrawArea(this);
	
	//private String[] myWords = {"rapper", "circus", "apple tree", "notebook", "wallet",
	//		"alien", "terrorist", "chicken", "sheep"};
	
	private String[] myWords = {"owca", "terrorysta", "portfel", "laptop", "ufo", "dzik"};
	
	private Random randomGenerator = new Random();
	
	private String chosenWord = myWords[randomGenerator.nextInt(myWords.length)];
	
	private JLabel wordLabel;
	
	private double points = 0;
	
	private JLabel pointsLabel;
	
	private final StringBuilder builder = new StringBuilder();
	
	public KFrame() {
		super("K");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(DEF_WIDTH, DEF_HEIGHT));
		setMinimumSize(new Dimension(800, 600));
		//setResizable(false);
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints parameters = new GridBagConstraints();
		
		Container mainContainer = getContentPane();	
		mainContainer.setLayout(layout);
		
		textField = new JTextField();
		textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));
		textField.addActionListener(this);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		
		
		hostButton = new JButton("Host");
		hostButton.addActionListener(this);
		
		joinButton = new JButton("Join");
		joinButton.addActionListener(this);
		
		exitButton = new JButton("Exit");
		exitButton.addActionListener(this);
		
		beDrawerButton = new JButton("Be drawer");
		beDrawerButton.addActionListener(this);
		beDrawerButton.setVisible(false);
		
		
		wordLabel = new JLabel("Your word: "+chosenWord);
		wordLabel.setBorder(BorderFactory.createEtchedBorder());
		wordLabel.setVisible(false);
		
		pointsLabel= new JLabel("Points: "+points);
		pointsLabel.setBorder(BorderFactory.createEtchedBorder());
		
		
		
		paintButtons.setLayout(new GridLayout(6,2));
		addDotShapeButton("Black", Color.black);
		addDotShapeButton("White", Color.white);
		addDotShapeButton("Blue", Color.blue);
		addDotShapeButton("Red", Color.red);
		addDotShapeButton("smallCircle", null);
		addDotShapeButton("smallSquare", null);
		addDotShapeButton("bigCircle", null);
		addDotShapeButton("bigSquare", null);
		addDotShapeButton("dot", null);
		
		wipeButton = new JButton("Clear sheet");
		wipeButton.addActionListener(this);
		wipeButton.setBackground(Color.white);
		paintButtons.add(wipeButton);
		paintButtons.add(beDrawerButton);
		paintButtons.add(wordLabel);
		
		parameters.weightx = 0;
		parameters.weighty = 0;
		parameters.fill = GridBagConstraints.BOTH;
		add(drawArea, parameters, 0, 0, 1, 5);
		
		parameters.fill = GridBagConstraints.HORIZONTAL;
		add(textField, parameters, 0, 7, 2, 1);
		
		parameters.fill = GridBagConstraints.HORIZONTAL;
		parameters.weightx = 100;
		parameters.weighty = 0;
		add(hostButton, parameters, 1, 0, 1, 1);
		add(joinButton, parameters, 1, 1, 1, 1);
		add(exitButton, parameters, 1, 2, 1, 1);
		add(paintButtons, parameters, 1, 3, 1, 1);
		add(pointsLabel, parameters, 1, 4, 1, 1);
		
		parameters.anchor = GridBagConstraints.CENTER;
		parameters.fill = GridBagConstraints.BOTH;
		parameters.weighty = 1;
		parameters.weightx = 0;
		add(scrollPane, parameters, 0, 6, 2, 1);
		
		setVisible(true);
		pack();
	}
	
	/**
	 * Method that adds buttons that specifies color and shape of dot.
	 * @param name Name of button.
	 * @param color Color to change to.
	 */
	
	private void addDotShapeButton(final String name, Color color) {
		if(color != null) {
			final JButton colorButton = new JButton();
			colorButton.setBackground(color);
			paintButtons.add(colorButton);
			colorButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					drawArea.setColor(colorButton.getBackground());
				}
			});
		
		}
		else {
			final JButton shapeButton = new JButton();
			
			ImageIcon icon = new ImageIcon(KFrame.class.getResource(name+".png"));
			shapeButton.setIcon(icon);
			shapeButton.setBackground(Color.white);
			paintButtons.add(shapeButton);
			shapeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					drawArea.setShape(name);
				}
			});
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		try{
			
		
			if (event.getSource() == hostButton) {
				disconnect();
				waitForConnection();
			}
			else if (event.getSource() == joinButton) {
				disconnect();
				connectToServer();
			}
			else if(event.getSource () == textField) {
				consoleCommands(textField.getText());
				String msg = textField.getText();
				writeLine("Me: " +msg);
				sendMessage(msg);
				textField.setText("");
			}
			else if(event.getSource() == exitButton) {
				dispose();
			}
			else if(event.getSource() == beDrawerButton) {
				drawArea.imDrawer();
				beDrawerButton.setVisible(false);
				wordLabel.setVisible(true);
				connection.sendCoordinates("hideDrawButton");
				drawArea.wipeArea();
			}
			else if(event.getSource() == wipeButton) {
				if(drawArea.imIDrawer()) {
					drawArea.wipeArea();
					connection.sendCoordinates("wipeArea");
				}
			}
		}catch (IOException e) {
			e.printStackTrace();	
			writeLine("Can't create connection: " + e.toString());
		}
	}
	
	/**
	 * Commands that can be written into textField,
	 * all of them got prefix "/k.".
	 * @param command String with command.
	 * @throws IOException
	 */
	
	private void consoleCommands (String command) throws IOException {
		if(command.equals("/k.disconnect"))
			disconnect();
		else if(command.equals("/k.exit"))
			dispose();
	}
	
	private void disconnect() throws IOException {
		if (connection != null) {
			connection.disconnect();
		}
	}
	
	private void waitForConnection() throws IOException {
		connection = new TCPServer(this);
		}
	
	private void connectToServer() throws IOException {
		String server = (String) JOptionPane.showInputDialog(this, "Enter server address:",
				"Connect to server", JOptionPane.PLAIN_MESSAGE, null, null, "127.0.0.1");
		connection = new TCPClient(server, this);
	}
	
	private void sendMessage(String msg) {
		if (connection != null && connection.isConnected()) {
			try {
				connection.sendMessage(msg);
			} catch (IOException e) {
				e.printStackTrace();
				writeLine("Can't send message: " + e.toString());
			}
		} else {
			writeLine("You are not connected!");
		}
	}
	
	private void writeLine(String line) {
		builder.append(line).append("\n");
		textArea.setText(builder.toString());
	}
	
	public void gotMessage(final String line) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
					writeLine(line);
					if(line.equals("They: "+chosenWord)) {
						writeLine("Word guessed.");
						writeLine("You earned 0.5pkt.");
						points+=0.5;
						pointsLabel.setText("Points: "+points);
						drawArea.imNotDrawer();
						try {
							connection.sendCoordinates("pwGuessed");
						} catch (IOException e) {
							writeLine("Cant send message "+e.toString());
						}
						setDefaults();
					}	
			}
		});
	}	
	
	public void setDrawButtonVisible() {
		beDrawerButton.setVisible(true);
	}
	
	/**
	 * sets default options after guessed password
	 */
	private void setDefaults() {
		setDrawButtonVisible();
		chosenWord = myWords[randomGenerator.nextInt(myWords.length)];
		wordLabel.setVisible(false);
		// validating
		wordLabel.setText("Your word: "+chosenWord);
		
		
	}
	
	/**
	 * sends coordinates of dot taken out of drawArea
	 */
	@Override
	public void dotToSend(String coords) {
		if (connection != null && connection.isConnected()) {
			try {
				connection.sendCoordinates(coords);
			} catch (IOException e) {
				e.printStackTrace();
				writeLine("Can't send drawing coordinates: " + e.toString());
			}
		}
		
	}

	/**
	 * Parses string with coordinates to integer and 
	 * sends them to with shape and color to drawArea
	 * 
	 * Its also used to get information from currently drawing guy that 
	 * password we sent was correct and to clear drawArea.
	 * 
	 * @param coord contains information about coordinates, shape and color of dot
	 * OR string that contains keywords to wipe drawArea, hide beDrawerButton and 
	 * notice that password was correct.
	 */

	@Override
	public void gotCoordinates(String coord) {
		if(coord.equals("pwGuessed")) {
			writeLine("You have guessed the word.");
			writeLine("You earned 1pkt.");
			points += 1;
			pointsLabel.setText("Points: "+points);
			setDefaults();
		}
		else if(coord.equals("hideDrawButton")) {
			beDrawerButton.setVisible(false);
			drawArea.wipeArea();
		}
		else if(coord.equals("wipeArea")) {
			drawArea.wipeArea();
		}
				
		else {		
		String delims = "[|]";
		String[] tokens = coord.split(delims);
		int x = Integer.parseInt(tokens[0]);
		int y = Integer.parseInt(tokens[1]);
		String color = tokens[2];
		String shape = tokens[3];
		drawArea.addCoordinates(x, y, color, shape);
		}
	}

	/**
	 * Add component with given parameters
	 * @param c component to add
	 * @param param GridBagConstraints
	 * @param x position x of area
	 * @param y position y of area
	 * @param w width of area
	 * @param h height of area
	 */
	public void add(Component c, GridBagConstraints param, int x, int y, int w, int h) {
		param.gridx = x;
		param.gridy = y;
		param.gridwidth = w;
		param.gridheight = h;
		getContentPane().add(c, param);
	}
}
