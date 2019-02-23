package io.jhdf.fx;

import org.apache.commons.lang3.ArrayUtils;

import io.jhdf.api.Dataset;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DatasetDialogController {

	@FXML
	TextArea dataTextField;
	@FXML
	TextField dimensionsField;
	@FXML
	TextField maxDimensionsField;
	@FXML
	TextField dataTypeField;
	@FXML
	TextArea dataText;

	public void setDataset(Dataset dataset) {

		dataTextField.setText(ArrayUtils.toString(dataset.getData()));

		dimensionsField.setText(ArrayUtils.toString(dataset.getDimensions()));
		maxDimensionsField.setText(ArrayUtils.toString(dataset.getMaxSize()));
		dataTypeField.setText(dataset.getJavaType().getSimpleName());
	}

}
