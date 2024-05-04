import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;



public class GUI
{
	
	
	
	public static GridBagConstraints createConstraints(	int x, int y, int xPixel, int yPixel, double xWeight, double yWeight, int xSize, int ySize,
														int fill, int anchor,
														int padTop, int padLeft, int padBottom, int padRight )
	{
		
		//Create a set of constraints that will determine an object's position, size and behavior within the window
		GridBagConstraints constraints = new GridBagConstraints();
		
		
		//Which column (x), and which row (y) should it start on ?
		constraints.gridx = x;
		
		constraints.gridy = y;
		
		
		
		//How much larger (pixels*2) should it be compared to other thing on the same row and same column ?
		constraints.ipadx = xPixel;
		
		constraints.ipady = yPixel;
		
		
		
		//How fast will it resize compared to other thing on the same row, and same column ?
		constraints.weightx = xWeight;
		
		constraints.weighty = yWeight;
		
		
		
		//How many columns and rows should it span ?
		constraints.gridwidth = xSize;
		
		constraints.gridheight = ySize;
		
		
		
		//If it's smaller than it's spot on the grid, how should it fill it ?
		constraints.fill = fill;
		
		
		
		//If it must not fill it's spot on the grid, where should it be anchored ?
		constraints.anchor = anchor;
		
		
		
		//How much pixels should it leave between itself and the edges of it's spot on the grid ?
		constraints.insets = new Insets(padTop, padLeft, padBottom, padRight);
		
		
		
		return constraints;
		
	}
	
	public static GridBagConstraints createConstraints(int x, int y, int xPixel, int yPixel, int xSize, int ySize)
	{
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		
		
		constraints.gridx = x;
		
		constraints.gridy = y;
		
		
		
		constraints.ipadx = xPixel;
		
		constraints.ipady = yPixel;
		
		
		
		constraints.weightx = 1d;
		
		constraints.weighty = 1d;
		
		
		
		constraints.gridwidth = xSize;
		
		constraints.gridheight = ySize;
		
		
		
		constraints.fill = GridBagConstraints.BOTH;				//Fill horizontaly and verticaly
		
		
		
		constraints.anchor = GridBagConstraints.PAGE_END;		//Anchor at bottom middle
		
		
		
		constraints.insets = new Insets(5, 5, 5, 5);			//Leave no space
		
		
		
		return constraints;
		
	}
	
	public static GridBagConstraints createConstraints(int x, int y, int xSize, int ySize)
	{
		
		return createConstraints(x, y, 0, 0, xSize, ySize);
		
	}
	
	
	
	public static JPanel addVoid(Container container, GridBagConstraints constraints)
	{
		
		JPanel label = new JPanel();
		
		container.add(label, constraints);
		
		
		
		return label;
		
	}
	
	
	
	public static Component addZeroHeightVoid(Container container, GridBagConstraints constraints)
	{
		
		constraints.weightx = 0.5;
		
		constraints.weighty = 0; 
		
		constraints.fill = GridBagConstraints.NONE;
		
		Component glue = Box.createHorizontalGlue();
		
		container.add(glue, constraints);
		
		return glue;
		
	}
	
	
	
	public static JSeparator addSeparator(Container container, GridBagConstraints constraints)
	{
		
		JSeparator separator = new JSeparator();
		
		container.add(separator, constraints);
		
		
		
		return separator;
		
	}
	
	
	
	public static JComboBox<String> addComboBox(Container container, GridBagConstraints constraints, String[] labelTexts, ActionListener actionListener)
	{
		
		//Create the ComboBox object (it's a dropbox)
		JComboBox<String> comboBox = new JComboBox<String>(labelTexts);
		
		
		//Configure some of the ComboBox's options
		comboBox.setEditable(false);
		
		
		//Add an ActionListener that will perform an action when an option is selected, or when the enter key is pressed
		if (actionListener != null)
		{
			
			comboBox.addActionListener(actionListener);
			
		}
		
		
		//Add the ComboBox with it's constraints to the container
		container.add(comboBox, constraints);
		
		
		//Return the ComboBox
		return comboBox;
		
	}
	
