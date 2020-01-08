package inlupp2a;

import java.awt.FlowLayout;

import javax.swing.*;
/**
 * @author ellendahlgren
 *
 */
public class CoordForm extends JPanel {
	
	private JTextField xCoord = new JTextField(4);
	private JTextField yCoord = new JTextField(4);
	public CoordForm() {
		setLayout(new FlowLayout());
		JPanel firstInput = new JPanel();
		firstInput.add(new JLabel("X:"));
		firstInput.add(xCoord);
		add(firstInput);

		JPanel secondInput = new JPanel();
		secondInput.add(new JLabel("Y"));
		secondInput.add(yCoord);
		add(secondInput);
	}
	public int getxCoord () {
		return Integer.parseInt(xCoord .getText());
	}
	public int getyCoord() {
		return Integer.parseInt(yCoord.getText());
	}

}
