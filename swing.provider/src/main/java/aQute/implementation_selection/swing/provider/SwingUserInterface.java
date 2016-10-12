package aQute.implementation_selection.swing.provider;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import aQute.implementation_selection.api.UserInterface;

@Component(immediate=true)
public class SwingUserInterface implements UserInterface {
	@Reference(target=
			"(|(&(launcher.arguments=*)(!(launcher.arguments=-mode:*)))(launcher.arguments=-mode:swing))")
	Object launcher;
	private JFrame frame;
	private JTextArea text;
	
	@Activate
	void activate() {
		this.frame = new JFrame("TextDemo");
		frame.setVisible(true);
		this.text = new JTextArea(10,20);
		this.frame.add( this.text );
		this.frame.pack();
	}

	@Deactivate
	void deactivate() {
		frame.dispose();
	}
	
	@Override
	public void print(String s) {
		text.append(s+"\n");
	}


}
