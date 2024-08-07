/*
 *	Adds margins to the sides of a card to respect print bleeding requirements
 *
 *	Loads all the PNG images in the folder specified in 'loadFolderPath'
 *	Adds the number of pixels specified in 'bleedWidth' to the left and right
 *	Adds the number of pixels specified in 'bleedHeight' to the top and bottom
 *	If using the Genevensis frames, extends frame lines
 *	Optionally, recolors corners
 *	Optionally, splits DFC cards into front and back
 *	Will look for an xml file with the same name as 'loadFolderPath' to find names of DFC backs
 *	Saves the images in a sub folder of the 'loadFolderPath'
*/



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;



public class Bleeder
{
	
	
	
	public static Set<Integer> GenevensisFrameColors = new HashSet<Integer>(Arrays.asList(-2111402, -14393705, -13619152, -9371648, -13605848, -6856192, -11107944, -10991293, -10010486, -1389208, -13601351, -12698050, -7667712, -13538261, -5607680, -9463892, -9610926, -8826717, -1256581, -12745523, -12171706, -6422528, -13207249, -4227072, -8346441, -8821413, -7711046, -1123954, -11037229, -11382190, -5433853, -12744908, -2976768, -7097919, -7769497, -6920256, -859996, -8803621, -10592674, -4310224, -12020669, -1659904, -5323567, -7110027, -5668148, -724538, -6635548, -9539986, -3057070, -11033259, -870912, -4337959, -5859186, -4745259, -66071, -4730644, -8421505, -2067347, -9456277, -14573, -3023129, -4739676, -3756578, -527681, -2759436, -4144960, -929585, -4468810, -535930, -2431248, -2371895, -3422998, -132132, -1182726, -1315861, -5141, -1051157, -134706, -328450, -922389, -1251081, -264236, -2430473, -1973791, -7711, -1904419, -201802, -1116933, -1252383, -1777677, -527929, -3547663, -2631721, -10281, -2955574, -269671, -1840138, -1845035, -2238736, -988487, -4139025, -3289651, -12851, -3743813, -336247, -2563345, -2175029, -2634259));
	
	
	
	public static boolean logToConsole = false;
	
	public static boolean packWhenLogging = true;
	
	public static int failCount = 0;
	
	public static int dfcCount = 0;
	
	public static int rotatedCount = 0;
	
	
	
	public static String saveFolderName = "Bleed Adder Results";
	
	
	
	
	
	//public static void main(String[] args)
	//{
	//	
	//	logToConsole = true;
	//	
	//	bleed("D:\\Magic Set Editor Folder\\Renders\\set-files\\", "0", "0", "Auto", "Yes", "Yes");
	//	
	//}
	
	
	
	
	
