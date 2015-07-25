package gui;

import gui.domino.DominoDisplay;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;

/**
 * Helper for creating, storing, and altering images
 */
public class ImageHelper {
		
	private static final String imagePath = "images/";
	
	private static ImageIcon trainIcon = new ImageIcon(getImage(MexicanTrainGUI.class, imagePath + "train.png"));
	private static ImageIcon turnIcon = new ImageIcon(makeColorTransparent(MexicanTrainGUI.class, imagePath + "turn.png", Color.WHITE));
	
	private static ImageIcon[] dominoEndIcons = new ImageIcon[13];
	static {
		for (int pipCount = 0; pipCount <= 12; pipCount++)
			dominoEndIcons[pipCount] = new ImageIcon(makeColorTransparent(MexicanTrainGUI.class, imagePath + "domino_end_" + pipCount + ".png", Color.WHITE));
	}
	
	/**
	 * Method that will trigger the building of all static images.  
	 * This can be done at the beginning of the program to specifically catch any image build areas from the start.
	 */
	public static void loadImages() {}
	
	/**
	 * Returns the train image icon.
	 * @return the train image icon
	 */
	public static ImageIcon getTrainIcon() {
		return trainIcon;
	}
	
	/**
	 * Returns the turn image icon.
	 * @return the turn image icon
	 */
	public static ImageIcon getTurnIcon() {
		return turnIcon;
	}
	
	/**
	 * Returns a domino end/side with the given pip count.
	 * @param pipCount number of pips on the desired domino end image
	 * @param imageSize square size to make the image
	 * @return a domino end/side with the given pip count
	 */
	public static ImageIcon getDominoEndIcon(int pipCount, int imageSize) {
		ImageIcon icon = dominoEndIcons[pipCount];
		if (imageSize != DominoDisplay.DOMINO_END_MAX_SIZE)
			icon = resizeIcon(icon, imageSize);
		return icon;
	}
	
	/**
	 * Returns the image icon at the given filename.
	 * @param filename name of the image file
	 * @return the image icon at the given filename
	 */
	public static ImageIcon getIcon(String filename) {
		try {
			File file = new File(imagePath + filename);
			if (!file.exists())
				return null;

			return new ImageIcon(imagePath + filename);
		}
		catch (Exception e) { return null; }
	}
	
	/**
	 * Returns the given icon in gray-scale.
	 * @param icon image icon to return in gray-scale
	 * @return a copy of the given icon in gray-scale
	 */
	public static ImageIcon toGrayscale(ImageIcon icon) {
		return new ImageIcon(GrayFilter.createDisabledImage(((ImageIcon)icon).getImage()));
	}
	
	/**
	 * Returns a buffered image of the given internal file.
	 * @param guiClass internal GUI class that is used to specify the location of the image
	 * @param filename name of the internal image file
	 * @return a buffered image of the given internal file
	 */
	public static BufferedImage getImage(Class<?> guiClass, String filename) {
		try { 
        	return ImageIO.read(guiClass.getResource(filename));   
        } 
        catch (Exception e) { return null; }
	}
	
	/**
	 * Returns an image icon at the given filename with the specified color made transparent.
	 * @param filename name of the image file
	 * @param transparentColor color to make transparent
	 * @return an image icon at the given filename with the specified color made transparent
	 */
	public static ImageIcon getTransparentIcon(String filename, Color transparentColor) {
		try {
			return new ImageIcon(makeColorTransparent(imagePath + filename, transparentColor));
		}
		catch (Exception e) { return null; }
	}
		
	/**
	 * Returns a buffered image at the given filename with the specified color made transparent.
	 * @param filename name of the image file
	 * @param color color to make transparent
	 * @return a buffered image at the given filename with the specified color made transparent
	 */
	public static BufferedImage makeColorTransparent(String filename, Color color) {
        try { 
        	return makeColorTransparent(ImageIO.read(new File(filename)), color);   
        } 
        catch (Exception e) { return null; }
	}
	
	/**
	 * Returns a buffered image of the given internal file with the specified color made transparent.
	 * @param guiClass internal GUI class that is used to specify the location of the image
	 * @param filename name of the internal image file
	 * @param color color to make transparent
	 * @return a buffered image of the given internal file with the specified color made transparent
	 */
	public static BufferedImage makeColorTransparent(Class<?> guiClass, String filename, Color color) {
        try { 
        	return makeColorTransparent(ImageIO.read(guiClass.getResource(filename)), color);   
        } 
        catch (Exception e) { Messenger.error(e.getMessage(), filename); return null; } 
	}
	
	/**
	 * Returns the given buffered image with the specified color made transparent.
	 * @param image buffered image to copy and alter
	 * @param color color to make transparent
	 * @return the given buffered image with the specified color made transparent
	 */
	public static BufferedImage makeColorTransparent(BufferedImage image, Color color) {        
        final int width = image.getWidth();
        final int height = image.getHeight();
		BufferedImage transparentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = transparentImage.createGraphics();   
		g.setComposite(AlphaComposite.Src);   
		g.drawImage(image, null, 0, 0);   
		g.dispose();   
		for(int i = 0; i < height; i++) {   
			for(int j = 0; j < width; j++) {   
				if(transparentImage.getRGB(j, i) == Color.WHITE.getRGB()) {   
					transparentImage.setRGB(j, i, 0x8F1C1C);   
				}   
			}   
		}
		return transparentImage;
	}
	
	/**
	 * Resizes and returns the given icon.
	 * @param icon icon to resize
	 * @param newSize size to transform to
	 * @return a resized image icon
	 */
	public static ImageIcon resizeIcon(ImageIcon icon, int newSize) {
		return new ImageIcon(icon.getImage().getScaledInstance(newSize, newSize, Image.SCALE_SMOOTH));
	}
	
	/**
	 * Rotates an image by the given degree (clockwise) and returns it.
	 * @param icon image icon to copy, rotate, and return
	 * @param rotateAngle the degree/angle to rotate by (clockwise)
	 * @return an image by the given degree and returns it
	 */
	public static ImageIcon rotateIcon(ImageIcon icon, int rotateAngle) {
		final int width = icon.getIconWidth();
        final int height = icon.getIconHeight();
		BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = rotatedImage.createGraphics();   
        g.rotate(Math.toRadians(rotateAngle), width/2, height/2);   
        g.drawImage(icon.getImage(), 0, 0, null);
        return new ImageIcon(rotatedImage);
	}
	
	/**
	 * Creates and returns a cursor from the given image.
	 * @param icon image icon to return as a cursor
	 * @param stretchIcon whether or not the image icon should be stretched to fill a 32x32 cursor image or if it should leave it the same size in the cursor's upper-left corner. 
	 * @return a cursor from the given image
	 */
	public static Cursor createCursor(ImageIcon icon, boolean stretchIcon) {
		Image cursorImage;
		if (stretchIcon)
			cursorImage = icon.getImage();
		else {
			cursorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB); 
			Graphics g = cursorImage.getGraphics(); 
			g.drawImage(icon.getImage(), 0, 0, null);
		}
		
		int iconMiddle = icon.getIconWidth() / (stretchIcon ? 1 : 2);
		Point cursorHotspot = new Point(iconMiddle, iconMiddle);
		return Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, cursorHotspot, null);
	}
}
