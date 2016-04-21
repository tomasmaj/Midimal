package model;

import controller.ViewController;

/**
 * Root view used to connect subviews
 * 
 * @author Tomas Majling
 *
 */

public interface View {

	/**
	 * Set to root view on child views
	 * 
	 * @param viewController
	 *            is the master view of the application
	 */
	public void setRootView(ViewController viewController);

	/**
	 * Initialize data after the controllers contructor have run
	 * 
	 */
	public void initData();

}
