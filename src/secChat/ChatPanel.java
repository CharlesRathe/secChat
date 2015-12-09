package secChat;

import java.awt.BorderLayout;

import javax.swing.*;

public class ChatPanel extends JPanel{

	// GUI Objects
	private JTextField chat;
	private JTextArea display;
	private JScrollPane scroll;
	
	// Layout
	BorderLayout layout = new BorderLayout();
	
	// Initialize ChatPanel object
	public ChatPanel()
	{
		// Initialize Objects
		display = new JTextArea();
		chat = new JTextField();
		scroll = new JScrollPane(display);
		
		// Set Layout
		this.setLayout(layout);
		this.add(display, BorderLayout.CENTER);
		this.add(chat, BorderLayout.SOUTH);
		
		// Set displays to be initially un-editable
		display.setEditable(false);
		chat.setEditable(false);
	}
	
	// Sets display un-editable
	public void mute() {display.setEditable(false);}
	
	// Sets display to editable
	public void unmute() {display.setEditable(true);}
	
	// Mute chat area
	public void muteChat() {chat.setEditable(false);}
	
	// Sets chat area to editable
	public void unmuteChat() {chat.setEditable(true);}
	
	// Clears the display
	public void clearDisplay() {display.setText("");}
	
	// Adds message to display
	public void appendDisplay(String s) {display.append("\n" + s);}
	
	// Clears chat text field
	public void clearChat() {chat.setText("");}
}
