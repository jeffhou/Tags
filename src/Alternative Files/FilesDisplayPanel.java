import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

	public class FilesDisplayPanel extends JPanel{
		//private String playerName;
		private final Font FileFont = new Font("Geneva", Font.PLAIN, 9);
		private JPanel filePanel;
		private ExpandableList myFileList;
		private boolean updateThroughFileIdentifiers=true;
		private HashMap<String,HashSet<String>> tagged;
		private String selectedFile;
		CheckPanel checkPanel;
		TextField textField;
		public FilesDisplayPanel(CheckPanel checkBoxPanel, TextField in_textField) throws Exception {
			textField=in_textField;
			checkPanel=checkBoxPanel;
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
							customTags(file);
						}
					});
			JScrollPane scrollpane =new JScrollPane(myFileList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollpane.setPreferredSize(new Dimension(330,560));
			filePanel.add(scrollpane);
			setLayout(new BorderLayout(30,30));
			add(filePanel, BorderLayout.CENTER);
			update();
		}
		public void customTags(String file){
			checkPanel.customTags(file,tagged.get(file));
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
					add(i);
				}
			}else{
				File folder =new File("C:\\Users\\Jeff Hou\\Desktop\\Programming\\Tags\\SampleFolder");
				File[] listOfFiles = folder.listFiles(); 
				for(File file:listOfFiles){
					add(file.getName());
				}
			}
		}
		public void showError(String file, String error){
			JOptionPane.showMessageDialog(this,file + ": " + error + "!!!",error,JOptionPane.ERROR_MESSAGE);
			textField.clear();
		}
		public void setReady()  {
			myFileList.clear();  // remove files from Panel/list
			paintImmediately(getVisibleRect());
		}
		public void add(String file)  {
			myFileList.add(file);
			myFileList.paintImmediately(myFileList.getVisibleRect());
			textField.clear();      //clear the tagsTextField text
		}
	}