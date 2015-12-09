package secChat;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class SecWindow extends JFrame implements ActionListener{

	// GUI - Panels to add
	private ChatPanel chatPanel = new ChatPanel();
	private OptionsPanel optionsPanel = new OptionsPanel();
	
	
	public SecWindow(String s)
	{	
		super(s);
		// Set up frame
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		this.setSize(400, 400);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Add panels
		contentPane.add(optionsPanel, BorderLayout.NORTH);
		contentPane.add(chatPanel, BorderLayout.CENTER);
		
		// Pack
		pack();
		
	}
	
	// Sets display un-editable
	public void mute() {chatPanel.mute();}
	
	// Sets IP field uneditable
	public void muteIP() {optionsPanel.muteIP();}
	
	// Sets chat field to uneditable
	public void muteChat() {chatPanel.muteChat();}
	
	// Sets chat field to editable
	public void unmuteChat() {chatPanel.unmuteChat();}
	
	// Sets the text in chat field
	public void sestChat(String m) {chatPanel.setChat(m);}
	
	// Clears IP field
	public void clearIP() {optionsPanel.clearIP();}
	
	// Sets IP field to editable
	public void unmuteIP() {optionsPanel.unmuteIP();}
	
	// Sets IP field text
	public void setIP(String s) {optionsPanel.setIP(s);}
	
	// Sets display to editable
	public void unmute() {chatPanel.unmute();}
	
	// Clears the display
	public void clearDisplay() {chatPanel.clearDisplay();}
	
	// Adds message to display
	public void appendDisplay(String s) {chatPanel.appendDisplay(s);}
	
	// Clears chat text field
	public void clearChat() {chatPanel.clearChat();}
	
	
	public void actionPerformed(ActionEvent e) {
		
		
	}
}
