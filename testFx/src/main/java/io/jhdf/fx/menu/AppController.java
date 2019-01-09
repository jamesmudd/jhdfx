package io.jhdf.fx.menu;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import io.jhdf.HdfFile;
import io.jhdf.Node;
import io.jhdf.fx.TextFieldTreeCellImpl;
import io.jhdf.object.message.AttributeMessage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.stage.FileChooser;

public class AppController implements Initializable {

	@FXML
	TreeView<Node> tree;

	@FXML
	Menu openRecent;

	@FXML
	TableView<Map.Entry<String, AttributeMessage>> attributeTable;
	@FXML
	TableColumn<Map.Entry<String, AttributeMessage>, String> attributeName;
	@FXML
	TableColumn<Map.Entry<String, AttributeMessage>, String> attributeValue;

	@FXML
	TextField nameField;

	@FXML
	TextField pathField;

	@FXML
	TextField typeField;

	@FXML
	Label fileStatus;

	@FXML
	public void openFile(ActionEvent event) {
		System.out.println("Open File");

		FileChooser fc = new FileChooser();
		fc.setTitle("Open HDF5 file");
		File openedFile = fc.showOpenDialog(tree.getScene().getWindow());

		if (openedFile != null) {
			openFile(openedFile);
		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		populateOpenRecent();

		tree.setRoot(new TreeItem<>());
		tree.setCellFactory(param -> new TextFieldTreeCellImpl());
		tree.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> updateAttributes(newValue));

		attributeName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
		attributeValue.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().toString()));
	}

	private void populateOpenRecent() {
		ObservableList<MenuItem> recents = FXCollections.observableArrayList();
		recents.add(new MenuItem("Hello"));
		recents.add(new MenuItem("Hello2"));
		recents.add(new MenuItem("Hello3"));

		openRecent.getItems().setAll(recents);
	}

	private void openFile(File file) {
		try {
			HdfFile hdfFile = new HdfFile(file);
			TreeItem<Node> fileRoot = new io.jhdf.fx.tree.HdfTreeItem(hdfFile);
			tree.getRoot().getChildren().add(fileRoot);
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, "Failed to open file: " + file.getAbsolutePath());
			alert.showAndWait();
		}
	}

	private void updateAttributes(TreeItem<Node> item) {
		ObservableList<Map.Entry<String, AttributeMessage>> items = FXCollections
				.observableArrayList(item.getValue().getAttributes().entrySet());
		attributeTable.setItems(items);

		nameField.setText(item.getValue().getName());
		pathField.setText(item.getValue().getPath());
		typeField.setText(item.getValue().getType());

		fileStatus.setText(item.getValue().getFile().getAbsolutePath());
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

}
