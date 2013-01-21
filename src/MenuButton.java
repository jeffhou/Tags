import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class MenuButton extends JButton{
	MenuButton(String text, String imagePath, String altText, int x, int y, int lenx, int leny){
		super(text,new ImageIcon(imagePath));
		this.setVerticalTextPosition(AbstractButton.BOTTOM);
		this.setHorizontalTextPosition(AbstractButton.CENTER);
		this.setBounds(x,y,lenx,leny);
		this.setMargin(new Insets(0,0,0,0));
		this.setToolTipText(altText);
	}
}
