package controller;

import java.io.IOException;
import java.util.HashMap;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import model.View;

/**
 * Masterview for application
 * Handles all views in root when changing scene
 * 
 * @author Tomas Majling
 *
 */
public class ViewController extends BorderPane {

	private HashMap<String, Node> views;

	private int loadedTrack;
	private int loadedTrackPart;

	private boolean showSequencer;
	private boolean soundOnNote;
	private boolean displayKeyNames;

	private MenuItem displayKeyName;

	/**
	 * Constructor that instantiate a HashMap of Key-String and Value-Node
	 * 
	 */
	public ViewController() {
		this.views = new HashMap<>();
		this.loadedTrack = 0;
		this.loadedTrackPart = 0;
		this.showSequencer = true;
		this.soundOnNote = false;
		this.displayKeyNames = true;
	}

	/**
	 * Add a view to to HashMap to get it by name
	 * 
	 * @param name
	 *            of view
	 * @param view
	 *            after loaded from fxml file
	 */
	public void addView(String name, Node view) {
		views.put(name, view);
	}

	/**
	 * Get a view by name from the HashMap
	 * 
	 * @param name
	 *            of the view
	 * @return a Node of the view
	 */
	public Node getView(String name) {
		return views.get(name);
	}

	public boolean isSoundOnNote() {
		return soundOnNote;
	}

	public void setSoundOnNote(boolean soundOnNote) {
		this.soundOnNote = soundOnNote;
	}

	public boolean isDisplayKeyNames() {
		return displayKeyNames;
	}

	public void setDisplayKeyNames(boolean displayKeyNames) {
		this.displayKeyNames = displayKeyNames;
	}

	public MenuItem getDisplayKeyName() {
		return displayKeyName;
	}

	public void setDisplayKeyName(MenuItem displayKeyName) {
		this.displayKeyName = displayKeyName;
	}

	public boolean isShowSequencer() {
		return showSequencer;
	}

	public void setShowSequencer(boolean showSequencer) {
		this.showSequencer = showSequencer;
	}

	public void setLoadedTrackPart(int trackPartNumber) {
		this.loadedTrackPart = trackPartNumber;
	}

	public int getLoadedTrackPart() {
		return this.loadedTrackPart;
	}

	public void setLoadedTrack(int trackNumber) {
		this.loadedTrack = trackNumber;
	}

	public int getLoadedTrack() {
		return this.loadedTrack;
	}

	public void setLoadedTrackPart(int track, int trackPart) {
		this.loadedTrack = track;
		this.loadedTrackPart = trackPart;
	}

	/**
	 * Load the view to the ViewController
	 * 
	 * @param name
	 *            of the view
	 * @param fxmlFile
	 *            of the views file path
	 */
	public void loadView(String name, String fxmlFile) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			Parent loadNode;
			loadNode = (Parent) loader.load();
			View viewController = ((View) loader.getController());
			viewController.setRootView(this);
			viewController.initData();
			addView(name, loadNode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove a view by name from the HashMap
	 * 
	 * @param name
	 *            of the view
	 */
	public void removeView(String name) {
		views.remove(name);
	}

	/**
	 * Set a loaded view to the center of the root node
	 * 
	 * @param name
	 *            of the view
	 */
	public void setCenterView(String name) {
		if (views.get(name) != null) {
			DoubleProperty opacity = opacityProperty();

			if (!(getCenter() == null)) {
				Timeline fadeOut = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
						new KeyFrame(new Duration(1000), (ae) -> {
							setCenter(views.get(name));
							Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
									new KeyFrame(new Duration(800), new KeyValue(opacity, 1.0)));
							fadeIn.play();
						} , new KeyValue(opacity, 0.0)));
				fadeOut.play();

			} else {
				setOpacity(0.0);
				setCenter(views.get(name));
				Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
						new KeyFrame(new Duration(2500), new KeyValue(opacity, 1.0)));
				fadeIn.play();
			}
		} else {
			System.out.println("No view was loaded with that name");
		}
	}

	/**
	 * Set a loaded view to the top of the root node
	 * 
	 * @param name
	 *            of the view
	 */
	public void setTopView(String name) {
		if (views.get(name) != null) {
			setTop(views.get(name));
		} else {
			System.out.println("No view was loaded with that name");
		}
	}

}
