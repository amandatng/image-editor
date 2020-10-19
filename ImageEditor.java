import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;


public class ImageEditor {
	
	private static JLabel imageLabel;
	private static JPanel panel;
	private static BufferedImage image;
	private static BufferedImage image2;
	private static String fileName;
	private static String fileNameInit;
	private static JFileChooser fileChooser = new JFileChooser();
	private static int imageWidth;
	private static int imageHeight;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Image Editor GUI");
		panel = new JPanel();
		imageLabel = new JLabel();
		JMenu file = new JMenu("File");
		JMenu options = new JMenu("Options");
		JMenuBar menuBar = new JMenuBar();
		JMenuItem open = new JMenuItem("Open");
		JMenuItem save = new JMenuItem("Save as");
		JMenuItem exit = new JMenuItem("Exit");
		JMenuItem restore = new JMenuItem("Restore to Original");
		JMenuItem horizontalFlip = new JMenuItem("Horizontal Flip");
		horizontalFlip.setActionCommand("HFlip");
		JMenuItem verticalFlip = new JMenuItem("Vertical Flip");
		JMenuItem grayScale = new JMenuItem("Gray Scale");
		JMenuItem sepiaTone = new JMenuItem("Sepia Tone");
		sepiaTone.setActionCommand("Sepia");
		JMenuItem invert = new JMenuItem("Invert Colours");
		invert.setActionCommand("Invert");
		JMenuItem blur = new JMenuItem("Gaussian Blur");
		JMenuItem bulge = new JMenuItem("Bulge Effect");
		
		//File Menu
		save.addActionListener(new Save());
		open.addActionListener(new OpenImage());
		exit.addActionListener(new Exit());
		open.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		save.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		exit.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		file.add(open);
		file.add(save); 
		file.addSeparator();
		file.add(exit);
		
