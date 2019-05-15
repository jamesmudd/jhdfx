package io.jhdf.fx.menu;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import io.jhdf.HdfFile;
import io.jhdf.api.Attribute;
import io.jhdf.api.Dataset;
import io.jhdf.api.Node;
import io.jhdf.fx.DatasetDialogController;
import io.jhdf.fx.TextFieldTreeCellImpl;
import io.jhdf.fx.persistance.RecentFiles;
import io.jhdf.fx.persistance.UserPersistance;
import io.jhdf.fx.tree.HdfTreeItem;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class AppController implements Initializable {

	@FXML
	TreeView<Node> tree;

	@FXML
	Menu openRecent;

	@FXML
	TableView<Map.Entry<String, Attribute>> attributeTable;
	@FXML
	TableColumn<Map.Entry<String, Attribute>, String> attributeName;
	@FXML
	TableColumn<Map.Entry<String, Attribute>, String> attributeValue;

	@FXML
	TextField nameField;

	@FXML
	TextField pathField;

	@FXML
	TextField typeField;

	@FXML
	Label fileStatus;

	@FXML
	TextField idField;

	private File lastOpenLocation;

	@FXML
	public void openFile(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open HDF5 file");
		ExtensionFilter hdf5Filter = new ExtensionFilter("HDF5 files (.h5, .hdf5, .he5)",
				Arrays.asList("*.h5", "*.hdf5", "*.he5"));
		ExtensionFilter nexusFilter = new ExtensionFilter("NeXus files (.nxs)", Collections.singletonList("*.nxs"));
		ExtensionFilter allFilter = new ExtensionFilter("All files", Collections.singletonList("*"));
		fileChooser.getExtensionFilters().addAll(hdf5Filter, nexusFilter, allFilter);
		fileChooser.setSelectedExtensionFilter(hdf5Filter);
		fileChooser.setInitialDirectory(lastOpenLocation);
		File openedFile = fileChooser.showOpenDialog(tree.getScene().getWindow());

		if (openedFile != null) {
			openFile(openedFile);
			lastOpenLocation = openedFile.getParentFile();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		populateOpenRecent(UserPersistance.getRecentFiles());

		tree.setRoot(new TreeItem<>());
		tree.setCellFactory(param -> new TextFieldTreeCellImpl());
		tree.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateAttributes(newValue));
		tree.setOnMouseClicked(new EventHandler<>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				// Double click tree item
				if (mouseEvent.getClickCount() == 2) {
					TreeItem<Node> item = tree.getSelectionModel().getSelectedItem();
					Node node = item.getValue();
					if (node instanceof Dataset) {
						Dataset dataset = (Dataset) node;
						FXMLLoader loader = new FXMLLoader(getClass().getResource("../datasetDialog.fxml"));
						try {
							Pane pane = loader.load();
							Scene scene = new Scene(pane);
							Stage stage = new Stage();
							stage.setScene(scene);
							stage.setTitle("jHDFx - " + dataset.getPath());
							stage.show();
							DatasetDialogController controller = loader.getController();
							controller.setDataset(dataset);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});

		attributeName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
		attributeValue.setCellValueFactory(
				param -> new SimpleStringProperty(ArrayUtils.toString(param.getValue().getValue().getData())));
	}

	private void populateOpenRecent(RecentFiles recentFiles) {
		ObservableList<MenuItem> recents = FXCollections.observableArrayList();

		if (recentFiles.isEmpty()) {
			MenuItem item = new MenuItem("None");
			item.setDisable(true);
			recents.add(item);

		} else {
			for (String string : recentFiles) {
				MenuItem item = new MenuItem(string);
				item.setOnAction(e -> openFile(new File(string)));
				recents.add(item);
			}
		}
		openRecent.getItems().setAll(recents);
	}

	private void openFile(File file) {
		try {
			HdfFile hdfFile = new HdfFile(file);
			TreeItem<Node> fileRoot = new HdfTreeItem(hdfFile);
			tree.getRoot().getChildren().add(fileRoot);
			RecentFiles recentFiles = UserPersistance.persistFileOpen(file);
			refreshRecents(recentFiles);
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, "Failed to open file: " + file.getAbsolutePath());
			alert.showAndWait();
		}
	}

	private void refreshRecents(RecentFiles recentFiles) {
		populateOpenRecent(recentFiles);
	}

	private void updateAttributes(TreeItem<Node> item) {
		if (item != null) {
			ObservableList<Map.Entry<String, Attribute>> items = FXCollections
					.observableArrayList(item.getValue().getAttributes().entrySet());
			attributeTable.setItems(items);
			nameField.setText(item.getValue().getName());
			pathField.setText(item.getValue().getPath());
			typeField.setText(item.getValue().getType().toString());
			idField.setText(Long.toString(item.getValue().getAddress()));
			fileStatus.setText(item.getValue().getFile().getAbsolutePath());
		} else {
			ObservableList<Map.Entry<String, Attribute>> items = FXCollections.observableArrayList();
			attributeTable.setItems(items);
			nameField.setText("");
			pathField.setText("");
			typeField.setText("");
			idField.setText("");
			fileStatus.setText("");
		}
	}

	@FXML
	public void openAboutDialog() {
		System.err.println("Implement about dialog");
	}

	@FXML
	public void closeAll() {
		tree.getRoot().getChildren().setAll(Collections.emptyList());
	}

	@FXML
	public void close() {
		ObservableList<TreeItem<Node>> selectedItems = tree.selectionModelProperty().getValue().getSelectedItems();
		ObservableList<TreeItem<Node>> allOpenFiles = tree.getRoot().getChildren();

		List<TreeItem<Node>> toBeClosed = selectedItems.stream().filter(allOpenFiles::contains)
				.collect(Collectors.toList());

		tree.getRoot().getChildren().removeAll(toBeClosed);
	}

	@FXML
	public void quit() {
		System.exit(0);
	}

}