	public static JComboBox<String> addComboBox(Container container, GridBagConstraints constraints, String[] labelTexts)
	{
		
		return addComboBox(container, constraints, labelTexts, null);
		
	}
	
	public static JComboBox<String> addComboBox(Container container, GridBagConstraints constraints, List<String> labelTexts, ActionListener actionListener)
	{
		
		return addComboBox(container, constraints, labelTexts.toArray(new String[0]), actionListener);
		
	}
	
	public static JComboBox<String> addComboBox(Container container, GridBagConstraints constraints, List<String> labelTexts)
	{
		
		return addComboBox(container, constraints, labelTexts.toArray(new String[0]), null);
		
	}
	
	public static void replaceComboBoxContents(JComboBox<String> comboBox, List<String> items)
	{
		
		ActionListener[] listeners = comboBox.getActionListeners();
		
		for (int i = 0; i < listeners.length; i++)
		{
			
			comboBox.removeActionListener(listeners[i]);
			
		}
		
		comboBox.removeAllItems();
		
		for (int i = 0; i < items.size(); i++)
		{
			
			comboBox.addItem(items.get(i));
			
		}
		
		for (int i = 0; i < listeners.length; i++)
		{
			
			comboBox.addActionListener(listeners[i]);
			
		}
	}
	
	
	
	public static JButton addButton(Container container, GridBagConstraints constraints, String text, ActionListener actionListener)
	{
		
		JButton button = new JButton(text);
		
		if (actionListener != null) button.addActionListener(actionListener);
		
		container.add(button, constraints);
		
		return button;
		
	}
	
	public static JButton addButton(Container container, GridBagConstraints constraints, String text)
	{
		
		return addButton(container, constraints, text, null);
		
	}
	
	
	
	public static JCheckBox addCheckBox(Container container, GridBagConstraints constraints, ActionListener actionListener)
	{
		
		JCheckBox checkbox = new JCheckBox();
		
		if (actionListener != null)
		{
			
			checkbox.addActionListener(actionListener);
			
		}
		
		container.add(checkbox, constraints);
		
		return checkbox;
		
	}
	
	public static JCheckBox addCheckBox(Container container, GridBagConstraints constraints)
	{
		
		return addCheckBox(container, constraints, null);
		
	}
	
	
	
	public static JLabel addLabel(Container container, GridBagConstraints constraints, String text, Color backgroundColor, int alignment)
	{
		
		JLabel label = new JLabel(text);
		
		label.setHorizontalAlignment(alignment);
		
		if (backgroundColor != null)
		{
			
			label.setBackground(backgroundColor);
			
			label.setOpaque(true);
			
		}
		
		container.add(label, constraints);
		
		return label;
		
	}
	
	public static JLabel addLabel(Container container, GridBagConstraints constraints, String text, Color backgroundColor)
	{
		
		return addLabel(container, constraints, text, backgroundColor, JLabel.CENTER);
		
	}
	
	public static JLabel addLabel(Container container, GridBagConstraints constraints, String text, int alignment)
	{
		
		return addLabel(container, constraints, text, null, alignment);
		
	}
	
	public static JLabel addLabel(Container container, GridBagConstraints constraints, String text)
	{
		
		return addLabel(container, constraints, text, null, JLabel.CENTER);
		
	}
	
	
	
	public static JTextField addTextField(Container container, GridBagConstraints constraints, String defaultText, int columns, int horizontalAlignment)
	{
		
		JTextField text = new JTextField(defaultText, columns);
		
		text.setHorizontalAlignment(horizontalAlignment);
		
		container.add(text, constraints);
		
		return text;
		
	}
	
	public static JTextField addTextField(Container container, GridBagConstraints constraints, String defaultText, int columns)
	{
		
		return addTextField(container, constraints, defaultText, columns, JLabel.CENTER);
		
	}
	
	
	
