package inlupp2a;

/**
 * @author ellendahlgren
 *
 */
import java.awt.*;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.*;
import javax.swing.event.*;
import static javax.swing.JOptionPane.*;


public class MapFrame extends JFrame {

	private JMenu archive;
	private MapArea ma;
	private JScrollPane scroll;
	private JFileChooser jfc = new JFileChooser(".");;
	private JButton newButton;
	private JRadioButton nameRadio, describedRadio;
	
	private JTextField searchField;

	private boolean changed = false;
	private Place createdPlace;

	private MouseList mouseList = new MouseList();

	private DefaultListModel<String> categoryListModel = new DefaultListModel();
	private JList<String> list = new JList(categoryListModel);
	private Map<String, Set<Place>> categoryPlaceList = new HashMap<>();
	private Map<Position, Place> basicPlaceList = new HashMap<>();
	private Map<String, Set<Place>> namePlaceList = new TreeMap<String, Set<Place>>();
	private ArrayList<Place> markingStatusSet = new ArrayList<Place>();

	public MapFrame() {
		super("Map");

		JPanel north = createNorth();
		add(north, BorderLayout.NORTH);

		JPanel east = createEast();
		add(east, BorderLayout.EAST);
		
		createMenuBar();
		FileFilter ff = new FileNameExtensionFilter("Bilder", "jpg", "gif", "png");
		jfc.setFileFilter(ff);

		addWindowListener(new EndListener());
		setMinimumSize(new Dimension(700, 200));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		archive.doClick();
	}

	private void createMenuBar() {
		archive = new JMenu("Archive");
		JMenuBar menubar = new JMenuBar();

		setJMenuBar(menubar);
		menubar.add(archive);

		JMenuItem newMenuItem = new JMenuItem("New Map");
		archive.add(newMenuItem);
		newMenuItem.addActionListener(new NewItemList());

		JMenuItem loadMenuItem = new JMenuItem("Load Places");
		archive.add(loadMenuItem);
		loadMenuItem.addActionListener(new LoadItemList());

		JMenuItem saveMenuItem = new JMenuItem("Save");
		archive.add(saveMenuItem);
		saveMenuItem.addActionListener(new SaveItemList());

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		archive.add(exitMenuItem);
		exitMenuItem.addActionListener(new ExitItemList());
	}

	private JPanel createNorth() {
		JPanel north = new JPanel();
		newButton = new JButton("New");
		north.add(newButton);
		newButton.setEnabled(false);
		newButton.addActionListener(new NewButtonList());

		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
		nameRadio = new JRadioButton("Namn", true);
		radioPanel.add(nameRadio);
		describedRadio = new JRadioButton("Described");
		radioPanel.add(describedRadio);
		ButtonGroup bg = new ButtonGroup();
		bg.add(nameRadio);
		bg.add(describedRadio);

		north.add(radioPanel);

		searchField = new JTextField(8);
		north.add(searchField);

		JButton searchButton = new JButton("Search");
		north.add(searchButton);
		searchButton.addActionListener(new SearchListener());
		JButton hideButton = new JButton("Hide");
		north.add(hideButton);
		hideButton.addActionListener(new HideListener());
		JButton removeButton = new JButton("Remove");
		north.add(removeButton);
		removeButton.addActionListener(new RemoveListener());
		JButton coordButton = new JButton("Coordinates");
		north.add(coordButton);
		coordButton.addActionListener(new CoordListener());
		return north;
	}

