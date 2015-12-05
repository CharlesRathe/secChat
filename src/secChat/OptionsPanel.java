package secChat;

import javax.swing.*;

import java.awt.*;

public class OptionsPanel extends JPanel{

	private String[] options = {"64", "128", "256"};
	private JComboBox encryptOptions = new JComboBox(options);
	
	public final JLabel ipLabel = new JLabel("IP Address: ");
	private JTextArea ipArea = new JTextArea();
	
	private FlowLayout layout = new FlowLayout();
	
	public OptionsPanel()
	{
		this.setLayout(layout);
		this.add(ipArea);
		this.add(encryptOptions);
		ipArea.setEditable(false);
	}
	
	public void clearIP() {ipArea.setText("");}
	
	public void setIP(String s) {ipArea.setText(s);}
	
	public void muteIP() {ipArea.setEditable(false);}
	
	public void unmuteIP() {ipArea.setEditable(true);}
	
}
