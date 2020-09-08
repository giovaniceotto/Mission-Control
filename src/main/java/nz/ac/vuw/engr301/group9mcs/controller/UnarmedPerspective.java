package nz.ac.vuw.engr301.group9mcs.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.eclipse.jdt.annotation.Nullable;

import nz.ac.vuw.engr301.group9mcs.view.ArmedButtonPanel;

/**
 * Perspective that holds the Panels for the when the rocket is Unarmed.
 * 
 * @author Bryony
 *
 */
public class UnarmedPerspective  extends Observable implements Perspective, Observer {

	/**
	 * The Panel displayed on the screen that holds all other panels.
	 */
	private JPanel panel;

	/** 
	 * GO NO GO PANEL : simulation
	 */
	private JPanel goNoGoPanel;

	/** 
	 * ARM BUTTON : pop-up to ask user "are you sure?" 
	 * -> Disclaimer: You are responsible for checking the surroundings are really clear before firing. 
	 * Don't trust our maps which don't know about people.
	 */
	private JPanel armButton;

	/** 
	 * BIG FAT WARNING PANEL : do not launch, we know where you are
	 */
	private JPanel warningPanel;

	/**
	 *  PANEL TO CONNECT TO ROCKET : should be first thing
	 */
	private JPanel rocketDetailsPanel;

	// CACHED MAP
	// TODO: What to do with this?

	/** 
	 * INPUT FOR WEATHER : User should gather weather data in the field
	 * BUTTON TO RUN SIMULATION : sends weather data, displays output
	 */
	private JPanel weatherDetailsPanel;

	/**
	 * Construct the Panel
	 */
	public UnarmedPerspective() {
		this.panel = new JPanel(new BorderLayout());

		// TODO: real warning panel
		this.warningPanel = new JPanel() {
			/***/
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(@Nullable Graphics g) {
				g.setColor(Color.red);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
		};
		this.warningPanel.setPreferredSize(new Dimension(400, 100));

		// TODO: real rocket details panel
		this.rocketDetailsPanel = new JPanel() {
			/***/
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(@Nullable Graphics g) {
				g.setColor(Color.orange);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
		};
		this.rocketDetailsPanel.setPreferredSize(new Dimension(200, 300));

		// TODO: real weather details panel
		this.weatherDetailsPanel = new JPanel() {
			/***/
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(@Nullable Graphics g) {
				g.setColor(Color.blue);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
		};
		JButton run = new JButton("Run Simulation");
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(@Nullable ActionEvent e) {
				switchTo(viewDetails());
			}
		});
		this.weatherDetailsPanel.add(run);
		this.weatherDetailsPanel.setPreferredSize(new Dimension(200, 300));
		
		// TODO: real weather details panel
		this.goNoGoPanel = new JPanel() {
			/***/
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(@Nullable Graphics g) {
				g.setColor(Color.yellow);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
		};
		this.goNoGoPanel.setPreferredSize(new Dimension(300, 300));
		
		// TODO: real weather details panel
		this.armButton = new ArmedButtonPanel(this);
		
		switchTo(viewDetails());
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
		return "armed";
	}
	
	/**
	 * Returns a Panel containing the panels for entering details.
	 * Warning Panel at the top.
	 * Rocket Details panel to the left (for connecting to the rocket).
	 * Weather Details panel to the right (for entering weather and starting simulation).
	 * 
	 * @return A Panel
	 */
	JPanel enterDetails() {
		JPanel details = new JPanel(new BorderLayout());
		details.add(this.warningPanel, BorderLayout.NORTH);
		details.add(this.rocketDetailsPanel, BorderLayout.WEST);
		details.add(this.weatherDetailsPanel, BorderLayout.CENTER);
		details.setSize(new Dimension(400, 400));
		return details;
	}
	
	/**
	 * Returns a Panel containing the panels for viewing details.
	 * Warning Panel at the top.
	 * GoNoGo Panel to the left (shows simulation results).
	 * Arm Button Panel to the right (button to arm rocket).
	 * @return A Panel
	 */
	JPanel viewDetails() {
		JPanel details = new JPanel(new BorderLayout());
		details.add(this.warningPanel, BorderLayout.NORTH);
		details.add(this.goNoGoPanel, BorderLayout.CENTER);
		details.add(this.armButton, BorderLayout.EAST);
		details.setSize(new Dimension(400, 400));
		return details;
	}

	@Override
	public void update(@Nullable Observable o, @Nullable Object arg) {
		if (arg instanceof String[]) {
			String[] args = (String[]) arg;
			if (args.length == 2) {
				if (args[0].equals("switch")) {
					if (args[1].equals("enterDetails")) {
						switchTo(enterDetails());
					}
				}
			}
		}
	}

	/**
	 * Switch to the given View Panel.
	 * 
	 * @param newPanel
	 */
	void switchTo(JPanel newPanel) {
		this.panel.removeAll();
		this.panel.add(newPanel, BorderLayout.CENTER);
		this.panel.revalidate();
		this.panel.repaint();
	}

}