	public static void bleed(String loadFolderPath, String bleedWidthPercentString, String bleedHeightPercentString, String bleedColorString, String fillCornersString, String splitDFCString)
	{
		
		System.out.println(new Date().toString().substring(11, 20) + "  INFO:    Starting.");
		
		packWhenLogging = true;
		
		failCount = 0;
		
		dfcCount = 0;
		
		rotatedCount = 0;
		
		
		
		//Load folder
		if (!loadFolderPath.endsWith(File.separator)) loadFolderPath = loadFolderPath + File.separator;
		
		String saveFolderPath = loadFolderPath + saveFolderName + File.separator;
		
		
		
		File loadFolder = new File(loadFolderPath);
		
		File saveFolder = new File(saveFolderPath);
		
		if (!loadFolder.exists())
		{
			
			log("Specified folder path ' " + loadFolderPath + " ' does not exist.", Color.red, "load");
			
			return;
			
		}
		
		if (!loadFolder.isDirectory())
		{
			
			log("Specified folder path ' " + loadFolderPath + " ' is not a folder.", Color.red, "load");
			
			return;
			
		}
		
		
		
		//Try to load cockatrice export xml splitterPaths
		List<String> splitterPaths = new ArrayList<String>();
		
		try
		{
			
			String xmlPath = loadFolderPath.replaceAll(Pattern.quote("-files" + File.separator) + "$", "") + ".xml";
			
			List<String> xmlLines = Files.readAllLines(Paths.get(xmlPath), StandardCharsets.UTF_8);
			
			for (int i = 0; i < xmlLines.size(); i++)
			{
				
				String line = xmlLines.get(i);
				
				if (line.contains("splitterPath=\"/"))
				{
					
					String splitterPath = xmlLines.get(i).replaceAll(".*splitterPath=\"/([^\"]+)\\.png\".*", "$1");
					
					splitterPaths.add(splitterPath);
					
				}
			}
		} catch (IOException e) { }
		
		
		
		//Tally images
		File[] files = loadFolder.listFiles();
		
		List<File> images = new ArrayList<>();
		
		for (int i=0;i<files.length;i++)
		{
			
			File file = files[i];
			
			String filePath = file.getPath();
			
			if (filePath.endsWith(".png"))
			{
				
				images.add(file);
				
			}
		}
		
		int imageCount = images.size();
		
		if (imageCount < 1)
		{
			
			log("No PNG images found in specified folder.", Color.red, "load");
			
			return;
			
		}
		
		else
		{
			
			log(imageCount + " PNG image" + (imageCount > 1 ? "s" : "") + " found.", Color.black, "load");
			
		}
		
		
		
		//Parse bleed size
		double bleedWidthPercent = 0d;
		
		double bleedHeightPercent = 0d;
		
		try
		{
			
			bleedWidthPercent = Double.parseDouble(bleedWidthPercentString.replace(",", "."))/100d;
			
		} catch (Exception e)
		{
			
			log("Could not interpret bleed horizontal size ' " + bleedWidthPercentString + " ' as a number.", Color.red, "logic");
			
			return;
			
		}
		
		if (bleedWidthPercent < 0d)
		{
			
			log("Bleed horizontal size must be positive.", Color.red, "logic");
			
			return;
			
		}
		
		try
		{
			
			bleedHeightPercent = Double.parseDouble(bleedHeightPercentString.replace(",", "."))/100d;
				
		} catch (Exception e)
		{
			
			log("Could not interpret vertical size ' " + bleedHeightPercentString + " ' as a number.", Color.red, "logic");
			
			return;
			
		}
		
		if (bleedHeightPercent < 0d)
		{
			
			log("Bleed vertical size must be positive.", Color.red, "logic");
			
			return;
			
		}
		
		
		
		//Parse color
		Integer bleedColor = -3623936;
		
		if (bleedColorString.equals("Auto")) bleedColor = null;
		
		else if (bleedColorString.equals("Black")) bleedColor = -16777216;
		
		else if (bleedColorString.equals("White")) bleedColor = -1;
		
		else if (bleedColorString.equals("Grey")) bleedColor = -3618616;
		
		else if (!bleedColorString.equals("Gold"))
		{
			
			log("Could not interpret bleed color ' " + bleedColorString + " '.", Color.red, "logic");
			
			return;
			
		}
		
		
		
		//Parse corners
		boolean fillCorners = false;
		
		if (fillCornersString.equals("Yes")) fillCorners = true;
		
		else if (!fillCornersString.equals("No"))
		{
			
			log("Could not interpret fill corners ' " + fillCornersString + " '.", Color.red, "logic");
			
			return;
			
		}
		
		
		
		//Parse dfc split
		boolean splitDFC = false;
		
		if (splitDFCString.equals("Yes")) splitDFC = true;
		
		else if (!splitDFCString.equals("No"))
		{
			
			log("Could not interpret split DFC ' " + splitDFCString + " '.", Color.red, "logic");
			
			return;
			
		}
		
		
		
		//Make destination folder
		if (!saveFolder.exists())
		{
			
			saveFolder.mkdirs();
			
		}
		
		
		
		//Start iterating on the images
		if (!logToConsole)
		{
			
			Launcher.progressBar.setVisible(true);
			
			Launcher.progressBar.setValue(0);
			
			Launcher.progressBar.setMaximum(imageCount);
			
		}
		
		log("Adding bleed...", Color.black, "logic");
		
		
		
		for (int i=0;i<imageCount;i++)
		{
			
			//Display progress
			if (!logToConsole)
			{
				
				Launcher.progressBar.setValue(i);
				
				Launcher.progressBar.setString((i+1) + "/" + imageCount + "        " + (100*(i+1)/imageCount) + "%");
				
			}
			
			else if ((100*(i+1)/imageCount)%10 == 0 && (100*(i)/imageCount)%10 == 9)
			{
				
				System.out.println(new Date().toString().substring(11, 20) + "  INFO:    " + (100*(i+1)/imageCount) + "%");
				
			}
			
			
			
			//Load image
			BufferedImage image = null;
			
			BufferedImage front = null;
			
			BufferedImage back = null;
			
			try
			{
				
				image = ImageIO.read(images.get(i));
				
			} catch (IOException e)
			{
				
				log("Could not load image ' " + images.get(i).getName() + " '.", Color.red, "logic");
				
				failCount++;
				
				continue;
				
			}
		    
			String name = images.get(i).getName();
			
			int width = image.getWidth();
			
			int height = image.getHeight();
			
			
			
			//Check if it's one of the Genevensis frames
			boolean isGenevensisFrame = isGenevensisFrame(image);
			
			
			
			//Cut in two if it's a double sided frame
			if
			(
				splitDFC &&
				(
						(width == 753 && height == 523)
					||	(width == 752 && height == 523)
					||	(width == 751 && height == 523)
					||	(width == 1295 && height == 902)
					||	(width == 1294 && height == 902)
					||	(width == 1293 && height == 902)
					||	(width == 1491 && height == 1039)
					||	(width == 1490 && height == 1039)
					||	(width == 1489 && height == 1039)
					||	(width == 1503 && height == 1046)
					||	(width == 1502 && height == 1046)
					||	(width == 1501 && height == 1046)
				)
			)
			{
				
				dfcCount++;
				
				
				
				int faceOffset =	width == 753 ? 378 :
									width == 752 ? 377 :
									width == 751 ? 376 :
									width == 1295 ? 649 :
									width == 1294 ? 648 :
									width == 1293 ? 647 :
									width == 1491 ? 747 :
									width == 1490 ? 746 :
									width == 1489 ? 745 :
									width == 1503 ? 753 :
									width == 1502 ? 752 :
									751 ;
				
				int faceWidth =		width == 753 ? 375 :
									width == 752 ? 375 :
									width == 751 ? 375 :
									width == 1295 ? 646 :
									width == 1294 ? 646 :
									width == 1293 ? 646 :
									width == 1491 ? 744 :
									width == 1490 ? 744 :
									width == 1489 ? 744 :
									750 ;
				
				
				
				front = new BufferedImage(faceWidth, height, BufferedImage.TYPE_INT_ARGB);
				
				Graphics2D graphicsFront = front.createGraphics();
				
				graphicsFront.drawImage(image.getSubimage(0, 0, faceWidth, height), 0, 0, null);
				
				graphicsFront.dispose();
				
				
				
				back = new BufferedImage(faceWidth, height, BufferedImage.TYPE_INT_ARGB);
				
				Graphics2D graphicsBack = back.createGraphics();
				
				graphicsBack.drawImage(image.getSubimage(faceOffset, 0, faceWidth, height), 0, 0, null);
				
				graphicsBack.dispose();
				
			}
			
			//Rotate if it's a battle frame
			else if (width > height)
			{
				
				rotatedCount++;
				
				
				
				front = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
				
				Graphics2D graphics = front.createGraphics();
				
				graphics.translate((width - height) / 2, (width - height) / 2);
				
				graphics.rotate(3 * Math.PI / 2, height / 2, width / 2);
				
				graphics.drawRenderedImage(image, null);
				
				graphics.dispose();
			    
			}
			
			else
			{
				
				front = image;
				
			}
			
			
			
			//Calculate bleed in pixels, clamp it
			int bleedWidth = (int) Math.round(front.getWidth() * bleedWidthPercent);
			
			int bleedHeight = (int) Math.round(front.getHeight() * bleedHeightPercent);
			
			if (bleedWidth < 0) bleedWidth = 0;
			
			if (bleedWidth > 5000) bleedWidth = 5000;
			
			if (bleedHeight < 0) bleedHeight = 0;
			
			if (bleedHeight > 5000) bleedHeight = 5000;
			
			
			
			//Do the thing we're here for
			if (fillCorners)
			{
				
				front = addCorners(front, false);
				
			}
			
			front = addBorders(front, bleedWidth, bleedHeight, bleedColor);
			
			if (isGenevensisFrame)
			{
				
				front = addGenevensisLines(front, bleedWidth, bleedHeight);
				
			}
			
			
			
			if (back != null)
			{
				
				if (fillCorners)
				{
					
					back = addCorners(back, false);
					
				}
				
				back = addBorders(back, bleedWidth, bleedHeight, bleedColor);
				
				if (isGenevensisFrame)
				{
					
					back = addGenevensisLines(back, bleedWidth, bleedHeight);
					
				}
				
				
				
				name = name.substring(0, name.length()-4);
				
				String nameFront = null;
				
				String nameBack = null;
				
				if (name.contains(" -- "))
				{
					
					String[] split = name.split(" -- ");
					
					nameFront = saveFolderPath + split[0] + ".png";
					
					nameBack = saveFolderPath + split[1] + ".png";
					
				}
				
				else if (splitterPaths.contains(name))
				{
					
					int index = splitterPaths.indexOf(name);
					
					nameFront = saveFolderPath + name + ".png";
					
					if (index+1 < splitterPaths.size())
					{
						
						nameBack = saveFolderPath + splitterPaths.get(index+1) + ".png";
						
					}
					
					else nameBack = saveFolderPath + name + "[BACK].png";
					
					
				}
				
				else
				{
					
					nameFront = saveFolderPath + name + ".png";
					
					nameBack = saveFolderPath + name + "[BACK].png";
					
					
				}
				
				boolean successFront = saveImage(front, nameFront);
				
				boolean successBack = saveImage(back, nameBack);
				
				if (!successFront) failCount++;
				
				if (!successBack) failCount++;
				
			}
			
			else
			{
				
				boolean success = saveImage(front, saveFolderPath + name);
				
				if (!success) failCount++;
				
			}
		}
		
		
		
		//Log results
		if (!logToConsole)
		{
			
			Launcher.progressBar.setVisible(false);
			
		}
		
		if (failCount > 0)
		{
			
			log(failCount + " card" + (failCount > 1 ? "s" : "") + " could not be saved.", Color.red, "logic");
			
		}
		
		else if (dfcCount > 0 || rotatedCount > 0)
		{
			
			String dfcString = dfcCount > 0 ? dfcCount + " double faced card" + (dfcCount > 1 ? "s" : "") + " split." : "";
			
			String rotatedString = rotatedCount > 0 ? rotatedCount + " battle card" + (rotatedCount > 1 ? "s" : "") + " rotated." : "";
			
			String separator = dfcString != "" && rotatedString != "" ? " " : "";
			
			log(dfcString + separator + rotatedString, Color.black, "logic");
			
		}
		
		else
		{
			
			log("", Color.black, "logic");
			
		}
		
		
		
		int successCount = imageCount + dfcCount - failCount;
		
		log(successCount + " image" + (successCount > 1 ? "s" : "") + " saved in the following folder:", Color.black, "save");
		
		log(saveFolderPath, Color.black, "saveName");
		
		
		
		System.gc();
		
		System.out.println(new Date().toString().substring(11, 20) + "  INFO:    Done.");
		
	}
	
	
	
	
	
