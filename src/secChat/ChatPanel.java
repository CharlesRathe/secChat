package secChat;

import java.awt.BorderLayout;

import javax.swing.*;

public class ChatPanel extends JPanel{

	JTextField chat;
	JTextArea display;
	BorderLayout layout = new BorderLayout();
	
	public ChatPanel()
	{
		display = new JTextArea();
		chat = new JTextField();
		
		display.setEditable(false);
		chat.setEditable(false);
		this.setLayout(layout);
		
		this.add(display, BorderLayout.NORTH);
		this.add(chat, BorderLayout.SOUTH);	
	}
	
	public void mute() {display.setEditable(false);}
	
	public void unmute() {display.setEditable(true);}
	
	public void clearDisplay() {display.setText("");}
	
	public void appendDisplay(String s) {display.append(s);}
	
	public void clearChat() {chat.setText("");}
	
}
