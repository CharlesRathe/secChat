package secChat;

import javax.swing.*;

import java.awt.*;

public class OptionsPanel extends JPanel{

	private JTextField ipArea = new JTextField();
	private BorderLayout layout = new BorderLayout();
	
	public OptionsPanel()
	{
		this.setLayout(layout);
		this.add(ipArea);
		ipArea.setEditable(true);
		setIP("Input desired IP here");
	
	}
	
	public void clearIP() {ipArea.setText("");}
	
	public void setIP(String s) {ipArea.setText(s);}
	
	public void muteIP() {ipArea.setEditable(false);}
	
	public void unmuteIP() {ipArea.setEditable(true);}
	
}
