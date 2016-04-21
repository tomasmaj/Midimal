package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import model.Midimal;
import model.Sequencer;
import model.TrackPart;
import model.View;

/**
 * Manage the view for the sequence and all the track parts
 * 
 * @author Tomas Majling
 *
 */

public class SequencerController implements Initializable, View {

	private ViewController vc;
	private Sequencer sequencer;
	private Timeline timeline;
	private KeyFrame timeframe;

	private int lineOnTrackPart = 0;
	private int selectedTrack;
	private int lengthToPlay;

	private final int TRACKPART_WIDTH = 8;
	private final int TRACKPART_HEIGHT = 8;
	private final double ONE_TEMPO_IN_MS = 1875;

	// FXML variables injection
	@FXML
	Label trackLabel;

	@FXML
	Button play;
	@FXML
	Button stop;

	@FXML
	Line line;


	@FXML
	ComboBox<String> instrumentBox;

	@FXML
	Label volumeValue;
	@FXML
	Slider volume;

	@FXML
	Button solo;
	@FXML
	Button mute;

	@FXML
	TextField tempo;

	@FXML
	HBox trackOneSeq;
	@FXML
	HBox trackTwoSeq;
	@FXML
	HBox trackThreeSeq;
	@FXML
	HBox trackFourSeq;

