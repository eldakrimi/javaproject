package inlupp2a;

/**
 * @author ellendahlgren
 *
 */
public class NamedPlace extends Place {

	public NamedPlace(String name, Position pos, String category) {
		super(name, pos, category);
	}
	
	@Override
	public String toString() {
		return String.format("Named,"+super.toString());
	}

}