	public static JProgressBar addProgressBar(Container container, GridBagConstraints constraints)
	{
		
		JProgressBar bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		
		bar.setStringPainted(true);
		
		container.add(bar, constraints);
		
		return bar;
		
	}
	
	
	
	public static JColorChooser addColorChooser(Container container, GridBagConstraints constraints)
	{
		
		JColorChooser chooser = new JColorChooser();
		
		container.add(chooser, constraints);
		
		return chooser;
		
	}
	
	
	
	public static JTextField addLabeledTextField(Container container, int x, int y, String labelText, String defaultText, boolean labelOnTheLeft, int xPixelText, int xSizeText)
	{
		
		JLabel label = new JLabel(labelText);
		
		GridBagConstraints labelConstraints = createConstraints(labelOnTheLeft ? x : x+1, y, 20, 10, 1, 1);
		
		container.add(label, labelConstraints);
		
		
		
		JTextField text = new JTextField(defaultText, 20);
		
		GridBagConstraints textConstrints = createConstraints(labelOnTheLeft ? x+1 : x, y, xPixelText, 10, xSizeText, 1);
		
		container.add(text, textConstrints);
		
		
		
		label.setLabelFor(text);
		
		
		
		return text;
		
	}
	
	public static JTextField addLabeledTextField(Container container, int y, String labelText, String defaultText)
	{
		
		return addLabeledTextField(container, 0, y, labelText, defaultText, true, 100, 3);
		
	}
	
	
	
	public static JTextArea addLabeledTextArea(Container container, int x, int y, int ySize, String labelText, String defaultText, boolean labelOnTheLeft, int xPixelText, int xSizeText)
	{
		
		JLabel label = new JLabel(labelText);
		
		GridBagConstraints labelConstraints = createConstraints(labelOnTheLeft ? x : x+1, y, 0, 0, 1, 1);
		
		container.add(label, labelConstraints);
		
		
		
		JTextArea text = new JTextArea(defaultText, ySize, 20);
		
		GridBagConstraints textConstrints = createConstraints(labelOnTheLeft ? x+1 : x, y, xPixelText, 0, xSizeText, 1);
		
		container.add(text, textConstrints);
		
		
		
		label.setLabelFor(text);
		
		
		
		return text;
		
	}
	
	public static JTextArea addLabeledTextArea(Container container, int y, int ySize, String labelText, String defaultText)
	{
		
		return addLabeledTextArea(container, 0, y, ySize, labelText, defaultText, true, 0, 2);
		
	}
	
	
	
	public static JComboBox<String> addLabeledComboBox(Container container, int x, int y, String labelText, String[] comboBoxTexts, boolean labelOnTheLeft, int xPixelText, int xSizeText)
	{
		
		JLabel label = new JLabel(labelText);
		
		GridBagConstraints labelConstraints = createConstraints(labelOnTheLeft ? x : x+1, y, 20, 10, 1, 1);
		
		container.add(label, labelConstraints);
		
		
		
		JComboBox<String> comboBox = new JComboBox<String>(comboBoxTexts);
		
		comboBox.setEditable(false);
		
		GridBagConstraints comboBoxConstrints = createConstraints(labelOnTheLeft ? x+1 : x, y, xPixelText, 10, xSizeText, 1);
		
		container.add(comboBox, comboBoxConstrints);
		
		
		
		label.setLabelFor(comboBox);
		
		
		
		return comboBox;
		
	}
	
	public static JComboBox<String> addLabeledComboBox(Container container, int y, String labelText, String[] comboBoxTexts)
	{
		
		return addLabeledComboBox(container, 0, y, labelText, comboBoxTexts, true, 100, 3);
		
	}
	
	
	
