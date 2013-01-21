import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
public class TagsGUI extends JFrame{
	TextField tagsTextField,fileTextField;
	private FilesDisplayPanel filesDisplayPanel;
	JComponent newContentPane;
	//JScrollPane checkBoxPanel;
	EditTagsOutsidePanel checkBoxPanel;
	TagsSearchOutsidePanel searchPanel;
	MenuButton addTagButton,deleteTagButton,importButton;
	Boolean justFilteredTags;
	public TagsGUI() throws Exception{
		super( "TagFile - Version 0.1"); //set title for window
		justFilteredTags=false;
		Container contentPane = getContentPane(); //simply gets content pane
		contentPane.setPreferredSize(new Dimension(1000,705));//set size for content pane
		contentPane.setLayout(null);//sets no layout for absolute placement

		addTagButton = new MenuButton("Add Tag", "tag.png","Add Tag...",2+ 0,0,54,54);
		addTagButton.setMnemonic('A');
		addTagButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String tag = JOptionPane.showInputDialog("New Tag:");
				try {
					checkBoxPanel.addTag(tag);
				} catch (Exception e3) {
					//showError("error reading from URL: " + address);

				}
			}
		});

	contentPane.add(addTagButton);

	deleteTagButton = new MenuButton("Delete Tag", "deleteTag.png","Currently Not Working...",2+54+2,0,70,54);
	deleteTagButton.setMnemonic('D');
	deleteTagButton.setEnabled(false);
	contentPane.add(deleteTagButton);

	importButton = new MenuButton("Import", "import.png","Currently Not Working...",2+54+2+70+2,0,54,54);
	importButton.setMnemonic('I');
	importButton.setEnabled(false);
	contentPane.add(importButton);

	tagsTextField = new TextField(15,"New Tag: ",false,true); //sets a TextField for the tags text field
	contentPane.add(tagsTextField); //adds tagsTextField to contentPane
	tagsTextField.setBounds(700,55+600,300,40);

	fileTextField = new TextField(20,"Enter Command: ",true,false); //sets a TextField for the file text field
	contentPane.add(fileTextField); //adds fileTextField to contentPane
	fileTextField.setBounds(300,55+600,400,40);

	filesDisplayPanel= new FilesDisplayPanel(); //make the panel for the file display (will be on right)
	filesDisplayPanel.setPreferredSize(new Dimension(400,600));//set size for filesDisplayPanel
	contentPane.add(filesDisplayPanel);
	filesDisplayPanel.setBounds(300,55+0,filesDisplayPanel.getPreferredSize().width,filesDisplayPanel.getPreferredSize().height);

	checkBoxPanel=new EditTagsOutsidePanel();
	checkBoxPanel.setPreferredSize(new Dimension(300,600));
	contentPane.add(checkBoxPanel);//adds scrollPane to contentpane
	checkBoxPanel.setBounds(300+400,55+0,checkBoxPanel.getPreferredSize().width,checkBoxPanel.getPreferredSize().height);

	searchPanel=new TagsSearchOutsidePanel();
	searchPanel.setPreferredSize(new Dimension(300,600));
	contentPane.add(searchPanel);
	searchPanel.setBounds(0,55+0,checkBoxPanel.getPreferredSize().width,checkBoxPanel.getPreferredSize().height);

	this.addComponentListener(new ComponentAdapter() {
		public void componentHidden(ComponentEvent e) {
			save();
			((JFrame)(e.getComponent())).dispose();
		}
	});
	//setUpMenuBar(); //sets up menubar
	pack(); //packs everything in
	setVisible(true); //sets visibility so frame can be seen
}
protected void save() {
	try {
		filesDisplayPanel.save();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
void updateFrame(){
	pack();
}
private void setUpMenuBar()  {
	JMenuBar menu = new JMenuBar();	//Set Up Menu Bar
	JMenu fileMenu = new JMenu("File");		// Game Menu   
	menu.add(fileMenu);
	JMenuItem newFile = new JMenuItem("New File");
	fileMenu.add(newFile);
	newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	newFile.addActionListener(new ActionListener(){
		public void actionPerformed( ActionEvent e) {
			System.out.println("New File Clicked!");
		}});
	fileMenu.addSeparator();
	JMenuItem quitGame = new JMenuItem("Quit");
	fileMenu.add(quitGame);
	quitGame.setMnemonic('Q');
	quitGame.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e) {
			System.exit(0);
		}});
	setJMenuBar(menu);
}
class TextField extends JPanel {
	private JTextField textField;
	private JButton myDoneButton;
	private StringBuilder myString;
	boolean tagAdmin;
	public TextField(int length,String label,boolean button,boolean tagAdmin_in) {
		tagAdmin=tagAdmin_in;
		textField = new JTextField(length);
		myString = new StringBuilder("");
		textField.addActionListener(
				new ActionListener() {
					public void actionPerformed( ActionEvent e) {
						String s= textField.getText().toUpperCase().trim();
						myString.delete(0, myString.length());
						myString.append(s);
						if(tagAdmin){
							checkBoxPanel.addTag(myString.toString());
						}else{
							filesDisplayPanel.showFile(s);
							//System.out.println(myString);
						}
						clear();
					}
				}
				);
		this.add(new JLabel(label));
		this.add(textField);
		if(button){
			myDoneButton = new JButton("DO");
			myDoneButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					System.out.println("DONE BUTTON CLICKED");
					System.out.println(myString);
				}
			});
			myDoneButton.setPreferredSize(new Dimension(60,20));
			this.add(myDoneButton);
		}
		setUnready();
	}
	public void clear() {
		textField.setText("");
	}
	public void setUnready() {
		clear();
		textField.setEditable(true);
		paintImmediately(getVisibleRect());
		TagsGUI.this.requestFocus();
	}
	public void setReady() {
		textField.setEditable(true);
		textField.requestFocus();
	}
}
class FilesDisplayPanel extends JPanel{
	//private String playerName;
	private final Font FileFont = new Font("Geneva", Font.PLAIN, 9);
	private JPanel filePanel;
	private ExpandableList myFileList;
	private boolean updateThroughFileIdentifiers=true;
	private HashMap<String,HashSet<String>> tagged;//filename,tags