	public static BufferedImage addBorders(BufferedImage image, int bleedWidth, int bleedHeight, Integer bleedColor)
	{
		
		//Get info
		int width = image.getWidth();
		
		int height = image.getHeight();
		
		
		
		//Auto detect color
		if (bleedColor == null)
		{
			
			List<Integer> colors = new ArrayList<>();
			
			int x = width/2;
			
			for (int i = -5; i < 5; i++)
			{
				
				if (x+i < 0 || x+i >= width) break;
				
				colors.add(image.getRGB(x, height-1));
				
			}
			
			bleedColor = averageColor(colors);
			
		}
		
		
		
		//Copy image inside a bigger one
		BufferedImage result = new BufferedImage(width + 2*bleedWidth, height + 2*bleedHeight, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D graphics = result.createGraphics();
		
		graphics.setPaint(new Color(bleedColor));
		
		graphics.fillRect(0, 0, width + 2*bleedWidth, height + 2*bleedHeight);
		
		graphics.drawImage(image, bleedWidth, bleedHeight, null);
		
		graphics.dispose();
		
		return result;
		
	}
	
	
	
	public static BufferedImage addCorners(BufferedImage image, boolean average)
	{
		
		//Get info
		int width = image.getWidth();
		
		int height = image.getHeight();
		
		int cornerSize = height / 32;
		
		
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D graphics = result.createGraphics();
		
		graphics.drawRenderedImage(image, null);
		
		graphics.dispose();
		
		
		
		if (average)
		{
			
			//Walk along a diagonal around the corner, get average color on that diagonal
			List<Integer> topLeftColors = new ArrayList<>();
			
			List<Integer> topRightColors = new ArrayList<>();
			
			List<Integer> bottomLeftColors = new ArrayList<>();
			
			List<Integer> bottomRightColors = new ArrayList<>();
			
			for (int i = 0; i < cornerSize + 1; i++)
			{
				
				topLeftColors.add(result.getRGB(i, cornerSize - i));
				
				topRightColors.add(result.getRGB(width - 1 - i, cornerSize - i));
				
				bottomLeftColors.add(result.getRGB(i, height - 1 - cornerSize + i));
				
				bottomRightColors.add(result.getRGB(width - 1 - i, height - 1 - cornerSize + i));
				
				//image.setRGB(i, cornerSize - i, -65536);
				
			}
			
			//saveImage(image, System.getProperty("user.home") + "/Desktop/[TEST].png");
			
			int topLeftColor = averageColor(topLeftColors);
			
			int topRightColor = averageColor(topRightColors);
			
			int bottomLeftColor = averageColor(bottomLeftColors);
			
			int bottomRightColor = averageColor(bottomRightColors);
			
			
			
			//Fill corners with these colors
			for (int x = 0; x < width; x++)
			{
				
				for (int y = 0; y < height; y++)
				{
					
					if (x + y < cornerSize)
					{
						
						result.setRGB(x, y, topLeftColor);
						
					}
					
					else if (width - x + y < cornerSize + 1)
					{
						
						result.setRGB(x, y, topRightColor);
						
					}
					
					else if (x + height - y < cornerSize + 1)
					{
						
						result.setRGB(x, y, bottomLeftColor);
						
					}
					
					else if (width - x + height - y < cornerSize + 2)
					{
						
						result.setRGB(x, y, bottomRightColor);
						
					}
				}
			}
		}
		
		else
		{
			
			//Walk along a diagonal around the corner, extend colors found on that diagonal to the corners
			for (int i = 0; i < cornerSize + 1; i++)
			{
				
				addLine(result, i, cornerSize - i, "top left");
				
				addLine(result, i+1, cornerSize - i, "top left");
				
				
				
				addLine(result, width - 1 - i, cornerSize - i, "top right");
				
				addLine(result, width - 1 - i-1, cornerSize - i, "top right");
				
				
				
				addLine(result, i, height - 1 - cornerSize + i, "bottom left");
				
				addLine(result, i+1, height - 1 - cornerSize + i, "bottom left");
				
				
				
				addLine(result, width - 1 - i, height - 1 - cornerSize + i, "bottom right");
				
				addLine(result, width - 1 - i-1, height - 1 - cornerSize + i, "bottom right");
				
			}
		}
		
		
		
		return result;
		
	}
	
	
	
	public static BufferedImage addGenevensisLines(BufferedImage image, int bleedWidth, int bleedHeight)
	{
		
		//Get info
		int width = image.getWidth();
		
		int height = image.getHeight();
		
		int cornerSize = height / 32;
		
		
		
		//Add lines
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
	    Graphics2D graphics = result.createGraphics();
	    
	    graphics.drawRenderedImage(image, null);
		
	    graphics.dispose();
	    
		for (int x = bleedWidth; x < width - bleedWidth; x++)
		{
			
			addGenevensisLine(result, x, bleedHeight, "horizontal");
			
		}
		
		for (int y = bleedHeight; y < height - bleedHeight; y++)
		{
			
			addGenevensisLine(result, bleedWidth, y, "vertical");
			
			addGenevensisLine(result, width-bleedWidth-1, y, "vertical");
			
		}
		
		for (int z = 0; z < cornerSize; z++)
		{
			
			addGenevensisLine(result, bleedWidth+z, bleedHeight+cornerSize-z, "up left");
			
			addGenevensisLine(result, bleedWidth+z+1, bleedHeight+cornerSize-z, "up left");
			
			addGenevensisLine(result, width-bleedWidth-1-cornerSize+z, bleedHeight+z, "down right");
			
			addGenevensisLine(result, width-bleedWidth-1-cornerSize+z-1, bleedHeight+z, "down right");
			
		}
		
		return result;
		
	}
	
	
	
	public static void addGenevensisLine(BufferedImage image, int x, int y, String checkDirection)
	{
		
		int width = image.getWidth();
		
		int height = image.getHeight();
		
		//Check that there is enough room
		if
		(
				x-4 < 0
			||	x+4 >= width
			||	y-4 < 0
			||	y+4 >= height
		)
		{
			
			return;
			
		}
		
		
		
		int color = image.getRGB(x, y);
		
		//Only extend lines that are of the frame colors
		if (!GenevensisFrameColors.contains(color)) return;
		
		
		
		//Only extend lines that are 3 or more pixels thick
		int adjacentCount = 0;
		
		if (checkDirection.equals("horizontal"))
		{
			
			if (image.getRGB(x+1, y) == color) adjacentCount++;
			
			if (image.getRGB(x+2, y) == color) adjacentCount++;
			
			if (image.getRGB(x-1, y) == color) adjacentCount++;
			
			if (image.getRGB(x-2, y) == color) adjacentCount++;
			
		}
		
		else if (checkDirection.equals("vertical"))
		{
			
			if (image.getRGB(x, y+1) == color) adjacentCount++;
			
			if (image.getRGB(x, y+2) == color) adjacentCount++;
			
			if (image.getRGB(x, y-1) == color) adjacentCount++;
			
			if (image.getRGB(x, y-2) == color) adjacentCount++;
			
		}
		
		else if (checkDirection.equals("up left"))
		{
			
			if (image.getRGB(x-1, y+1) == color) adjacentCount++;
			
			if (image.getRGB(x-2, y+2) == color) adjacentCount++;
			
			if (image.getRGB(x+1, y-1) == color) adjacentCount++;
			
			if (image.getRGB(x+2, y-2) == color) adjacentCount++;
			
		}
		
		else if (checkDirection.equals("down right"))
		{
			
			if (image.getRGB(x+1, y+1) == color) adjacentCount++;
			
			if (image.getRGB(x+2, y+2) == color) adjacentCount++;
			
			if (image.getRGB(x-1, y-1) == color) adjacentCount++;
			
			if (image.getRGB(x-2, y-2) == color) adjacentCount++;
			
		}
		
		if (adjacentCount < 2) return;
		
		
		
		//Determine in which direction to extend
		String extendDirection = null;
		
		if
		(
				image.getRGB(x+1, y+1) == color
			&&	image.getRGB(x+2, y+2) == color
			&&	image.getRGB(x+3, y+3) == color
			&&	image.getRGB(x+4, y+4) == color
		)
		{
			
			extendDirection = "top left";
			
		}
		
		else if
		(
				image.getRGB(x-1, y+1) == color
			&&	image.getRGB(x-2, y+2) == color
			&&	image.getRGB(x-3, y+3) == color
			&&	image.getRGB(x-4, y+4) == color
		)
		{
			
			extendDirection = "top right";
			
		}
		
		else if
		(
				image.getRGB(x+1, y-1) == color
			&&	image.getRGB(x+2, y-2) == color
			&&	image.getRGB(x+3, y-3) == color
			&&	image.getRGB(x+4, y-4) == color
		)
		{
			
			extendDirection = "bottom left";
			
		}
		
		else if
		(
				image.getRGB(x-1, y-1) == color
			&&	image.getRGB(x-2, y-2) == color
			&&	image.getRGB(x-3, y-3) == color
			&&	image.getRGB(x-4, y-4) == color
		)
		{
			
			extendDirection = "bottom right";
			
		}
		
		if (extendDirection != null) addLine(image, x, y, extendDirection);
		
	}
	
	
	
	public static void addLine(BufferedImage image, int x, int y, String extendDirection)
	{
		
		int width = image.getWidth();
		
		int height = image.getHeight();
		
		int color = image.getRGB(x, y);
		
		
		
		int p = 1;
			
		if (extendDirection.equals("top left"))
		{
			
			while (x-p >= 0 && y-p >= 0)
			{
				
				image.setRGB(x-p, y-p, color);
				
				p++;
				
			}
		}
		
		else if (extendDirection.equals("top right"))
		{
			
			while (x+p < width && y-p >= 0)
			{
				
				image.setRGB(x+p, y-p, color);
				
				p++;
				
			}
		}
		
		else if (extendDirection.equals("bottom left"))
		{
			
			while (x-p >= 0 && y+p < height)
			{
				
				image.setRGB(x-p, y+p, color);
				
				p++;
				
			}
		}
		
		else if (extendDirection.equals("bottom right"))
		{
			
			while (x+p < width && y+p < height)
			{
				
				image.setRGB(x+p, y+p, color);
				
				p++;
				
			}
		}
	}
	
	
	
	public static boolean isGenevensisFrame(BufferedImage image)
	{
		
		int width = image.getWidth();
		
		int height = image.getHeight();
		
		int offsetX = 0;
		
		int offsetY = 0;
		
		if (width == 750 && height == 1046)
		{
			
			offsetX = 658;
			
			offsetY = 29;
			
		}
		
		else if (width == 1046 && height == 750)
		{
			
			offsetX = 943;
			
			offsetY = 18;
			
		}
		
		else if (width == 1500 && height == 1046)
		{
			
			offsetX = 1408;
			
			offsetY = 29;
			
		}
		
		else return false;
		
		
		
		for (int x = 0; x < 3; x++)
		{
			
			for (int y = 0; y < 3; y++)
			{
				
				int color = image.getRGB(offsetX + x, offsetY + y);
				
				if (!GenevensisFrameColors.contains(color)) return false;
				
			}
		}
		
		return true;
		
	}
	
	
	
	
	
	static int averageColor(Collection<Integer> colors)
	{
		
		int size = colors.size();
		
		
		
		if (size == 1) return colors.iterator().next();
		
		else if (size == 0) System.out.println(new Date().toString().substring(11, 20) + "  ERROR:   Trying to average zero colors.");
		
		
		
		int r = 0;
		
		int g = 0;
				
		int b = 0;
				
		int a = 0;
		
		for (int color : colors)
		{
			
			Color colorObject = new Color(color);
			
			r += colorObject.getRed();
			
			g += colorObject.getGreen();
			
			b += colorObject.getBlue();
			
			a += colorObject.getAlpha();
			
		}
		
		r = r/size;
		
		g = g/size;
		
		b = b/size;
		
		a = a/size;
		
		
		
		return rgbaToInt(r, g, b, a);
		
	}
	
	static int rgbaToInt(int r, int g, int b, int a)
	{
		
		return (a << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
		
	}
	
	
	
	
	
	static boolean saveImage(BufferedImage image, String filePath)
	{
		
		File saveFile = new File(filePath);
	
		try
		{
			
			ImageIO.write(image, "png", saveFile);
			
		} catch (IOException e)
		{
			
			log("Could not save image ' " + filePath + " '.", Color.red, "save");
			
			return false;
			
		}
		
		return true;
		
	}
	
	
	

	
	public static void log(String text, Color color, String out)
	{
		
		boolean textIsEmpty = text == null || text.equals("");
		
		if (logToConsole)
		{
			
			if (textIsEmpty) return;
			
			System.out.println(new Date().toString().substring(11, 20) + (color == Color.black ? "  INFO:    " : "  ERROR:   " ) + text);
			
		}
		
		else
		{
			
			if (textIsEmpty)
			{
				
				text = "";
				
				color = Color.black;
				
			}
			
			if (out.equals("load"))
			{
				
				if (!color.equals(Color.red) && Launcher.loadMessage.getForeground().equals(Color.red)) return;
				
				Launcher.loadMessage.setText(text);
				
				Launcher.loadMessage.setForeground(color);
				
			}
			
			else if (out.equals("logic"))
			{
				
				if (!color.equals(Color.red) && Launcher.logicMessage.getForeground().equals(Color.red)) return;
				
				Launcher.logicMessage.setText(text);
				
				Launcher.logicMessage.setForeground(color);
				
			}
			
			else if (out.equals("save"))
			{
				
				if (!color.equals(Color.red) && Launcher.saveMessage.getForeground().equals(Color.red)) return;
				
				Launcher.saveMessage.setText(text);
				
				Launcher.saveMessage.setForeground(color);
				
			}
			

			else
			{
				
				if (!color.equals(Color.red) && Launcher.saveNameMessage.getForeground().equals(Color.red)) return;
				
				Launcher.saveNameMessage.setText(text);
				
				Launcher.saveNameMessage.setForeground(color);
				
			}
			
			
			
			if (color.equals(Color.red) || packWhenLogging) Launcher.window.pack();
			
		}
	}
}





class BleederWorker extends SwingWorker<Integer, Integer>
{
	
	String loadFolderPath;
	
	String bleedWidthPercentString;
	
	String bleedHeightPercentString;
	
	String bleedColorString;
	
	String fillCornersString;
	
	String splitDFCString;
	
	BleederWorker(String loadFolderPath, String bleedWidthPercentString, String bleedHeightPercentString, String bleedColorString, String fillCornersString, String splitDFCString)
	{ 
		
		this.loadFolderPath = loadFolderPath;
		
		this.bleedWidthPercentString = bleedWidthPercentString;
		
		this.bleedHeightPercentString = bleedHeightPercentString;
		
		this.bleedColorString = bleedColorString;
		
		this.fillCornersString = fillCornersString;
		
		this.splitDFCString = splitDFCString;
		
    }
	
	@Override
    protected Integer doInBackground()
    {
    	
    	Bleeder.bleed(loadFolderPath, bleedWidthPercentString, bleedHeightPercentString, bleedColorString, fillCornersString, splitDFCString);
		
    	return 0;
    	
    }
}
