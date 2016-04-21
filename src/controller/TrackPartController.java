package controller;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import model.Midimal;
import model.Sequencer;
import model.Track;
import model.TrackPart;
import model.View;
import model.instrument.MidiDrumKit;

/**
 * Manage the view for the notes in a track part
 * 
 * @author Tomas Majling
 *
 */

public class TrackPartController implements Initializable, View {

	private HashMap<String, StackPane> cellMap;
	private HashMap<Integer, List<StackPane>> addedNotesMap;

	private final int WIDTH = 40;
	private final int HEIGHT = 40;

	private final int noteLengthInMs = 500;

	private Sequencer sequencer;
	private Track track;
	private TrackPart trackPart;

	private Timeline timeline;
	private KeyFrame timeframe;
	private ViewController vc;

	private final double ONE_TEMPO_IN_MS = 375;

	// FXML variables injection
	@FXML
	Pane pane;

	@FXML
	Line line;

	@FXML
	Label trackLabel;

	@FXML
	Button play;
	@FXML
	Button stop;

	@FXML
	TextField tempo;

	@FXML
	Button backBtn;

	@FXML
	ComboBox<String> instrumentBox;

	@FXML
	Slider volume;
	@FXML
	Label volumeValue;

	public TrackPartController() {
		this.cellMap = new HashMap<>();
		this.addedNotesMap = new HashMap<>();
		addnotesMap();
		this.sequencer = Midimal.INSTANCE.getSequencer();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setupLine();
		playLine();
		this.tempo.setText(String.valueOf(this.sequencer.getTempo()));

		play.setOnAction(ae -> {
			play();
		});

		stop.setOnAction(ae -> {
			stop();
		});

		volume.valueProperty().addListener((observable, oldValue, newValue) -> {
			volumeValue.setText(String.valueOf(newValue.intValue()));
			this.track.setVelocity(newValue.intValue());
		});

		backBtn.setOnAction(ae -> {
			stop();
			ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(2000), vc.getCenter());
			scaleTransition.setToX(-1f);
			scaleTransition.setToY(-1f);
			scaleTransition.play();
			vc.removeView("trackpart");
			vc.setShowSequencer(true);
			vc.loadView("sequencer", "../view/SequencerLayout.fxml");
			vc.setCenterView("sequencer");
		});

		instrumentBox.setOnAction(ae -> {
			this.track.setProgram(this.track.getMidiInstrument().getInstrument().get(instrumentBox.getValue()));
			this.track.setLoadedInstrument(this.instrumentBox.getValue());
		});

