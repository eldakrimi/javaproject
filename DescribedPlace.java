package inlupp2a;

/**
 * @author ellendahlgren
 *
 */
public class DescribedPlace extends Place {
	private String description; 
	
	public DescribedPlace(String name, Position pos, String category, String description) {
		super(name, pos, category);
		this.description = description; 
	}

public String getDescription(){
	return description;
}

	@Override
	public String toString() {
		return String.format("Described,"+super.toString()+","+description);
	}
}