	public SequencerController() {
		this.sequencer = Midimal.INSTANCE.getSequencer();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadTrackPartLayout(trackOneSeq, 0);
		loadTrackPartLayout(trackTwoSeq, 1);
		loadTrackPartLayout(trackThreeSeq, 2);
		loadTrackPartLayout(trackFourSeq, 3);
		setupLine();
		playLine();

		play.setOnAction(ae -> {
			this.play();
		});

		stop.setOnAction(ae -> {
			this.stop();
			this.line.setStartX(-1);
			this.line.setEndX(-1);
		});

		solo.setOnAction(ae -> {
			solo();
		});

		mute.setOnAction(ae -> {
			mute();
		});

		line.setOnMouseDragged(ae -> {
			line.setStartX(ae.getX());
			line.setEndX(ae.getX());
		});

		line.setOnMouseReleased(ae -> {
			line.setStartX(line.getStartX() - (line.getStartX() % 128) - 1);
			line.setEndX(line.getEndX() - (line.getEndX() % 128) - 1);
		});

		volume.valueProperty().addListener((observable, oldValue, newValue) -> {
			volumeValue.setText(String.valueOf(newValue.intValue()));
			this.sequencer.getTracks().get(this.selectedTrack).setVelocity(newValue.intValue());
		});

		instrumentBox.setOnAction(ae -> {
			String instrumentValue = this.instrumentBox.getValue();
			if (this.sequencer.getTracks().get(selectedTrack).getMidiInstrument().getInstrument()
					.get(instrumentValue) != null) {
				this.sequencer.getTracks().get(selectedTrack).setProgram(this.sequencer.getTracks().get(selectedTrack)
						.getMidiInstrument().getInstrument().get(instrumentValue));
				this.sequencer.getTracks().get(selectedTrack).setLoadedInstrument(instrumentValue);
			}
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
		this.selectedTrack = this.vc.getLoadedTrack();
		changeSelectedTrack(this.selectedTrack);
		this.tempo.setText(String.valueOf(this.sequencer.getTempo()));
		this.trackLabel.setText("Track " + (this.selectedTrack + 1));
	}

	@Override
	public void setRootView(ViewController viewController) {
		this.vc = viewController;
	}

	private void mute() {
		boolean muteValue;
		if (mute.getStyleClass().get(1).equals("muteBtn-off")) {
			muteValue = true;
		} else {
			muteValue = false;
		}
		this.sequencer.getTracks().get(selectedTrack).getChannel().setMute(muteValue);
		this.addMuteLayout();
	}

	private void solo() {
		boolean soloValue;
		if (solo.getStyleClass().get(1).equals("soloBtn-off")) {
			soloValue = true;
			solo.getStyleClass().remove(1);
			solo.getStyleClass().add(1, "soloBtn-on");
		} else {
			soloValue = false;
			solo.getStyleClass().remove(1);
			solo.getStyleClass().add(1, "soloBtn-off");
		}
		this.sequencer.getTracks().get(selectedTrack).getChannel().setSolo(soloValue);
		this.addSoloLayout();
	}

	private void addSoloLayout() {
		if (this.sequencer.getTracks().get(selectedTrack).getChannel().getSolo() == true) {
			solo.getStyleClass().remove(1);
			solo.getStyleClass().add(1, "soloBtn-on");
		} else {
			solo.getStyleClass().remove(1);
			solo.getStyleClass().add(1, "soloBtn-off");
		}
	}
	
	private void addMuteLayout() {
		if (this.sequencer.getTracks().get(selectedTrack).getChannel().getMute() == true) {
			mute.getStyleClass().remove(1);
			mute.getStyleClass().add(1, "muteBtn-on");
		} else {
			mute.getStyleClass().remove(1);
			mute.getStyleClass().add(1, "muteBtn-off");
		}
	}

	private void loadTrackPartLayout(HBox trackSeq, int trackNumber) {
		for (int i = 0; i < this.sequencer.getTracks().get(trackNumber).getTrackPart().size(); i++) {
			loadNoteLayout(addTrackPartLayout(trackSeq), trackNumber, i);
		}
	}

	@FXML
	public void trackSelect(ActionEvent event) {
		Button trackButton = (Button) event.getSource();
		VBox trackParent = (VBox) trackButton.getParent();
		Label trackText = (Label) trackParent.getChildren().get(0);
		String track = trackText.getText();
		trackLabel.setText(track);

		int trackNumber = Integer.parseInt(String.valueOf(track.charAt(track.length() - 1))) - 1;
		this.selectedTrack = trackNumber;
		changeSelectedTrack(trackNumber);
	}

	private void changeSelectedTrack(int trackNumber) {
		addInstrumentLayout(trackNumber);
		addVolumeLayout(trackNumber);
		addMuteLayout();
		addSoloLayout();
	}

	private void addVolumeLayout(int trackNumber) {
		int volumeNumber = this.sequencer.getTracks().get(trackNumber).getVelocity();
		volumeValue.setText(String.valueOf(volumeNumber));
		volume.setValue(volumeNumber);
	}

	private void addInstrumentLayout(int trackNumber) {
		ObservableList<String> instrument = FXCollections.observableArrayList(
				this.sequencer.getTracks().get(trackNumber).getMidiInstrument().getInstrument().keySet());
		instrument.sort(null);
		this.instrumentBox.getItems().clear();
		this.instrumentBox.setItems(instrument);
		this.instrumentBox.setValue(this.sequencer.getTracks().get(trackNumber).getLoadedInstrument());
	}

	@FXML
	public void addNewTrackPart(ActionEvent event) {
		int trackNumber = Integer
				.parseInt(String.valueOf(trackLabel.getText().charAt(trackLabel.getText().length() - 1)));
		addTrackPart(trackNumber);
	}

	private void addTrackPart(int trackNumber) {
		switch (trackNumber) {
		case 1:
			addNewTrackPart(trackOneSeq);
			break;
		case 2:
			addNewTrackPart(trackTwoSeq);
			break;
		case 3:
			addNewTrackPart(trackThreeSeq);
			break;
		case 4:
			addNewTrackPart(trackFourSeq);
			break;
		default:
			System.out.println("No track was found");
		}
	}

	private void addNewTrackPart(HBox trackSeq) {
		int addedTrackNumber = (int) (trackSeq.getLayoutY() == 0 ? 0 : trackSeq.getLayoutY() / 176);
		this.sequencer.getTracks().get(addedTrackNumber).addTrackPart();
		addTrackPartLayout(trackSeq);
	}

	private StackPane addTrackPartLayout(HBox trackSeq) {
		StackPane stackPane = new StackPane();
		stackPane.setAlignment(Pos.TOP_LEFT);
		stackPane.setMinWidth(128);
		stackPane.setMinHeight(144);
		stackPane.setMaxWidth(128);
		stackPane.setMaxHeight(144);
		stackPane.getStyleClass().addAll("trackSeq", trackSeq.getId());
		trackSeq.getChildren().add(stackPane);

		this.lengthToPlay = sizeOfBiggestTrack() * 128;

		stackPane.setOnMouseClicked(ae -> {
			if (ae.getClickCount() == 2) {
				HBox parent = (HBox) stackPane.getParent();
				int loadedTrackNumber = (int) (parent.getLayoutY() == 0 ? 0 : parent.getLayoutY() / 176);
				int loadedTrackPartNumber = (int) (stackPane.getLayoutX() == 0 ? 0 : stackPane.getLayoutX() / 128);
				vc.setLoadedTrackPart(loadedTrackNumber, loadedTrackPartNumber);
				stop();
				ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(2000), vc.getCenter());
				scaleTransition.setToX(4f);
				scaleTransition.setToY(4f);
				scaleTransition.play();
				vc.removeView("sequencer");
				vc.setShowSequencer(false);
				vc.loadView("trackpart", "../view/TrackPartLayout.fxml");
				vc.setCenterView("trackpart");
			}
		});

		return stackPane;

	}

	private void loadNoteLayout(StackPane stackPane, int trackNumber, int trackPartNumber) {

		TrackPart trackPart = this.sequencer.getTracks().get(trackNumber).getTrackPart().get(trackPartNumber);

		for (int x = 0; x < stackPane.getMinWidth(); x += this.TRACKPART_WIDTH) {
			for (int y = 0; y < stackPane.getMinHeight(); y += this.TRACKPART_HEIGHT) {
				int noteX = x == 0 ? 0 : x / this.TRACKPART_WIDTH;
				int noteY = y == 0 ? 0 : y / this.TRACKPART_HEIGHT;

				if (trackPart.getNote(noteY, noteX)) {
					StackPane note = new StackPane();
					note.setTranslateX(x);
					note.setTranslateY(y);
					note.setMaxWidth(this.TRACKPART_WIDTH);
					note.setMaxHeight(this.TRACKPART_HEIGHT);
					note.getStyleClass().add("note-on");

					note.setOnMouseClicked(ae -> {
						System.out.println("Note layoutY: " + note.getLayoutY());
						System.out.println("Note TranslateY: " + note.getTranslateY());
						System.out.println("Note LayoutX: " + note.getLayoutX());
						System.out.println("Note TranslateX: " + note.getTranslateX());
					});

					stackPane.getChildren().add(note);
				}

			}
		}
	}

	private double calculateTempoInMs() {
		return (this.ONE_TEMPO_IN_MS / this.sequencer.getTempo()) / 1000;
	}

	private void setupLine() {

		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);

	}
	
