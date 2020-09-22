package nz.ac.vuw.engr301.group9mcs.controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import org.eclipse.jdt.annotation.Nullable;

import nz.ac.vuw.engr301.group9mcs.commons.Null;
import nz.ac.vuw.engr301.group9mcs.commons.RocketData;
import nz.ac.vuw.engr301.group9mcs.commons.RocketDataListener;
import nz.ac.vuw.engr301.group9mcs.view.ArmedButtonPanel;
import nz.ac.vuw.engr301.group9mcs.view.RocketOutputPanel;
import nz.ac.vuw.engr301.group9mcs.view.WarningPanel;

/**
 * Perspective that holds the Panels for the View while the rocket is Armed.
 *
 * @author Bryony
 *
 */
public class ArmedPerspective extends Observable implements Perspective, Observer, RocketDataListener {

	/**
	 * The Panel displayed on the screen that holds all other panels.
	 */
	JPanel panel;

	/**
	 * WARNING PANEL : armed and dangerous, do not touch rocket -> close when rocket is fired
	 */
	private JPanel warningPanel;

	/**
	 * DISARM BUTTON : return to unarmed state
	 */
	private JPanel disarmButton;

	/**
	 * Holds the Warning Panel and Disarm Button at the top of the page.
	 */
	private JPanel topPanel;

	/**
	 * ROCKET DATA PANEL : text output from rocket?
	 */
	private RocketOutputPanel rocketDataPanel;

	/**
	 * Resources for Perspective
	 */
	private @Nullable Resources resources;

	/**
	 * Construct the Panel
	 */
	public ArmedPerspective() {

		this.panel = new JPanel(new BorderLayout());

		this.rocketDataPanel = new RocketOutputPanel();
		this.rocketDataPanel.setPreferredSize(new Dimension(400, 300));

		String[] args = {"Armed and Dangerous", "Do not touch while Armed"};
		this.warningPanel = new WarningPanel(args);
		this.warningPanel.setPreferredSize(new Dimension(300, 100));

		this.disarmButton = new ArmedButtonPanel(this, "DISARM");
		this.disarmButton.setPreferredSize(new Dimension(100, 100));

		this.topPanel = new JPanel(new BorderLayout());
		this.topPanel.add(this.warningPanel, BorderLayout.CENTER);
		this.topPanel.add(this.disarmButton, BorderLayout.EAST);
		this.topPanel.setPreferredSize(new Dimension(400, 100));

		this.panel.add(this.rocketDataPanel, BorderLayout.CENTER);
		this.panel.add(this.topPanel, BorderLayout.NORTH);
	}

	@Override
	public JPanel enable(MenuController menu, Resources resource) {
		this.resources = resource;
		Null.nonNull(this.resources).getDriver().addRocketDataListener(this);
		return this.panel;
	}

	@Override
	public void init(MenuController menu, Observer o) {
		this.addObserver(o);
	}

	@Override
	public String name() {
		return "armed";
	}

	@Override
	public void update(@Nullable Observable o, @Nullable Object arg) {
		if (arg instanceof String[]) {
			String[] args = (String[]) arg;
			if (args.length == 1) {
				if (args[0].equals("DISARM")) {
					// TODO: Disarm
					String[] newArgs = {"switch view", "unarmed"};
					notify(newArgs);
				}
			}
		}
	}

	/**
	 * Notifies the Observer that there is an Object they can view.
	 * Can be passed any type of Object.
	 *
	 * @param o
	 */
	private void notify(Object o) {
		this.setChanged();
		this.notifyObservers(o);
	}

	@Override
	public void releaseResources() {
		if (this.resources != null) {
			Null.nonNull(this.resources).getDriver().removeRocketDataListener(this);
		}
	}

	@Override
	public void receiveRocketData(RocketData data) {
		this.rocketDataPanel.passRocketData(data);
	}

}