		tempo.setOnAction(ae -> {
			int tempoValue = Integer.parseInt(tempo.getText());
			if (tempoValue < 60) {
				tempo.setText("60");
				tempoValue = 60;
			} else if (tempoValue > 180) {
				tempo.setText("180");
				tempoValue = 180;
			}

			newTempo(tempoValue);
		});

	}

	@Override
	public void initData() {

		this.trackLabel.setText("Track " + (this.vc.getLoadedTrack() + 1));
		this.track = this.sequencer.getTracks().get(this.vc.getLoadedTrack());
		this.trackPart = this.track.getTrackPart().get(this.vc.getLoadedTrackPart());
		addCellLayout();
		addInstrumentLayout();
		addVolumeLayout();
		this.addKeyNameLayout();



		vc.getDisplayKeyName().textProperty().addListener((ob, oldValue, newValue) -> {
			if (newValue.equals("Show key value")) {
				pane.getChildren().remove(pane.getChildren().size() - 18, pane.getChildren().size());
			} else {
				this.addKeyNameLayout();
			}
		});
	}

	@Override
	public void setRootView(ViewController viewController) {
		this.vc = viewController;

	}

	private double calculateTempoInMs() {
		return (this.ONE_TEMPO_IN_MS / this.sequencer.getTempo()) / 1000;
	}

	private void addVolumeLayout() {
		int volumeNumber = this.track.getVelocity();
		volumeValue.setText(String.valueOf(volumeNumber));
		volume.setValue(volumeNumber);
	}

	private void play() {
		if (play.getText().equals("Play")) {
			timeline.play();
			play.setText("Pause");
		} else {
			timeline.stop();
			this.track.getChannel().allNotesOff();
			play.setText("Play");
		}
	}

	private void stop() {
		if (timeline.getStatus() == Status.RUNNING) {
			timeline.stop();
			this.track.getChannel().allNotesOff();
			play.setText("Play");
		}
		this.line.setStartX(-1);
		this.line.setEndX(-1);
	}

	private void addnotesMap() {

		for (int i = 0; i < 16; i++) {
			this.addedNotesMap.put(i, new LinkedList<>());
		}
	}

	private void addCellToNotesMap(StackPane cell) {
		int x = (int) (cell.getLayoutX() == 0 ? 0 : cell.getLayoutX() / 40);
		int y = (int) (cell.getLayoutY() == 0 ? 0 : cell.getLayoutY() / 40);
		this.trackPart.addNote(y, x);
		List<StackPane> notesMap = this.addedNotesMap.get(x);
		notesMap.add(cell);

	}

	private void removeCellToNotesMap(StackPane cell) {

		int x = (int) (cell.getLayoutX() == 0 ? 0 : cell.getLayoutX() / 40);
		int y = (int) (cell.getLayoutY() == 0 ? 0 : cell.getLayoutY() / 40);
		this.trackPart.removeNote(y, x);
		List<StackPane> notesMap = this.addedNotesMap.get(x);
		notesMap.remove(cell);

	}

	private void setupLine() {

		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);

	}

	private void playLine() {
		timeframe = new KeyFrame(Duration.seconds(calculateTempoInMs()), ae -> {
			line.setStartX(line.getStartX() + 1);
			line.setEndX(line.getEndX() + 1);
			if (line.getStartX() >= 640) {
				line.setStartX(0);
				line.setEndX(0);
			}

			if (line.getStartX() % this.WIDTH == 0) {
				int x = (int) (line.getStartX() == 0 ? 0 : line.getStartX() / this.WIDTH);
				List<StackPane> notesMap = this.addedNotesMap.get(x);

				for (StackPane cell : notesMap) {
					new Thread(() -> {
						playNote(cell);
						try {
							Thread.sleep(this.noteLengthInMs);
						} catch (Exception e) {
							e.printStackTrace();
						}
						offNote(cell);
					}).start();
				}
			}

		});
		timeline.getKeyFrames().clear();
		timeline.getKeyFrames().add(timeframe);
	}

	private void newTempo(int tempoValue) {
		this.sequencer.setTempo(tempoValue);
		if (this.timeline.getStatus() != Status.STOPPED) {
			this.timeline.stop();
			this.playLine();
			this.timeline.play();
		} else {
			this.playLine();
		}
	}

	private void addInstrumentLayout() {
		ObservableList<String> instrument = FXCollections
				.observableArrayList(this.track.getMidiInstrument().getInstrument().keySet());
		instrument.sort(null);
		this.instrumentBox.getItems().addAll(instrument);
		this.instrumentBox.setValue(this.track.getLoadedInstrument());
	}

	private void addCellLayout() {
		for (int x = 0; x < this.pane.getPrefWidth(); x += this.WIDTH) {
			for (int y = 0; y < this.pane.getPrefHeight(); y += this.HEIGHT) {
				StackPane cell = new StackPane();
				cell.setLayoutX(x);
				cell.setLayoutY(y);
				cell.setPrefWidth(this.WIDTH);
				cell.setPrefHeight(this.HEIGHT);
				cell.getStyleClass().add("note-off");

				int noteY = y == 0 ? 0 : y / 40;
				int noteX = x == 0 ? 0 : x / 40;
				if (this.trackPart.getNote(noteY, noteX)) {
					cell.getStyleClass().clear();
					cell.getStyleClass().add("note-on");
					addCellToNotesMap(cell);
				}

				cell.setOnMouseClicked(ae -> {
					if (cell.getStyleClass().get(0).equals("note-off")) {
						cell.getStyleClass().clear();
						cell.getStyleClass().add("note-on");
						addCellToNotesMap(cell);
						if (vc.isSoundOnNote()) {
							new Thread(() -> {
								playNote(cell);
								try {
									Thread.sleep(this.noteLengthInMs);
								} catch (Exception e) {
									e.printStackTrace();
								}
								offNote(cell);
							}).start();
						}
					} else {
						cell.getStyleClass().clear();
						removeCellToNotesMap(cell);
						cell.getStyleClass().add("note-off");
					}
				});

				this.pane.getChildren().add(cell);
				this.cellMap.put(x + " " + y, cell);

			}
		}
	}

	private void addKeyNameLayout() {
		if (vc.isDisplayKeyNames()) {
			int index = 0;
			for (int i = 0; i < (18 * this.HEIGHT); i += this.HEIGHT) {
				Label label;
				if (this.track.getMidiInstrument() instanceof MidiDrumKit) {
					label = new Label(this.track.getMidiInstrument().getScaleName()[i / this.HEIGHT]);
				} else {
					if (index >= this.track.getMidiInstrument().getScaleName().length) {
						index = 0;
					}
					label = new Label(this.track.getMidiInstrument().getScaleName()[index]);
					index++;
				}

				StackPane keyName = new StackPane();
				keyName.setMouseTransparent(true);
				keyName.setLayoutX(0);
				keyName.setLayoutY(i);
				keyName.setMinWidth(this.WIDTH);
				keyName.setPrefHeight(this.HEIGHT);
				keyName.setAlignment(Pos.CENTER);
				keyName.setStyle("-fx-padding: 5px");
				label.setMouseTransparent(true);
				label.getStyleClass().add("label");
				keyName.getChildren().add(label);
				this.pane.getChildren().add(keyName);
			}
		}
	}

	private void offNote(StackPane cell) {
		int index = cell.getLayoutY() == 0 ? 0 : (int) cell.getLayoutY() / this.HEIGHT;
		this.track.getChannel().noteOff(this.track.getMidiInstrument().getScaleNumber()[index]);
	}

	private void playNote(StackPane cell) {
		int index = cell.getLayoutY() == 0 ? 0 : (int) cell.getLayoutY() / this.HEIGHT;
		this.track.getChannel().noteOn(this.track.getMidiInstrument().getScaleNumber()[index],
				this.track.getVelocity());
	}

}