	public static JColorChooser addLabeledColorChooser(Container container, int x, int y, String labelText, Color defaultColor, boolean labelOnTheLeft, int xPixelText, int xSizeText)
	{
		
		JLabel label = new JLabel(labelText);
		
		GridBagConstraints labelConstraints = createConstraints(labelOnTheLeft ? x : x+1, y, 20, 10, 1, 1);
		
		container.add(label, labelConstraints);
		
		
		
		JColorChooser chooser = new JColorChooser();
		
		chooser.setColor(defaultColor);
		
		GridBagConstraints chooserBoxConstrints = createConstraints(labelOnTheLeft ? x+1 : x, y, xPixelText, 10, xSizeText, 1);
		
		container.add(chooser, chooserBoxConstrints);
		
		
		
		label.setLabelFor(chooser);
		
		
		
		return chooser;
		
	}
	
	
	
	public static void updateConstraints(Container container, Component component, GridBagConstraints constraints)
	{
		
		container.remove(component);
		
		container.add(component,constraints);
		
		container.revalidate();
		
		container.repaint();
		
	}
	
	
	
	public static String findTextFromLabel(Component[] components, String labelText)
	{
		
		for (int i=0; i<components.length; i++)
		{
			
			Component component = components[i];
			
			if (component instanceof JLabel)
			{
				
				JLabel label = (JLabel) component;
				
				if (label.getText().equals(labelText))
				{
					
					JTextComponent text = (JTextComponent) label.getLabelFor();
					
					return text.getText();
					
				}
			}
		}
		
		System.out.println(new Date().toString().substring(11, 20) + "  ERROR:   Cannot find component with label ' " + labelText + " '");
		
		return null;
		
	}
	
	
	
	public static Boolean findCheckBoxFromLabel(Component[] components, String labelText)
	{
		
		for (int i=0; i<components.length; i++)
		{
			
			Component component = components[i];
			
			if (component instanceof JLabel)
			{
				
				JLabel label = (JLabel) component;
				
				if (label.getText().equals(labelText))
				{
					
					JCheckBox checkbox = (JCheckBox) label.getLabelFor();
					
					return checkbox.isSelected();
					
				}
			}
		}
		
		System.out.println(new Date().toString().substring(11, 20) + "   ERROR:   Cannot find component with label ' " + labelText + " '");
		
		return null;
		
	}
	
	
	
	public static String findComboBoxFromLabel(Component[] components, String labelText)
	{
		
		for (int i=0; i<components.length; i++)
		{
			
			Component component = components[i];
			
			if (component instanceof JLabel)
			{
				
				JLabel label = (JLabel) component;
				
				if (label.getText().equals(labelText))
				{
					
					JComboBox<?> box = (JComboBox<?>) label.getLabelFor();
					
					String text = String.valueOf(box.getSelectedItem());
					
					return text; 
					
				}
			}
		}
		
		System.out.println(new Date().toString().substring(11, 20) + "   ERROR:   Cannot find component with label ' " + labelText + " '");
		
		return null;
		
	}
	
	
	
	public static void setFont(javax.swing.plaf.FontUIResource font)
	{
		
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		
		while (keys.hasMoreElements())
		{
			
			Object key = keys.nextElement();
			
			Object value = UIManager.get(key);
			
			if (value instanceof javax.swing.plaf.FontUIResource)
			{
				
				UIManager.put (key, font);
				
			}
		}
	} 
	
	
	
	public static List<String> stringToList(String string, String splittingRegex, boolean trim, boolean toLowerCase, boolean intern)
	{
		
		if (string.equals("")) return new ArrayList<String>(0);
		
		List<String> list = new ArrayList<String>(Arrays.asList(string.split(splittingRegex)));
		
		if (trim)
		{
			
			for (int i = 0; i < list.size(); i++)
			{
				
				list.set(i, list.get(i).trim());
				
			}
		}
		
		if (toLowerCase)
		{
			
			for (int i = 0; i < list.size(); i++)
			{
				
				list.set(i, list.get(i).toLowerCase());
				
			}
		}
		
		if (intern)
		{
			
			for (int i = 0; i < list.size(); i++)
			{
				
				list.set(i, list.get(i).intern());
				
			}
		}
		
		return list;
		
	}
	
	
	
