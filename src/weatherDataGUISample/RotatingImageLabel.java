/*
 * Amir Granot,
 * H.I.T. 2015 Summer
 * Programming within the Internet environment.
 */
package weatherDataGUISample;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class RotatingImageLabel extends JLabel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5473015080329751347L;
	private Image img;
	
	public Image getImg() {
		return img;
	}

	@Override
	public Dimension getPreferredSize()
	{
		if(getIcon()!=null)
			return new Dimension(getIcon().getIconWidth(),getIcon().getIconHeight());
		else
			return new Dimension(0,0);
	}
	
	public void setImg(Image img) {
		this.img = img;
		this.setIcon(new ImageIcon(img));
	}
	
	public void rotateImage(Double degrees,ImageObserver o)
	{
		if(img == null)
			return;
		
		ImageIcon icon = new ImageIcon(this.img);
		BufferedImage blankCanvas = new BufferedImage(icon.getIconWidth(),icon.getIconHeight(),BufferedImage.TYPE_4BYTE_ABGR);	
		Graphics2D g2 = (Graphics2D) blankCanvas.getGraphics();
		g2.rotate(Math.toRadians(degrees), icon.getIconWidth() / 2, icon.getIconHeight()/2);
		g2.drawImage(this.img, 0, 0, o);
		this.setIcon(new ImageIcon(blankCanvas));
	}
}
