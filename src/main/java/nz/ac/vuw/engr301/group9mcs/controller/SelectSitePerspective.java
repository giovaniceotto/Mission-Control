package nz.ac.vuw.engr301.group9mcs.controller;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import org.eclipse.jdt.annotation.Nullable;

import nz.ac.vuw.engr301.group9mcs.commons.Condition;
import nz.ac.vuw.engr301.group9mcs.commons.Null;
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
	
	private final JPanel fileGet = new SelectFileView(this);
	private final JPanel siteMap = new JPanel();
	private final JPanel resultsShow = new JPanel();
	
	/**
	 * Create the Perspective and construct the Panel.
	 */
	public SelectSitePerspective() {
		this.panel = new JPanel(new BorderLayout());
		this.switchTo(this.fileGet);
	}
	
	@Override
	public JPanel enable(MenuController menu) {
		return this.panel;
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
		if(arg instanceof String[])
		{
			String[] args = (String[]) Null.nonNull(arg);
			Condition.PRE.positive("args.length", args.length);
			
			switch(args[0])
			{
				case "rocket imported":
					this.switchTo(this.siteMap);
					break;
				default:
					throw new PreconditionViolationException("Unregonized command sent to SelectSitePerspective");
			}
		}
	}
	
	private void switchTo(JPanel newPanel)
	{
		this.panel.removeAll();
		this.panel.add(newPanel, BorderLayout.CENTER);
	}
	
}
