package nz.ac.vuw.engr301.group9mcs.controller;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import org.eclipse.jdt.annotation.Nullable;
import nz.ac.vuw.engr301.group9mcs.commons.PreconditionViolationException;
import nz.ac.vuw.engr301.group9mcs.view.SelectFileView;
import nz.ac.vuw.engr301.group9mcs.view.ViewObservable;

/**
 * Perspective that holds the Panels for the Selecting a Launch Site.
 * 
 * @author Bryony
 *
 */
public class SelectSitePerspective extends Observable implements Perspective, Observer {

	/**
	 * The Panel displayed on the screen that holds all other panels.
	 */
	private JPanel panel;
	/**
	 * The filename from SelectFileView.
	 */
	private String filename;
	
	/**
	 * Create the Perspective and construct the Panel.
	 */
	public SelectSitePerspective() {
		this.filename = "";
		this.panel = new JPanel(new BorderLayout());
	}
	
	@Override
	public JPanel enable(MenuController menu) {
		start();
		return this.panel;
	}
	
	/**
	 * Start the Perspective at the File Selection.
	 */
	private void start() {
		this.panel.add(new SelectFileView(new ViewObservable(this)), BorderLayout.CENTER);
	}
	
	/**
	 * Continue the Perspective in the Select Information.
	 */
	private void second() {
		this.panel.removeAll();
		// this.panel.add(SelectSiteView(....
	}
	
	/**
	 * Finish the Perspective in the Go No Go View.
	 */
	private void third() {
		this.panel.removeAll();
		// this.panel.add(GoNoGoView(....
	}

	@Override
	public void init(MenuController menu, Observer o) {
		this.addObserver(o);
	}

	@Override
	public String name() {
		return "select";
	}

	@Override
	public void update(@Nullable Observable o, @Nullable Object arg) {
		if(arg != null) {
			if(arg instanceof String) {
				this.filename = (String) arg;
				second();
				return;
			}
		}
		throw new PreconditionViolationException("Unregonized command sent to SelectSitePerspective");
	}
	
}
