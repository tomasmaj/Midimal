package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Midimal;
import model.Sequencer;
import model.View;

/**
 * Controller for the top menu in application
 * 
 * @author Tomas Majling
 *
 */
public class MenuController implements Initializable, View {

	private ViewController vc;
	private Sequencer sequencer;
	private File file;

	// FXML variables injection
	@FXML
	MenuItem newSong;
	@FXML
	MenuItem openSong;
	@FXML
	MenuItem saveSong;
	@FXML
	MenuItem exit;

	@FXML
	MenuItem soundOnNote;
	@FXML
	MenuItem displayKeyValue;

	@FXML
	MenuItem gettingStarted;
	@FXML
	MenuItem about;

	public MenuController() {
		this.sequencer = Midimal.INSTANCE.getSequencer();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		newSong.setOnAction(ae -> {
			createNewSong();
		});

		openSong.setOnAction(ae -> {
			open();
		});

		saveSong.setOnAction(ae -> {
			save();
		});

		exit.setOnAction(ae -> {
			Platform.exit();
		});

		displayKeyValue.setOnAction(ae -> {
			if (displayKeyValue.getText().equals("Show key value")) {
				vc.setDisplayKeyNames(true);
				displayKeyValue.setText("Hide key value");
			} else {
				vc.setDisplayKeyNames(false);
				displayKeyValue.setText("Show key value");
			}
		});

		soundOnNote.setOnAction(ae -> {
			if (soundOnNote.getText().equals("Enable sound on add note")) {
				vc.setSoundOnNote(true);
				soundOnNote.setText("Disable sound on add note");
			} else {
				vc.setSoundOnNote(false);
				soundOnNote.setText("Enable sound on add note");
			}
		});

		gettingStarted.setOnAction(ae -> {
			Alert info = new Alert(AlertType.INFORMATION);
			info.setTitle("Getting started using Midimal");
			info.setHeaderText("Quick guide to getting started with Midimal");
			info.setContentText(
					"1. Select track on left side bar.\n2. Add new track part.\n3. Double click on the newly created track part.\n4. Go back to track view with button down on the right side.\n5. Let your creativity flow!");
			info.showAndWait();
		});

		about.setOnAction(ae -> {
			Alert info = new Alert(AlertType.INFORMATION);
			info.setTitle("About");
			info.setHeaderText("Midimal is made by Tomas Majling");
			info.setContentText("Midimal - Copyright 2016");
			info.showAndWait();
		});
	}

	private void createNewSong() {

		Midimal.INSTANCE.setSequencer(new Sequencer(4, "src/files/soundbank.sf2"));
		this.sequencer = Midimal.INSTANCE.getSequencer();

		if (vc.isShowSequencer()) {
			vc.removeView("sequencer");
		} else {
			vc.removeView("trackpart");
		}
		vc.loadView("sequencer", "../view/SequencerLayout.fxml");
		vc.setCenterView("sequencer");
	}

	@Override
	public void initData() {
		vc.setDisplayKeyName(displayKeyValue);
	}

	@Override
	public void setRootView(ViewController viewController) {
		this.vc = viewController;
	}

	private void save() {
		this.file = fileChoose((Stage) vc.getCenter().getScene().getWindow(), "Save song", true);

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.file))) {
			bufferedWriter.write(convertDataToFile());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void open() {
		this.file = fileChoose((Stage) vc.getCenter().getScene().getWindow(), "Open song", false);
		loadDataFromFile(this.file);
	}

	private void loadDataFromFile(File file) {
		String strToRead = "";
		String[] textOneOneLineSplit = null;
		int counter = 0;
		int track = -1;
		int trackPart = -1;
		int yCoord = 0;

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.file))) {
			Midimal.INSTANCE.setSequencer(new Sequencer(4, "src/files/soundbank.sf2"));
			this.sequencer = Midimal.INSTANCE.getSequencer();
			for (int i = 0; i < 4; i++) {
				this.sequencer.getTracks().get(i).getTrackPart().clear();
			}
			while ((strToRead = bufferedReader.readLine()) != null) {
				if (counter == 0) {
					track++;
					textOneOneLineSplit = strToRead.split(",");
					this.sequencer.setTempo(Integer.parseInt(textOneOneLineSplit[0]));
					this.sequencer.getTracks().get(track).setLoadedInstrument(textOneOneLineSplit[1]);
					this.sequencer.getTracks().get(track).setProgram(Integer.parseInt(textOneOneLineSplit[2]));
					this.sequencer.getTracks().get(track).setVelocity(Integer.parseInt(textOneOneLineSplit[3]));
					counter++;
					continue;
				}

				if (counter == 1 && !(strToRead.equals(""))) {
					this.sequencer.getTracks().get(track).addTrackPart();
					trackPart++;
					counter++;
					continue;
				} else if (strToRead.equals("")) {
					counter = 0;
					trackPart = -1;
				}

				if (counter == 2) {
					textOneOneLineSplit = strToRead.split(",");
					for (int j = 0; j < 16; j++) {
						if (textOneOneLineSplit[j].equals("true")) {
							this.sequencer.getTracks().get(track).getTrackPart().get(trackPart).addNote(yCoord, j);
						}
					}
					yCoord++;
					if (yCoord == 18) {
						counter = 1;
						yCoord = 0;
					}
				}

			}
			if (vc.isShowSequencer()) {
				vc.removeView("sequencer");
			} else {
				vc.removeView("trackpart");
			}
			vc.loadView("sequencer", "../view/SequencerLayout.fxml");
			vc.setCenterView("sequencer");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File fileChoose(Stage stage, String title, boolean save) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter fileChooserExt = new FileChooser.ExtensionFilter("Midimal files (*.mma)", "*.mma");
		fileChooser.getExtensionFilters().add(fileChooserExt);
		fileChooser.setTitle(title);
		if (save) {
			return fileChooser.showSaveDialog(stage);
		} else {
			return fileChooser.showOpenDialog(stage);
		}
	}

	private String convertDataToFile() {
		String strOfSongData = "";

		for (int track = 0; track < this.sequencer.getTracks().size(); track++) {
			String tempo = Integer.toString(this.sequencer.getTempo());
			String loadedInstrumentName = this.sequencer.getTracks().get(track).getLoadedInstrument();
			String loadedInstrumentProgram = Integer.toString(this.sequencer.getTracks().get(track).getProgram());
			String volume = Integer.toString(this.sequencer.getTracks().get(track).getVelocity());

			strOfSongData = strOfSongData
					.concat(tempo + "," + loadedInstrumentName + "," + loadedInstrumentProgram + "," + volume + "\n");

			for (int trackParts = 0; trackParts < this.sequencer.getTracks().get(track).getTrackPart()
					.size(); trackParts++) {
				String trackPartNumber = Integer.toString(trackParts);

				strOfSongData = strOfSongData.concat(trackPartNumber + "\n");

				for (int noteRow = 0; noteRow < 18; noteRow++) {
					for (int noteColumn = 0; noteColumn < 16; noteColumn++) {
						String noteValue = Boolean.toString(this.sequencer.getTracks().get(track).getTrackPart()
								.get(trackParts).getNote(noteRow, noteColumn));

						strOfSongData = strOfSongData.concat(noteValue + ",");

					}
					strOfSongData += "\n";
				}
			}
			if (track != this.sequencer.getTracks().size() - 1) {
				strOfSongData += "\n";
			}
		}

		return strOfSongData;
	}

}