	private JPanel createEast() {
		JPanel east = new JPanel();
		east.setLayout(new BorderLayout());

		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));

		JLabel category = new JLabel("Categories");
		eastPanel.add(category);

		categoryListModel.addElement("Bus");
		categoryListModel.addElement("Underground");
		categoryListModel.addElement("Train");

		JScrollPane scrollPane = new JScrollPane(list);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		eastPanel.add(scrollPane);
		list.addListSelectionListener(new listListener());

		JButton hideCategoryButton = new JButton("Hide category");
		eastPanel.add(hideCategoryButton);
		hideCategoryButton.addActionListener(new CategoryListener());
		east.add(eastPanel, BorderLayout.NORTH);
		return east;
	}

	protected class listListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (!(e.getValueIsAdjusting())) {

				try {
					if (categoryPlaceList.isEmpty()) {
						return;
					}
					String category = list.getSelectedValue();
					Set<Place> categoryPlaces = categoryPlaceList.get(category);
					categoryPlaces.forEach((place) -> place.setVisible(true));

				} catch (NullPointerException err) {
					return;
				}
			}
		}
	}

	protected class CategoryListener implements ActionListener {
		public void actionPerformed(ActionEvent ave) {

			try {
				if (categoryPlaceList.isEmpty()) {
					return;
				}
				String category = list.getSelectedValue();
				Set<Place> categoryPlaces = categoryPlaceList.get(category);
				if (category != null) {
					categoryPlaces.forEach((place) -> place.setVisible(false));
				}
//				for(Place p : markingStatusSet){
//					p.setMarkedFalse();
//				}
//				repaint();
	//			markingStatusSet.clear();
				list.clearSelection();
			} catch (NullPointerException err) {
				return;
			}
		}
	}

	protected class NewItemList implements ActionListener {
		public void actionPerformed(ActionEvent ave) {
			list.clearSelection();
			
			if (changed) {
				int reply = JOptionPane.showConfirmDialog(MapFrame.this, "Osparade ändringar, skapan ny ändå?");
				if (reply != JOptionPane.OK_OPTION) {
					return;
				} 
			}
			
			int answer = jfc.showOpenDialog(MapFrame.this);
			if (answer != JFileChooser.APPROVE_OPTION) {
				return;
			}else{
				resetForNew();
			}

		    File fileNewMap = jfc.getSelectedFile();
			String fileNameNewMap = fileNewMap.getAbsolutePath();
			ma = new MapArea(fileNameNewMap );
			ma.setLayout(null);
			scroll = new JScrollPane(ma);
			add(scroll, BorderLayout.CENTER);
		
			newButton.setEnabled(true);
			validate();
			repaint();
		}

		private void prepareForNew() {
			ma.removeAll();		
			remove(scroll);
			ma = null;	
			scroll = null;
			validate();
			repaint();
		}

		private void resetForNew() {
			basicPlaceList.clear();
			categoryPlaceList.clear();
			namePlaceList.clear();
			markingStatusSet.clear();
			createdPlace = null;
			changed = false;
			if ((ma != null && scroll != null)) {
				prepareForNew();
			}
		}
	}

	protected class LoadItemList implements ActionListener {
		public void actionPerformed(ActionEvent ave) {

			if (changed) {
				int reply = JOptionPane.showConfirmDialog(MapFrame.this, "Osparade ändringar, ladda in en ny ändå?");
				if (reply != JOptionPane.OK_OPTION) {
					return;
				} else {
					clearForNewPlaces();
				}
			}
			JFileChooser jlfc = new JFileChooser(".");
			FileFilter ffText = new FileNameExtensionFilter("Text", "txt");
			jlfc.setFileFilter(ffText);

			try {

				int answer = jlfc.showDialog(MapFrame.this,"ladda in");
				if (answer != JFileChooser.APPROVE_OPTION) {
					return;
				}
				
				File file = jlfc.getSelectedFile();
				String fileName = file.getAbsolutePath();
				FileReader infil = new FileReader(fileName);
				BufferedReader br = new BufferedReader(infil);
				String line;

				while ((line = br.readLine()) != null) {
					String[] tokens = line.split(",");
					String typeOfPlace = tokens[0];
					String category = tokens[1];
					int xCoord = Integer.parseInt(tokens[2]);
					int yCoord = Integer.parseInt(tokens[3]);
					Position pos = new Position(xCoord, yCoord);
					String name = tokens[4];
					if (typeOfPlace.equals("Described")) {
						String description = tokens[5];
						createdPlace = new DescribedPlace(name, pos, category, description);

					} else {
						createdPlace = new NamedPlace(name, pos, category);
					}
					mouseList.addPlaceListeners(createdPlace);
					mouseList.adToLists(createdPlace);
					ma.add(createdPlace);
					ma.validate();
					ma.repaint();
				}

				infil.close();
				br.close();
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(MapFrame.this, "Fel i filen");
			} catch (IndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(MapFrame.this, "Fel i filen");
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(MapFrame.this, "Kan inte Öppna");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(MapFrame.this, "Fel");
			}
		}

		private void clearForNewPlaces() {
			basicPlaceList.clear();
			categoryPlaceList.clear();
			namePlaceList.clear();
			markingStatusSet.clear();
			if ((ma != null)) {
				ma.removeAll();
				ma.repaint();
			}
			createdPlace = null;
			changed = false;
		}
	}

	protected class SaveItemList implements ActionListener {
		public void actionPerformed(ActionEvent ave) {

			JFileChooser jofc = new JFileChooser(".");
			FileFilter ffText = new FileNameExtensionFilter("Text", "txt");
			jofc.setFileFilter(ffText);
			try {
				int answer = jofc.showSaveDialog(MapFrame.this);
				if (answer != JFileChooser.APPROVE_OPTION) {
					return;
				}
				
				File file = jofc.getSelectedFile();
				String fileName = file.getAbsolutePath();
				FileWriter outFile = new FileWriter(fileName + ".txt");
				PrintWriter out = new PrintWriter(outFile);
				basicPlaceList.forEach((k, v) -> out.println(v.toString()));
				changed = false;

				out.close();
				outFile.close();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(MapFrame.this, "Kan inte öppna");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(MapFrame.this, "Fel");
			}
		}
	}

	protected class ExitItemList implements ActionListener {
		public void actionPerformed(ActionEvent ave) {
			if (changed) {
				int svar = JOptionPane.showConfirmDialog(MapFrame.this, "Osparade ändringar, avsluta ändå?");
				if (svar == JOptionPane.OK_OPTION) {
					System.exit(0);
				} else {
					return;
				}

			} else {
				System.exit(0);
			}
		}
	}

	class NewButtonList implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ma.addMouseListener(mouseList);
			newButton.setEnabled(false);
			ma.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
	}

	class HideListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!(markingStatusSet.isEmpty())) {
				for(Place p : markingStatusSet){
					p.setVisible(false);
					p.setMarkedFalse();
				}
				repaint();
				markingStatusSet.clear();
			}
		}
	}

	class RemoveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (!(markingStatusSet.isEmpty())) {

				for(Place p : markingStatusSet){
					p.setVisible(false);
					namePlaceList.get(p.getName()).remove(p);
				    basicPlaceList.remove(p.getPosition(), p);
			        categoryPlaceList.get(p.getCategory()).remove(p);
				}

				markingStatusSet.clear();
				changed = true;
			}
		}
	}

	class SearchListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (basicPlaceList.isEmpty()) {
				return;
			}
			if (!(markingStatusSet.isEmpty())) { 
				for(Place p : markingStatusSet){
					p.setMarkedFalse();
				}
				repaint();
				markingStatusSet.clear();
			}
			
			String name = searchField.getText();
			Set<Place> correctPlaces = namePlaceList.get(name);
			

			if (correctPlaces != null) {
				correctPlaces.forEach((place) -> place.setMarkedTrue());
				correctPlaces.forEach((place) -> markingStatusSet.add(place));
				markingStatusSet.forEach((place) -> place.setVisible(true));
				repaint();						
				for(Place p : markingStatusSet){
					p.setVisible(true);
		
				}
			}
		}
	}

	class CoordListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				CoordForm cf = new CoordForm();
				int svar = JOptionPane.showConfirmDialog(MapFrame.this, cf, "Input coordinates: ",
						JOptionPane.OK_CANCEL_OPTION);
				if (svar != JOptionPane.OK_OPTION) {
					return;
				}

				int xCoord = cf.getxCoord();
				int yCoord = cf.getyCoord();

				Position searchedPosition = new Position(xCoord, yCoord);
				if (basicPlaceList.containsKey(searchedPosition)) {
					if (markingStatusSet != null) {
						for(Place p : markingStatusSet){
							p.setMarkedFalse();
						}
						repaint();
						markingStatusSet.clear();
					}

					Place foundPlace = basicPlaceList.get(searchedPosition);
					foundPlace.setMarkedTrue();
					markingStatusSet.add(foundPlace);
					foundPlace.setVisible(true);
					repaint();
				} else {
					showMessageDialog(MapFrame.this, "Ingen plats på dessa coordinater", "Meddelande",
							INFORMATION_MESSAGE);
				}

			} catch (NumberFormatException error) {
				showMessageDialog(MapFrame.this, "Fel inmatning!", "Fel", ERROR_MESSAGE);
			}
		}
	}

	class MouseListRight extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent mev) {

			if (mev.getButton() == MouseEvent.BUTTON3) {

				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				Component rigidArea = Box.createRigidArea(new Dimension(1, 20));
				panel.add(rigidArea);

				JLabel info;
				JLabel description;
				Place p = (Place) mev.getSource();

				if (p instanceof DescribedPlace) {

					DescribedPlace dp = (DescribedPlace) p;
					info = new JLabel("Name: " + p.getName() + "{" + p.getPosition() + "}");
					description = new JLabel("Description: " + dp.getDescription());
					panel.add(info);
					panel.add(description);

				} else {
					info = new JLabel(p.getName() + "{" + p.getPosition() + "}");
					panel.add(info);
				}

				JOptionPane.showMessageDialog(p, panel, "Platsinfo: ", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	class MouseListLeft extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent mev) {

			if (mev.getButton() == MouseEvent.BUTTON1) {

				Place p = (Place) mev.getSource();
				if (p.isMarked()) {
					p.setMarkedFalse();
				} else {
					p.setMarkedTrue();
				}

				boolean marking = p.isMarked();
				if (marking) {
					markingStatusSet.add(p);
				} else { //
					markingStatusSet.remove(p);
				}
				repaint();
			}
		}
	}

	class MouseList extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent mev) {

			int x = mev.getX();
			int y = mev.getY();
			Position pos = new Position(x, y);
			if (basicPlaceList.containsKey(pos)) {
				showMessageDialog(MapFrame.this, "Platsen är upptagen!", "Fel", ERROR_MESSAGE);
				returnMethod();
				return;
			}

			if (describedRadio.isSelected()) {
				try {

					DescriptionForm d = new DescriptionForm();
					int svar = JOptionPane.showConfirmDialog(MapFrame.this, d, "Beskriven plats",
							JOptionPane.OK_CANCEL_OPTION);
					if (svar != JOptionPane.OK_OPTION) {
						returnMethod();
						return;
					}
					if (d.getName() == null || d.getDescription() == null || (d.getName().trim().length() == 0)) {
						throw new Exception();
					}

					String name = d.getName();
					String category = setCategory();
					String description = d.getDescription();
					createdPlace = new DescribedPlace(name, pos, category, description);

				} catch (NumberFormatException e) {
					errorMessage();
				} catch (Exception e) {
					errorMessage();
					return;
				}
			} else {
				try {
					String category = setCategory();
					String name = JOptionPane.showInputDialog(null, "Namngiven plats");
					if (name == null) {
						returnMethod();
						return;
					}
					if ((name.trim().length() == 0)) {
						throw new Exception();
					}
					createdPlace = new NamedPlace(name, pos, category);
				} catch (Exception e) {
					errorMessage();
					return;
				}
			}
			changed = true;
			addPlaceListeners(createdPlace);
			adToLists(createdPlace);
			ma.add(createdPlace);
			ma.validate();
			ma.repaint();
			returnMethod();
		}

		private String setCategory() {
			String category = list.getSelectedValue();
			if (category == null || category.trim().isEmpty()) {
				category = "None";
			}
			return category;
		}

		public void adToLists(Place createdPlace) {

			Position pos = createdPlace.getPosition();
			String category = createdPlace.getCategory();
			String name = createdPlace.getName();
			basicPlaceList.put(pos, createdPlace);

			Set<Place> namePlaces = namePlaceList.get(name);
			if (namePlaces == null) {
				namePlaces = new HashSet<Place>();
				namePlaceList.put(name, namePlaces);
			}
			namePlaces.add(createdPlace);

			Set<Place> categoryPlaces = categoryPlaceList.get(category);
			if (categoryPlaces == null) {
				categoryPlaces = new HashSet<Place>();
				categoryPlaceList.put(category, categoryPlaces);
			}
			categoryPlaces.add(createdPlace);
		}

		public void addPlaceListeners(Place createdPlace) {
			createdPlace.addMouseListener(new MouseListLeft());
			createdPlace.addMouseListener(new MouseListRight());
		}

		private void returnMethod() {
			ma.removeMouseListener(mouseList);
			newButton.setEnabled(true);
			list.clearSelection();
			ma.setCursor(Cursor.getDefaultCursor());
		}
	}

	private void errorMessage() {
		showMessageDialog(MapFrame.this, "Fel inmatning!", "Fel", ERROR_MESSAGE);
	}

	class EndListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent ave) {

			if (changed) {
				int svar = JOptionPane.showConfirmDialog(MapFrame.this, "Osparade ändringar, avsluta ändå?");
				if (svar == JOptionPane.OK_OPTION) {
					System.exit(0);
				} else {
					return;
				}

			} else {
				System.exit(0);
			}
		}
	}
}
