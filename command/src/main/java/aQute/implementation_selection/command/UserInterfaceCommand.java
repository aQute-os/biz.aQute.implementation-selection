package aQute.implementation_selection.command;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.implementation_selection.api.UserInterface;
import osgi.enroute.debug.api.Debug;

@Component( service=UserInterfaceCommand.class, property={Debug.COMMAND_SCOPE+"=ui", Debug.COMMAND_FUNCTION+"=print"})
public class UserInterfaceCommand {

	@Reference
	UserInterface	ui;
	
	
	public void print( String[] s) {
		String line = Stream.of(s).collect(Collectors.joining(" "));
		ui.print(line);
	}
}
