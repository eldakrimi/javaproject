package inlupp2a;

/**
 * @author ellendahlgren
 *
 */
import java.awt.*;
import javax.swing.*;

public class MapArea extends JPanel{
	private ImageIcon picture;
	
	public MapArea(String filename){
		picture = new ImageIcon(filename);
		int w = picture.getIconWidth();
		int h = picture.getIconHeight();
		setPreferredSize(new Dimension(w,h));
		setMaximumSize(new Dimension(w,h));
		setMinimumSize(new Dimension(w,h));
		
		setLayout(null);
	}

	protected void paintComponent(Graphics g){	
		super.paintComponent(g);
		g.drawImage(picture.getImage(), 0, 0, this);
	}
}
