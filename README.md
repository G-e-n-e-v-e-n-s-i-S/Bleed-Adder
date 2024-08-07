# Instructions

Download the "Bleed.Adder.jar" file from the Releases page (link on the right) and run it by double-clicking on it.

(You must have the Java Runtime Environment installed: https://www.java.com/en/download/)

Specify the folder containing the card images.

Define the width and height of the bleed to add. This is in percentage.
4.8% horizontal bleed will add 18 pixels on either sides of a 375 pixels wide image (150 DPI).
3.6% vertical bleed will add 19 pixels on the top and bottom of a 523 pixels tall image (150 DPI).
These default values should work well for any size, but you might want to increase them a bit.

Define which color the bleed should be. "Auto" will look at the bottom border of the card, and use that color.

Decide if the corners should be recolored.
If the images contain white corners (like some Magic Set Editor templates output), select yes.
If the corners are already filled in with the card frame, a border or art, select no.

The program will create a new folder named "Bleed Adder Results" inside the specified folder, and store the results there. 
Any existing images found at these locations, for example from a previous execution of the program, will be overwritten without warning!!!
The original images will not be modified or overwritten though.

The program will also split DFC cards into their constituant faces.

Hit the "Add Bleed Margins" button to run the program.
