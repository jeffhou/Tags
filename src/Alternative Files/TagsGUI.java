import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
public class TagsGUI extends JFrame{
	TextField tagsTextField,fileTextField;
	private FilesDisplayPanel filesDisplayPanel;
	JComponent newContentPane;
	//JScrollPane checkBoxPanel;
	CheckPanel checkBoxPanel;
	MenuButton addTagButton,deleteTagButton,importButton;
	public TagsGUI() throws Exception{
		super( "TagFile - Version 0.1"); //set title for window
		
		Container contentPane = getContentPane(); //simply gets content pane
		contentPane.setPreferredSize(new Dimension(1000,700));//set size for content pane
		contentPane.setLayout(null);//sets no layout for absolute placement
		
		addTagButton = new MenuButton("Add Tag", "tag.png","Add Tag...",0,0,54,54);
        contentPane.add(addTagButton);
        deleteTagButton = new MenuButton("Delete Tag", "deleteTag.png","Delete Tag...",54+2,0,70,54);
        contentPane.add(deleteTagButton);
        importButton = new MenuButton("Import", "import.png","Import Files...",54+2+70+2,0,54,54);
        contentPane.add(importButton);
        
		tagsTextField = new TextField(15,"New Tag: ",false,true); //sets a TextField for the tags text field
		contentPane.add(tagsTextField); //adds tagsTextField to contentPane
		tagsTextField.setBounds(700,50+600,300,40);

		fileTextField = new TextField(20,"Enter Command: ",true,false); //sets a TextField for the file text field
		contentPane.add(fileTextField); //adds fileTextField to contentPane
		fileTextField.setBounds(300,50+600,400,40);

		filesDisplayPanel= new FilesDisplayPanel(); //make the panel for the file display (will be on right)
		filesDisplayPanel.setPreferredSize(new Dimension(400,600));//set size for filesDisplayPanel
		contentPane.add(filesDisplayPanel);
		filesDisplayPanel.setBounds(300,50+0,filesDisplayPanel.getPreferredSize().width,filesDisplayPanel.getPreferredSize().height);

		checkBoxPanel=new CheckPanel();
		checkBoxPanel.setPreferredSize(new Dimension(300,600));
		contentPane.add(checkBoxPanel);//adds checkboxpanel to contentpane
		checkBoxPanel.setBounds(300+400,50+0,checkBoxPanel.getPreferredSize().width,checkBoxPanel.getPreferredSize().height);

		this.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				save();
				((JFrame)(e.getComponent())).dispose();
			}
		});
		setUpMenuBar(); //sets up menubar
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
				myDoneButton = new JButton("DONE");
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
	class EditTagsPanel extends JPanel implements ItemListener {
		ArrayList<JCheckBox> tags;
		HashSet<String> tagsSet;
		JPanel checkPanel;
		public EditTagsPanel() throws IOException {
			//super(new BorderLayout());
			//Create the check boxes.
			tagsSet=new HashSet<String>();	
			tags = new ArrayList<JCheckBox>();
			Scanner scanner = new Scanner(new File("C:\\Users\\Jeff Hou\\Desktop\\Programming\\Tags\\SampleFolder\\tags.txt"));
			while(scanner.hasNext()){
				String new_tag=scanner.nextLine().trim();
				if(!tagsSet.contains(new_tag)){
					tags.add(new JCheckBox(new_tag));
					tagsSet.add(new_tag);
				}
			}
			for(JCheckBox c:tags){
				c.setSelected(false);
				c.addItemListener(this);
			}
			//Put the check boxes in a column in a panel
			checkPanel = new JPanel(new GridLayout(0,2));
			update(tags);
			add(checkPanel, BorderLayout.LINE_START);
			//add(pictureLabel, BorderLayout.CENTER);
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
			String changedTag=((JCheckBox)e.getSource()).getText();
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				//System.out.println("UNCHECKED BOX: "+changedTag);
				//if(filesDisplayPanel.isTagged(filesDisplayPanel.getSelectedFile(), changedTag))
				//System.out.println(changedTag+" "+filesDisplayPanel.getSelectedFile());
				filesDisplayPanel.untagFile(filesDisplayPanel.getSelectedFile(),changedTag);
				updateFrame();
			}else{
				//System.out.println("CHECKED BOX: "+((JCheckBox)e.getSource()).getText());
				//if(!filesDisplayPanel.isTagged(filesDisplayPanel.getSelectedFile(), changedTag))
				//System.out.println(changedTag+" "+filesDisplayPanel.getSelectedFile());
				filesDisplayPanel.tagFile(filesDisplayPanel.getSelectedFile(),changedTag);
				updateFrame();
			}
		}
		public boolean addTag(String tag){
			if(tagsSet.contains(tag)) return false;
			tagsSet.add(tag);
			JCheckBox c=makeNewCheckBox(tag);
			tags.add(c);
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
		public void customTags(String file, HashSet<String> hs) {
			for(JCheckBox i:tags){
				i.setSelected(hs.contains(i.getText()));
			}
		}
	}
	class FilesDisplayPanel extends JPanel{
		//private String playerName;
		private final Font FileFont = new Font("Geneva", Font.PLAIN, 9);
		private JPanel filePanel;
		private ExpandableList myFileList;
		private boolean updateThroughFileIdentifiers=true;
		private HashMap<String,HashSet<String>> tagged;
		private String selectedFile;
		
		public FilesDisplayPanel() throws Exception {
			tagged= new HashMap<String,HashSet<String>>();
			filePanel = new JPanel();
			Border etched = BorderFactory.createEtchedBorder();
			TitledBorder etchedTitle = BorderFactory.createTitledBorder(etched, "File List");
			etchedTitle.setTitleJustification(TitledBorder.RIGHT);
			filePanel.setBorder(etchedTitle);
			myFileList = new ExpandableList();
			myFileList.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							String file = e.getActionCommand();
							selectedFile=file;
							//System.out.println(selectedFile+" !!!!!!");
							newTaggedFile(selectedFile);
							checkBoxPanel.customTags(file,tagged.get(file));
						}
					});
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
			FileWriter writer = new FileWriter(new File("C:\\Users\\Jeff Hou\\Desktop\\Programming\\Tags\\SampleFolder\\identifiers.txt"));
			for(TaggedFile i: files){
				writer.write(i.toStringWithTags(tagged.get(i.toString()))+"\n");
			}
			writer.close();
			writer = new FileWriter(new File("C:\\Users\\Jeff Hou\\Desktop\\Programming\\Tags\\SampleFolder\\tags.txt"));
			for(String i: set){
				writer.write(i+"\n");
			}
			writer.close();
		}
		public void newTaggedFile(String selectedFile){
			new TaggedFile(selectedFile);
		}
		public void tagFile(String tag) {
			tagged.get(selectedFile).add(tag);
		}
		public void tagFile(String file, String tag) {
			tagged.get(file).add(tag);
		}
		public void untagFile(String file, String tag) {
			tagged.get(file).remove(tag);
		}
		public String getSelectedFile(){
			return selectedFile;
		}
		public boolean isTagged(String file, String tag){
			if(tagged.containsKey(file) && tagged.get(file).contains(tag))
				return true;
			return false;
		}
		
		public void update() throws IOException{
			if(updateThroughFileIdentifiers){
				Scanner scanner=new Scanner(new File("C:\\Users\\Jeff Hou\\Desktop\\Programming\\Tags\\SampleFolder\\identifiers.txt"));
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
				File folder =new File("C:\\Users\\Jeff Hou\\Desktop\\Programming\\Tags\\SampleFolder");
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
	}
	class CheckPanel extends JPanel{
		//private String playerName;
		private JScrollPane checkboxpanel;
		private ExpandableList myFileList;
		private EditTagsPanel insidepanel;
		public CheckPanel() throws IOException {
			insidepanel=new EditTagsPanel();
			checkboxpanel = new JScrollPane(insidepanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			Border etched = BorderFactory.createEtchedBorder();
			TitledBorder etchedTitle = BorderFactory.createTitledBorder(etched, "Tags");
			etchedTitle.setTitleJustification(TitledBorder.RIGHT);
			checkboxpanel.setBorder(etchedTitle);
			checkboxpanel.setPreferredSize(new Dimension(300,600));
			setLayout(new BorderLayout(30,30));
			add(checkboxpanel, BorderLayout.CENTER);
		}
		public void customTags(String file, HashSet<String> hs) {
			insidepanel.customTags(file,hs);

		}
		public boolean addTag(String tag){
			return insidepanel.addTag(tag);
		}
	}
}