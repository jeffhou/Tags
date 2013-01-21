import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


public class CheckPanel extends JPanel{
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
	public boolean add(String tag){
		return insidepanel.addTag(tag);
	}
}
