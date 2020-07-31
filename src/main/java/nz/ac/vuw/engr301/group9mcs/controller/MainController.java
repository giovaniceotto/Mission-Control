package nz.ac.vuw.engr301.group9mcs.controller;

import java.awt.BorderLayout;

import javax.swing.JFrame;

/**
 * Controller class.
 * Creates the screen.
 *
 * @author Bryony
 * @author Claire
 */
public class MainController extends JFrame {

	private static final long serialVersionUID = -6186153488874946242L;
	
	/**
	 * The menu controller.
	 */
	private final MenuController menu;
	
	/**
	 * The perspective controller.
	 */
	private final PerspectiveController persp;

	/**
	 * Creates the screen.
	 */
	public MainController() {
		super("Mission Control");
		
		this.menu = new MenuController(this);
		this.menu.addMenuItem("file/exit", "Exit", (e) -> {
			this.setVisible(false);
			this.dispose();
		});
		
		this.persp = new PerspectiveController(this.menu);
		this.setLayout(new BorderLayout());
		this.add(this.persp.getPanel(), BorderLayout.CENTER);

		this.setSize(300, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	/**
	 * Adds a perspective to this main
	 * 
	 * @param name The name of the perspective
	 * @param perspective The perspective
	 */
	public void addPerspective(String name, Perspective perspective)
	{
		this.persp.addPerspective(name, perspective);
	}
	
	/**
	 * Changes the active perspective. Should only be called once
	 * 
	 * @param name
	 */
	public void setPerspective(String name)
	{
		this.persp.changePerspective(name);
	}


}