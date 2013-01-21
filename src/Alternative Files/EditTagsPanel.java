import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class EditTagsPanel extends JPanel implements ItemListener {
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