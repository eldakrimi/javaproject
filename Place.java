package inlupp2a;

/**
 * @author ellendahlgren
 *
 */
import javax.swing.*;
import java.awt.*;

abstract public class Place extends JComponent {
	
	private String name;
	private Position pos;
	private Color color;
	private String category;
	private boolean marked;
	
	public Place(String name, Position pos, String category){
		
		this.name = name;
		this.pos = pos;
		this.category = category;
		marked = false;
			
		setBounds((pos.getXcord()-25),(pos.getYcord()-50),50,50);	
		setPreferredSize(new Dimension(50,50));
	
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		color = this.getColor(category);
		
		int[] xes = {0,25,50};
		int[] yes = {0, 50 ,0};
		if(marked == false){
			g.setColor(color);
			g.fillPolygon(xes, yes, 3);
			
		}else{	
			int[] bxes = {8,25,42};
			int[] byes = {6, 42 ,6};
			g.setColor(Color.YELLOW);
			g.fillPolygon(xes, yes, 3);
			g.setColor(color);
			g.fillPolygon(bxes, byes, 3);			
		}		
	}
	
	public String getName(){
		return name;
	}
	public Position getPosition(){
		return pos;
	}
	public String getCategory(){
		return category;
	}
	public boolean isMarked(){
		return marked;
	}
	public void setMarkedFalse(){
		marked =false;
	}
	public void setMarkedTrue(){
		marked =true;
	}

	public Color getColor(String category){
		if (category.equals("Bus")){
			return Color.RED;
		}
		else if(category.equals("Underground")){
			return Color.BLUE;
		}
		else if(category.equals("Train")){
			return Color.GREEN;
		}
		else{
			return Color.BLACK;
		}			
	}
	
	public String toString() {
		return String.format(getCategory()+","+getPosition().getXcord()+","+getPosition().getYcord()+","+getName());
	}	

}
