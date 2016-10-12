package aQute.implementation_selection.html.provider;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import aQute.implementation_selection.api.UserInterface;
import osgi.enroute.http.capabilities.RequireHttpImplementation;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@Component(property = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN
		+ "=/userinterface/*")
@RequireHttpImplementation
@RequireWebServerExtender
public class HTMLUserInterface extends HttpServlet
		implements UserInterface, Servlet {
	private static final long								serialVersionUID	= 1L;

	private final ConcurrentHashMap<PrintWriter, Semaphore>	outputs				= new ConcurrentHashMap<>();

	@Reference(target = "(launcher.arguments=-mode:server)")
	Object													launcher;

	@Activate
	void activate() {
		System.out.println("Goto http://localhost:"
				+ System.getProperty("org.osgi.service.http.port")
				+ "/implementation-selection/index.html");
	}

	@Deactivate
	void deactivate() {
		outputs.values().forEach(s -> s.release());
	}

	@Override
	public void doGet(HttpServletRequest rq, HttpServletResponse response)
			throws IOException {

		response.setContentType("text/event-stream");

		// encoding must be set to UTF-8
		response.setCharacterEncoding("UTF-8");

		Semaphore s = new Semaphore(0);
		outputs.put(response.getWriter(), s);
		try {
			while ( !s.tryAcquire(5, TimeUnit.SECONDS) )
				response.getWriter().println();
		} catch (InterruptedException e) {
			// ignore, just closes the connection
		}
	}

	@Override
	public void print(String s) {
		for (Map.Entry<PrintWriter, Semaphore> e : outputs.entrySet()) {
			PrintWriter w = e.getKey();
			try {
				String[] splitted = s.split("\\s*(\r?\n)+\\s*");
				for (String split : splitted)
					w.write("data: " + split + "\n\n");

				w.flush();
			} catch (Exception ee) {
				e.getValue().release();
			}
		}
	}

}
