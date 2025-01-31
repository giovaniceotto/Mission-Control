package nz.ac.vuw.engr301.group9mcs.view;

/**
 * Listens for changes in the position
 *
 * @author August Bolter
 * Copyright (C) 2020, Mission Control Group 9
 */
public interface PositionUpdateListener {
	/**
	 * Updated the position.
	 * @param lat The positions new latitude
	 * @param lon The positions new longitude
	 */
	void poisitionUpdate(double lat, double lon);
}
