package inlupp2a;

/**
 * @author ellendahlgren
 *
 */

import javax.swing.*;

public class Position extends JComponent {
	private int xcord;
	private int ycord;
	
	public Position(int xcord, int ycord){
		this.xcord = xcord;
		this.ycord = ycord;
	}
	
	public int getXcord(){
		return xcord;
	}
	public int getYcord(){
		return ycord;
	}
	public void setXcord(int x){
		xcord = x;
	}
	public void setYcord(int y){
		ycord = y;
	}
	
	@Override
	public int hashCode(){
		int tmp = (ycord+((xcord+1)/2));
		return xcord+(tmp*tmp);
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Position){
			Position p = (Position)other;  
			return (xcord == p.xcord && ycord == p.ycord);
					
		}else{
			return false; 
		}
	}
	public String toString() {
		return String.format(xcord+","+ycord );
	}

}
