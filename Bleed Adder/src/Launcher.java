import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;



public class Launcher
{
	
	
	
	public static JFrame window;
	
	public static Container container;
	
	
	
	public static Dimension screenSize;
	
	
	
	public static JLabel loadMessage;
	
	public static JLabel logicMessage;
	
	public static JLabel saveMessage;
	
	public static JLabel saveNameMessage;
	
	
	
	public static JProgressBar progressBar;
	
	
	
	
	
	public static void main(String[] args)
	{
		
		GUI.setFont(new javax.swing.plaf.FontUIResource("Arial",java.awt.Font.BOLD,20));
		
		
		
		window = new JFrame("Bleed Adder");
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		window.setLocation(screenSize.width/10, screenSize.height/10);
		
		
		
		container = window.getContentPane();
		
		container.setLayout(new GridBagLayout());
		
		
		
		String desktopPath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator;
		
		
		
		GUI.addLabeledTextField(container, 0, 0, "Folder Path :", desktopPath, true, 0, 6);
		
		
		
		GUI.addLabeledTextField(container, 0, 1, "Bleed Horizontal Size :", "4.8", true, -250, 1).setHorizontalAlignment(SwingConstants.RIGHT);
		
		GUI.addLabel(container, GUI.createConstraints(2, 1, 0, 10, 1, 1), "%");
		
		GUI.addLabeledTextField(container, 0, 2, "Bleed Vertical Size :", "3.6", true, -250, 1).setHorizontalAlignment(SwingConstants.RIGHT);
		
		GUI.addLabel(container, GUI.createConstraints(2, 2, 0, 10, 1, 1), "%");
		
		
		
		GUI.addVoid(container, GUI.createConstraints(3, 1, 60, 10, 1, 1));
		
		GUI.addVoid(container, GUI.createConstraints(3, 2, 60, 10, 1, 1));
		
		
		
		GUI.addLabeledComboBox(container, 4, 1, "Bleed Color :", new String[] {"Auto", "Black", "White", "Grey", "Gold"}, true, 80, 2);
		
		GUI.addLabeledComboBox(container, 4, 2, "Recolor Corners :", new String[] {"Yes", "No"}, true, 80, 2);
		
		
		
		JButton button = GUI.addButton(container, GUI.createConstraints(0, 6, 0, 20, 7, 1), "Add Bleed Margins");
		
		button.addActionListener(new BleedButtonListener(button));
		
		
		
		loadMessage = GUI.addLabel(container, GUI.createConstraints(0, 7, 0, 10, 7, 1), "");
		
		logicMessage = GUI.addLabel(container, GUI.createConstraints(0, 8, 0, 10, 7, 1), "");
		
		progressBar = GUI.addProgressBar(container, GUI.createConstraints(0, 9, 0, 10, 7, 1));
		
		progressBar.setVisible(false);
		
		saveMessage = GUI.addLabel(container, GUI.createConstraints(0, 10, 0, 10, 7, 1), "");
		
		saveNameMessage = GUI.addLabel(container, GUI.createConstraints(0, 11, 0, 10, 7, 1), "");
		
		
		
		window.pack();
		
		window.setVisible(true);
		
	}
	
	
	
	
	
	static class BleedButtonListener implements ActionListener
	{
		
		JButton button;
		
		public BleedButtonListener(JButton button)
		{
			
			super();
			
			this.button = button;
			
		}

		@Override
		public void actionPerformed(ActionEvent event)
		{
			
			button.setEnabled(false);
			
			loadMessage.setText("");
			
			logicMessage.setText("");
			
			saveMessage.setText("");
			
			saveNameMessage.setText("");
			
			loadMessage.setForeground(Color.black);
			
			logicMessage.setForeground(Color.black);
			
			saveMessage.setForeground(Color.black);
			
			saveNameMessage.setForeground(Color.black);
			
			Component[] components = window.getContentPane().getComponents();
			
			
			
			String folderPath = GUI.findTextFromLabel(components, "Folder Path :");
			
			String bleedColor = GUI.findComboBoxFromLabel(components, "Bleed Color :");
			
			String fillCorners = GUI.findComboBoxFromLabel(components, "Recolor Corners :");
			
			String bleedWidthPercent = GUI.findTextFromLabel(components, "Bleed Vertical Size :");
			
			String bleedHeightPercent = GUI.findTextFromLabel(components, "Bleed Horizontal Size :");
			
			
			
			new BleederWorker(folderPath, bleedWidthPercent, bleedHeightPercent, bleedColor, fillCorners, "Yes").execute();
			
			
			
			new ButtonEnablerLaterWorker(button, 1500l).execute();
			
		}
	}
}