	public FilesDisplayPanel() throws Exception {
		tagged= new HashMap<String,HashSet<String>>();
		filePanel = new JPanel();
		Border etched = BorderFactory.createEtchedBorder();
		TitledBorder etchedTitle = BorderFactory.createTitledBorder(etched, "File List");
		etchedTitle.setTitleJustification(TitledBorder.CENTER);
		filePanel.setBorder(etchedTitle);
		myFileList = new ExpandableList();
		myFileList.addListSelectionListener(
				new ListSelectionListener(){
					public void valueChanged(ListSelectionEvent event) {
						if (!event.getValueIsAdjusting()) {
							System.out.println("CLICKED");
							String file;
							justFilteredTags=false;
							try{
								file = myFileList.getSelectedValue().toString();
							}catch(Exception e){
								file = null;
							}
							if(file!=null) checkBoxPanel.customTags(file,tagged.get(file));
							else checkBoxPanel.customTags();
						}
					}
				}
				);
		JScrollPane scrollpane =new JScrollPane(myFileList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setPreferredSize(new Dimension(330,560));
		filePanel.add(scrollpane);
		setLayout(new BorderLayout(30,30));
		add(filePanel, BorderLayout.CENTER);
		update();
	}

	public void save() throws IOException{
		ArrayList<TaggedFile> files = new ArrayList<TaggedFile>(); 
		HashSet<String> set = new HashSet<String>();
		for(String i:tagged.keySet()){
			files.add(new TaggedFile(i));
			set.addAll(tagged.get(i));
		}
		Collections.sort(files);
		FileWriter writer = new FileWriter(new File("SampleFolder\\identifiers.txt"));
		for(TaggedFile i: files){
			writer.write(i.toStringWithTags(tagged.get(i.toString()))+"\n");
		}
		writer.close();
		writer = new FileWriter(new File("SampleFolder\\tags.txt"));
		for(String i: set){
			writer.write(i+"\n");
		}
		writer.close();
	}
	public void newTaggedFile(String fileName){
		new TaggedFile(fileName);
	}
	public void tagFile(String tag) {
		tagged.get(myFileList.getSelectedValue().toString()).add(tag);
		System.out.println("File: "+myFileList.getSelectedValue().toString());
		System.out.println("Tags: "+tag);
	}
	public void tagFile(String file, String tag) {
		tagged.get(file).add(tag);
		System.out.println("File: "+file);
		System.out.println("Tags: "+tag);
	}
	public void untagFile(String file, String tag) {
		tagged.get(file).remove(tag);
	}
	public String getSelectedFile(){
		try{
			return myFileList.getSelectedValue().toString();
		}catch(Exception e){
			return null;
		}
	}
	public boolean isTagged(String file, String tag){
		if(tagged.containsKey(file) && tagged.get(file).contains(tag))
			return true;
		return false;
	}

	public void update() throws IOException{
		System.out.println("updating filesdisplaypanel...");
		if(updateThroughFileIdentifiers){
			Scanner scanner=new Scanner(new File("SampleFolder\\identifiers.txt"));
			PriorityQueue<String> queue=new PriorityQueue<String>();
			while(scanner.hasNext()){
				String[] s=scanner.nextLine().trim().split(",,");
				int id=Integer.parseInt(s[0]);
				tagged.put(s[1]+" ("+id+")",new HashSet<String>());
				for(int i =2;i<s.length;i++){
					tagged.get(s[1]+" ("+id+")").add(s[i]);
				}
				queue.add(s[1]+" ("+id+")");
			}
			scanner.close();
			for(String i:queue){
				showFile(i);
			}
		}else{
			File folder =new File("SampleFolder");
			File[] listOfFiles = folder.listFiles(); 
			for(File file:listOfFiles){
				showFile(file.getName());
			}
		}
	}
	public void showError(String file, String error){
		JOptionPane.showMessageDialog(this,file + ": " + error + "!!!",error,JOptionPane.ERROR_MESSAGE);
		tagsTextField.clear();
	}
	public void setReady()  {
		myFileList.clear();  // remove files from Panel/list
		paintImmediately(getVisibleRect());
	}
	public void showFile(String file)  {
		myFileList.add(file);
		myFileList.paintImmediately(myFileList.getVisibleRect());
		tagsTextField.clear();      //clear the tagsTextField text
	}

	public void displayFiles(ArrayList<String> selectedTags) throws FileNotFoundException {
		ArrayList<String> list=new ArrayList<String>();
		for(String i: tagged.keySet()){
			boolean shouldDisplay=true;
			for(String j:selectedTags){
				if(!tagged.get(i).contains(j))
					shouldDisplay=false;
			}
			if(shouldDisplay)
				list.add(i);
		}
		myFileList.clearSelection();
		myFileList.clear();
		Collections.sort(list);
		for(String i:list){
			showFile(i);
		}
	}
}
class EditTagsInsidePanel extends JPanel implements ItemListener {
	ArrayList<JCheckBox> tagsCheckBoxList;
	HashSet<String> tagsSet;
	JPanel checkPanel;
	public EditTagsInsidePanel() throws IOException {
		//Create the check boxes.
		tagsSet=new HashSet<String>();	
		tagsCheckBoxList = new ArrayList<JCheckBox>();
		Scanner scanner = new Scanner(new File("SampleFolder\\tags.txt"));
		while(scanner.hasNext()){//if tag is not already in list, make new CheckBox and add tag to tagsSet
			String new_tag=scanner.nextLine().trim();
			if(!tagsSet.contains(new_tag)){
				tagsCheckBoxList.add(new JCheckBox(new_tag));
				tagsSet.add(new_tag);
			}
		}
		for(JCheckBox c:tagsCheckBoxList){
			c.setSelected(false);
			c.addItemListener(this);
		}
		//Put the check boxes in a column in a panel
		checkPanel = new JPanel(new GridLayout(0,2));
		update(tagsCheckBoxList);
		add(checkPanel, BorderLayout.LINE_START);
		setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
	}
	public void update(ArrayList<JCheckBox> newTags){
		for(JCheckBox c:newTags){
			checkPanel.add(c);
		}
	}
	public void update(JCheckBox newTag){
		checkPanel.add(newTag);
	}
	/** Listens to the check boxes. */
	public void itemStateChanged(ItemEvent e) {

		if(!justFilteredTags){
			System.out.println("_+_+_+_+_+_");
			String changedTag=((JCheckBox)e.getSource()).getText();
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				//System.out.println("UNCHECKED BOX: "+changedTag);
				//if(filesDisplayPanel.isTagged(filesDisplayPanel.getSelectedFile(), changedTag))
				//System.out.println(changedTag+" "+filesDisplayPanel.getSelectedFile());
				filesDisplayPanel.untagFile(filesDisplayPanel.getSelectedFile(),changedTag);
				System.out.println("untagging files");
			}else{
				//System.out.println("CHECKED BOX: "+((JCheckBox)e.getSource()).getText());
				//if(!filesDisplayPanel.isTagged(filesDisplayPanel.getSelectedFile(), changedTag))
				//System.out.println(changedTag+" "+filesDisplayPanel.getSelectedFile());
				filesDisplayPanel.tagFile(filesDisplayPanel.getSelectedFile(),changedTag);

			}
			updateFrame();
		}else{
			System.out.println("cannot change...");
		}
	}
	public boolean addTag(String tag){
		if(tagsSet.contains(tag)) return false;
		tagsSet.add(tag);
		JCheckBox c=makeNewCheckBox(tag);
		tagsCheckBoxList.add(c);
		update(c);
		updateFrame();
		return true;
	}
	public JCheckBox makeNewCheckBox(String tag){
		JCheckBox checkBox=new JCheckBox(tag);
		checkBox.setSelected(false);
		checkBox.addItemListener(this);
		return checkBox;
	}
	public void customTags(){
		System.out.println("cleared");
		for(JCheckBox i:tagsCheckBoxList){
			i.setSelected(false);
		}
	}
	public void customTags(String file, HashSet<String> hs) {
		for(JCheckBox i:tagsCheckBoxList){
			i.setSelected(hs.contains(i.getText()));
		}
	}
}
class EditTagsOutsidePanel extends JPanel{
	//private String playerName;
	private JScrollPane scrollPane;
	private ExpandableList myFileList;
	private EditTagsInsidePanel insidepanel;
	public EditTagsOutsidePanel() throws IOException {
		insidepanel=new EditTagsInsidePanel();
		scrollPane = new JScrollPane(insidepanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		TitledBorder etchedTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Tags");
		etchedTitle.setTitleJustification(TitledBorder.RIGHT);
		scrollPane.setBorder(etchedTitle);
		scrollPane.setPreferredSize(new Dimension(300,600));
		setLayout(new BorderLayout(30,30));
		add(scrollPane, BorderLayout.CENTER);
	}
	public void customTags() {
		insidepanel.customTags();
	}
	public void customTags(String file, HashSet<String> hs) {
		insidepanel.customTags(file,hs);
	}
	public boolean addTag(String tag){
		return insidepanel.addTag(tag);
	}
}
class TagsSearchInsidePanel extends JPanel implements ItemListener {
	ArrayList<JCheckBox> tagsCheckBoxList;
	HashSet<String> tagsSet;
	JPanel checkPanel;
	ArrayList<String> selectedTags;
	public TagsSearchInsidePanel() throws IOException {
		//Create the check boxes.
		selectedTags=new ArrayList<String>();
		tagsSet=new HashSet<String>();	
		tagsCheckBoxList = new ArrayList<JCheckBox>();
		Scanner scanner = new Scanner(new File("SampleFolder\\tags.txt"));
		while(scanner.hasNext()){//if tag is not already in list, make new CheckBox and add tag to tagsSet
			String new_tag=scanner.nextLine().trim();
			if(!tagsSet.contains(new_tag)){
				tagsCheckBoxList.add(new JCheckBox(new_tag));
				tagsSet.add(new_tag);
			}
		}
		for(JCheckBox c:tagsCheckBoxList){
			c.setSelected(false);
			c.addItemListener(this);
		}
		//Put the check boxes in a column in a panel
		checkPanel = new JPanel(new GridLayout(0,2));
		update(tagsCheckBoxList);
		add(checkPanel, BorderLayout.LINE_START);
		setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
	}
	public void update(ArrayList<JCheckBox> newTags){
		for(JCheckBox c:newTags){
			checkPanel.add(c);
		}
	}
	public void update(JCheckBox newTag){
		checkPanel.add(newTag);
	}
	/** Listens to the check boxes. */
	public void itemStateChanged(ItemEvent e) {
		for(int i = 0;i<3;i++){
			String changedTag=((JCheckBox)e.getSource()).getText();
			System.out.println("Changed tag: --"+changedTag+"--");

			if (e.getStateChange() == ItemEvent.DESELECTED){
				selectedTags.remove(changedTag);
			}else{
				selectedTags.add(changedTag);
			}
			try {
				filesDisplayPanel.myFileList.clearSelection();
				filesDisplayPanel.displayFiles(selectedTags);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			updateFrame();
			justFilteredTags=true;
		}
	}
	public boolean addTag(String tag){
		if(tagsSet.contains(tag)) return false;
		tagsSet.add(tag);
		JCheckBox c=makeNewCheckBox(tag);
		tagsCheckBoxList.add(c);
		update(c);
		updateFrame();
		return true;
	}
	public JCheckBox makeNewCheckBox(String tag){
		JCheckBox checkBox=new JCheckBox(tag);
		checkBox.setSelected(false);
		checkBox.addItemListener(this);
		return checkBox;
	}
}
class TagsSearchOutsidePanel extends JPanel{
	//private String playerName;
	private JScrollPane scrollPane;
	private ExpandableList myFileList;
	private TagsSearchInsidePanel insidepanel;
	public TagsSearchOutsidePanel() throws IOException {
		insidepanel=new TagsSearchInsidePanel();
		scrollPane = new JScrollPane(insidepanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		TitledBorder etchedTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Navigation");
		etchedTitle.setTitleJustification(TitledBorder.LEFT);
		scrollPane.setBorder(etchedTitle);
		scrollPane.setPreferredSize(new Dimension(300,600));
		setLayout(new BorderLayout(30,30));
		add(scrollPane, BorderLayout.CENTER);
	}
	public boolean addTag(String tag){
		return insidepanel.addTag(tag);
	}
}

}