		//Options Menu
		restore.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		horizontalFlip.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		verticalFlip.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		grayScale.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		sepiaTone.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		invert.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		blur.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
		bulge.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_B, ActionEvent.CTRL_MASK));	
		invert.addActionListener(new Filters());
		bulge.addActionListener(new Bulge());
		blur.addActionListener(new GaussianBlur());
		sepiaTone.addActionListener(new Filters());
		grayScale.addActionListener(new Filters());
		horizontalFlip.addActionListener(new Flip());
		restore.addActionListener(new Restore());
		verticalFlip.addActionListener(new Flip());	
		options.add(restore);	
		options.addSeparator();
		options.add(horizontalFlip);	
		options.add(verticalFlip);	
		options.add(grayScale);	
		options.add(sepiaTone);	
		options.add(invert);	
		options.add(blur);	
		options.add(bulge);
		
		menuBar.add(file);
		menuBar.add(options);
		panel.add(menuBar);
		frame.setJMenuBar(menuBar);
		panel.add(imageLabel);
		
		frame.setSize(new Dimension(1000,1000));
		frame.setVisible(true);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
	}
	
	private static class Bulge implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			double shiftX, shiftY, r, angle, transformedX, transformedY;
			int pixel;
			
			if (imageLabel.getIcon() != null) {
				for (int x = 0; x < imageWidth; x++) {
					for (int y = 0; y < imageHeight; y++) {
							shiftX = x - imageWidth / 2; 
							shiftY = y - imageHeight / 2;			
							r = Math.sqrt(shiftX * shiftX + shiftY * shiftY); //To polar coordinates
							angle = Math.atan2(shiftY, shiftX);	
								
							r = Math.pow(r, 1.5) / 22;
							
							transformedX = (r * Math.cos(angle)) + imageWidth / 2; 
							transformedY = (r * Math.sin(angle)) + imageHeight / 2;
							
							transformedX = (
								transformedX > imageWidth - 1) ? imageWidth - 1 : transformedX;
							transformedX = (
								transformedX < 0) ? 0 : transformedX;
							transformedY = (
								transformedY > imageHeight - 1) ? imageHeight - 1 : transformedY;
							transformedY = (
								transformedY < 0) ? 0 : transformedY;
		                    
		                    pixel = image.getRGB(
								(int) transformedX, (int) transformedY);  
	
							image2.setRGB(x, y, pixel);
					}
				}
				ColorModel cm = image2.getColorModel();
				WritableRaster raster = image2.copyData(null); 
				image = new BufferedImage(cm, raster, false, null);
				
				imageLabel.setIcon(new ImageIcon(image2));
			}
		}
	}
	
	private static class GaussianBlur implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			if (imageLabel.getIcon() != null) {
				Color color = new Color(0, 0, 0);
				double[][] weight = new double [5][5];
				double sum = 0, sumRed = 0, sumGreen = 0, sumBlue = 0, gaussian;
				int newX = 0, newY = 0;

				for (int x = 0; x < 5; x++) {
					for (int y = 0; y < 5; y++) { 
						//Gaussian equation for blur
						gaussian = Math.pow(1 / (
							Math.sqrt((2 * Math.PI) * Math.pow(5, 2))), 
							-(Math.pow(x, 2) + Math.pow(y, 2)) 
							/ (2 * Math.pow(5, 2)));
						sum += gaussian; 
						weight[x][y] = gaussian;
					}
				}
				
				for (int x = 0; x < 5; x++) {
					for (int y = 0; y < 5; y++) {
					weight[x][y] /= sum;
					}
				}
				
				for (int x = 0; x < imageWidth; x++) {
					for (int y = 0; y < imageHeight; y++) {
						
							for (int x1 = 0; x1 < 5; x1++) {
								for (int y1 = 0; y1 < 5; y1++) {
									newX = (x1 < 3) ? x - x1 : x + (x1 - 3); 
									newY = (y1 < 3) ? y - y1 : y + (y1 - 3);
						
									//Ensures that the new pixel is within the bounds 
									// of the image, modifies the pixel if not.
									newX = (
										newX > imageWidth - 1) ? imageWidth - 1 : newX; 
									newX = (newX < 0) ? 0 : newX;
									newY = (
										newY > imageHeight - 1) ? imageHeight - 1 : newY;
									newY = (newY  < 0) ? 0 : newY;
									
									color = new Color(image.getRGB(newX, newY));
									
									sumRed += weight[x1][y1] * color.getRed();
									sumGreen += weight[x1][y1] * color.getGreen(); 
									sumBlue += weight[x1][y1] * color.getBlue();	 
								}
							}
							
						image.setRGB(x, y, new Color(
							(int)sumRed, (int)sumGreen,(int)sumBlue).getRGB());
						sumRed = 0;
						sumGreen = 0;
						sumBlue = 0;
					}
				}
				imageLabel.setIcon(new ImageIcon(image));
			}
		}	
	}			
								
	private static class Save implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			if (imageLabel.getIcon() != null) {	
				if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { 
					String extension, path;
					String newFile = fileChooser.getSelectedFile().getName(); 
					extension = (newFile.indexOf(".") == -1 || 
					newFile.charAt(newFile.length() - 1) == '.') ?  
							    fileName.substring(fileName.indexOf(".") + 1) :
							    newFile.substring(newFile.indexOf(".") + 1);
				
					if (newFile.indexOf(".") == -1) {
						path = fileChooser.getSelectedFile().getAbsolutePath() + 
						'.' + extension;
					} else if (newFile.charAt(newFile.length() - 1) == '.') { 
						path = fileChooser.getSelectedFile().getAbsolutePath() 
						+ extension;
					} else {
						path = fileChooser.getSelectedFile().getAbsolutePath();
					}
				
					try {
						ImageIO.write(image, extension, new File(path));
					} catch (IOException e) {
						System.out.println("Error: " + e);
					}
				}
			}	
		}
	}
	
	private static class Restore implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			if (imageLabel.getIcon() != null) {	
				try {
					image = ImageIO.read(new File(fileNameInit));
				} catch (IOException e) {
					System.out.println("Error: " + e);
				}
				
				imageLabel.setIcon(new ImageIcon(image));
			}
		}
	}
	
	private static class Flip implements ActionListener { 
	
		public void actionPerformed(ActionEvent event) {
				
			if (imageLabel.getIcon() != null) {

				for (int x = 0; x < imageWidth; x++) {	
					for (int y = 0; y < imageHeight; y++) {
						int pixel = image.getRGB(x, y);
						if (event.getActionCommand().equals("HFlip")) {
							image2.setRGB((imageWidth - 1) - x, y, pixel);
						} else {
							image2.setRGB(x, ((imageHeight - 1) - y), pixel);
						}
					}
				}
					
				ColorModel cm = image2.getColorModel();
				WritableRaster raster = image2.copyData(null);
				image = new BufferedImage(cm, raster, false, null);
				imageLabel.setIcon(new ImageIcon(image2));
			}
		}
	}
	
	private static class Filters implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			if (imageLabel.getIcon() != null) {	
				int pixel, r, g, b;

				for (int x = 0; x < imageWidth; x++) {
					for (int y = 0; y < imageHeight; y++) {
						pixel = image.getRGB(x, y);
						int[] rgb = {(pixel>>16)&0xff, (pixel>>8)&0xff, pixel&0xff};
						
						if (event.getActionCommand().equals("Sepia")) {
							//Formula for the sepia effect
							r = (int) (0.393 * rgb[0] + 0.769 * rgb[1] + 0.189 * rgb[2]);
							g = (int) (0.349 * rgb[0] + 0.686 * rgb[1] + 0.168 * rgb[2]); 
							b = (int) (0.272 * rgb[0] + 0.534 * rgb[1] + 0.131 * rgb[2]);
							rgb[0] = (r > 255) ? 255 : r;
							rgb[1] = (g > 255) ? 255 : g;
							rgb[2] = (b > 255) ? 255 : b;
							pixel = (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
						} else if (event.getActionCommand().equals("Invert")) {
							//Invert
							pixel = ((255 - pixel>>16&0xff) << 16) |
							 ((255 - pixel>>8&0xff) << 8) | 255 - pixel&0xff;
						} else {
							//Greyscale
							pixel = (((pixel >> 16)&0xff) + 
							((pixel >> 8)&0xff) + (pixel&0xff)) / 3;
							pixel = (pixel << 16) | (pixel<< 8) | pixel;
							
						}
							
						image.setRGB(x, y, pixel);
					}
				}
				
				imageLabel.setIcon(new ImageIcon(image));
			}
		}
	}
	
	private static class OpenImage implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				try {
					image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
					image = ImageIO.read(new File(
						fileChooser.getSelectedFile().getAbsolutePath().replace('\\', '/')));
					fileNameInit = fileChooser.getSelectedFile().getAbsolutePath().replace('\\', '/');
					fileName = fileChooser.getSelectedFile().getName();
					imageHeight = image.getHeight();
					imageWidth = image.getWidth();
					image2 = new BufferedImage(imageWidth, 
					 imageHeight, BufferedImage.TYPE_INT_RGB);		
					imageLabel.setIcon(new ImageIcon(image));
					} catch (IOException e) {
						System.out.println("Error: " + e);
					}
			}
		}
	}
	
	private static class Exit implements ActionListener {
		//Exits program
		public void actionPerformed(ActionEvent event) {
			System.exit(0); 
		}
	}
}
	