	private void playLine() {
		timeframe = new KeyFrame(Duration.seconds(calculateTempoInMs()), ae -> {
			line.setStartX(line.getStartX() + 1);
			line.setEndX(line.getEndX() + 1);
			if (line.getStartX() >= this.lengthToPlay) {

				line.setStartX(0);
				line.setEndX(0);
			}

			if (line.getStartX() % 128 == 0) {
				this.lineOnTrackPart = (int) (line.getStartX() == 0 ? 0 : line.getStartX() / 128);
			}

			for (int track = 0; track < 4; track++) {
				if (line.getStartX() % this.TRACKPART_WIDTH == 0
						&& this.lineOnTrackPart < this.sequencer.getTracks().get(track).getTrackPart().size()) {
					for (int yCoord = 0; yCoord < 18; yCoord++) {
						int trackPart = (int) (line.getStartX() - (this.lineOnTrackPart * 128));
						int xCoord = (int) (trackPart == 0 ? 0 : trackPart / this.TRACKPART_WIDTH);
						if (this.sequencer.getTracks().get(track).getTrackPart().get(this.lineOnTrackPart)
								.getNote(yCoord, xCoord)) {
							int trackThread = track;
							int yCoordThread = yCoord;
							new Thread(() -> {
								onNote(trackThread, yCoordThread);
								try {
									Thread.sleep(500);
								} catch (Exception e) {
									e.printStackTrace();
								}
								offNote(trackThread, yCoordThread);
							}).start();
						}
					}
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

	private void offNote(int track, int note) {
		this.sequencer.getTracks().get(track).getChannel()
				.noteOff(this.sequencer.getTracks().get(track).getMidiInstrument().getScaleNumber()[note]);
	}

	private void onNote(int track, int note) {
		this.sequencer.getTracks().get(track).getChannel().noteOn(
				this.sequencer.getTracks().get(track).getMidiInstrument().getScaleNumber()[note],
				this.sequencer.getTracks().get(track).getVelocity());
	}

	private int sizeOfBiggestTrack() {

		int sizeOfTrack = 0;

		for (int i = 0; i < this.sequencer.getTracks().size(); i++) {
			int size = this.sequencer.getTracks().get(i).getTrackPart().size();
			if (size > sizeOfTrack) {
				sizeOfTrack = size;
			}
		}

		return sizeOfTrack;
	}

	private void play() {
		if (this.play.getText().equals("Play")) {
			timeline.play();
			this.play.setText("Pause");
		} else {
			timeline.stop();
			allNotesOff();
			this.play.setText("Play");
		}
	}

	private void stop() {
		if (timeline.getStatus() == Status.RUNNING) {
			timeline.stop();
			allNotesOff();
			this.play.setText("Play");
		}
		this.line.setStartX(-1);
		this.line.setEndX(-1);
	}

	private void allNotesOff() {
		for (int i = 0; i < 4; i++) {
			this.sequencer.getTracks().get(i).getChannel().allNotesOff();
		}

	}

}
