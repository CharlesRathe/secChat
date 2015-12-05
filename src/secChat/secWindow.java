package secChat;

import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

public class secWindow extends JFrame implements ActionListener{
	
	public JLayeredPane panelManager = new JLayeredPane();
	public OptionsPanel optionsPanel = new OptionsPanel();
	public ChatPanel chatPanel = new ChatPanel();
	// Initialize layout here
	public final JLabel ipLabel = new JLabel("IP Address: ");
	
	public secWindow()
	{	
		this.setSize(400, 400);
		this.setLayout(new GridLayout(1,1));
		this.add(panelManager);
		// panelManager.setLayout(optionsLayout);
		panelManager.add(optionsPanel, 2);
		panelManager.add(chatPanel, 1);
		
	}
	
	public void switchPanels()
	{
		int temp = JLayeredPane.getLayer(optionsPanel);
		panelManager.setLayer(optionsPanel, JLayeredPane.getLayer(chatPanel));
		panelManager.setLayer(chatPanel, temp);
		
		if(temp == 1)
		{
			chatPanel.mute();
			
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