	static String[] stringListToArray(List<String> list)
	{
		
		int size = list.size();
		
		String[] array = new String[size];
		
		for (int i=0; i<size; i++)
		{
			
			array[i] = list.get(i);
			
		}
		
		return array;
		
	}
	
	
	
	public static String pad(String string, int length)
	{
		
		while(string.length() < length) string = string + " ";
		
		return string;
		
	}
	
	public static String pad(int number, int length)
	{
		
		String string = Integer.toString(number);
		
		while(string.length() < length) string = "0" + string;
		
		return string;
		
	}
	
	
	
	static List<File> getFilesInFolder(String pathName, int getInSubfolders)
	{
		
		if (!pathName.endsWith(File.separator)) pathName = pathName + File.separator;
		
		
		
		List<File> returnedFiles = new ArrayList<File>();
		
		File folder = new File(pathName);
		
		if (!folder.isDirectory())
		{
			
			System.out.println(new Date().toString().substring(11, 20) + " ERROR:   Specified path ' " + pathName + " ' is not a folder");
			
			return returnedFiles;
			
		}
		
		File[] files = folder.listFiles();
		
		
		
		for (int i=0;i<files.length;i++)
		{
			
			String filePathName = files[i].getPath();
			
			if (files[i].isDirectory())
			{
				
				if (getInSubfolders > 0) returnedFiles.addAll(getFilesInFolder(filePathName, getInSubfolders-1));
			
			}
			
			else
			{
				
				returnedFiles.add(files[i]);
				
			}
		}
		
		return returnedFiles;
		
	}
	
	
	
	public static String getDate()
	{
		
		Date date = new Date();
		
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		
		int year = localDate.getYear();
		
		int month = localDate.getMonthValue();
		
		int day = localDate.getDayOfMonth();
		
		int hour = localDateTime.getHour();
		
		int minute = localDateTime.getMinute();
		
		int second = localDateTime.getSecond();
		
		return year + "-" + pad(month, 2) + "-" + pad(day, 2) + " " + pad(hour, 2) + "h" + pad(minute, 2) + "m" + pad(second, 2) + "s";
		
	}
	
	public static String getTime()
	{
		
		Date date = new Date();
		
		LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		
		int hour = localDateTime.getHour();
		
		int minute = localDateTime.getMinute();
		
		int second = localDateTime.getSecond();
		
		return pad(hour, 2) + "h " + pad(minute, 2) + "m " + pad(second, 2) + "s";
		
	}
	
	
	
	public static void logExceptionStackTrace(Exception exception)
	{
		
		StringWriter stringWriter = new StringWriter();
		
		PrintWriter printWriter = new PrintWriter(stringWriter);
		
		exception.printStackTrace(printWriter);
		
		String stringStackTrace = stringWriter.toString(); 
		
		try
		{
			
			Files.write(Paths.get(System.getProperty("user.home"), "Desktop", "StackTrace.txt"), stringStackTrace.getBytes());
			
		} catch (IOException e)
		{
			
			System.out.println(new Date().toString().substring(11, 20) + " ERROR:   Could not log stack trace.");
			
		}
	}
}





class ButtonEnablerLaterWorker extends SwingWorker<Integer, Integer>
{
	
	JButton button;
	
	long sleepMillis;
	
	
	
	ButtonEnablerLaterWorker(JButton button, long sleepMillis)
	{
		
		this.button = button;
		
		this.sleepMillis = sleepMillis;
		
	}
	
	@Override
	protected Integer doInBackground()
	{
		
		try
		{
			
			Thread.sleep(sleepMillis);
			
		} catch (InterruptedException e)
		{
			
			System.out.println(new Date().toString().substring(11, 20) + " ERROR:   Sleep interrupted. Enabling button now.");
			
		}
		
		button.setEnabled(true);
		
		return 0;
		
	}
}