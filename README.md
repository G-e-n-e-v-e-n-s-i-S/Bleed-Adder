# Instructions

Download the Bleed.Adder.jar file from the Releases page (link on the right) and run it by double-clicking on it.

(You must have the Java Runtime Environment installed: https://www.java.com/en/download/manual.jsp)

Specify the folder containing the card images.

Define the width and height of the bleed to add. This is in percentage.
4.8% horizontal bleed will add 18 pixels on either sides of a 375 pixels wide image (150 DPI).
3.6% vertical bleed will add 19 pixels on the top and bottom of a 523 pixels tall image (150 DPI).
These default values should work well for any size, but you might want to increase them a bit.

Define which color the bleed should be.

Decide if the corners should be recolored.
If the images contain white corners (like some Magic Set Editor templates output), select yes.
If the corners are already filled in with the card frame, a border or art, select no.

Hit the "Add Bleed Margins" button.

The program will create a new folder named "Bleed" inside the specified folder, and store the results there.
