package io.jhdf.fx;

import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class TextFieldTreeCellImpl extends TreeCell<Node> {

	private static final int ICON_SIZE = 20;

	private final ImageView fileIcon = new ImageView(
			new Image(getClass().getResourceAsStream("icons/hdf5.png"), ICON_SIZE, ICON_SIZE, true, true));

	private final ImageView groupIcon = new ImageView(
			new Image(getClass().getResourceAsStream("icons/folder.png"), ICON_SIZE, ICON_SIZE, true, true));

	private final ImageView dataIcon = new ImageView(
			new Image(getClass().getResourceAsStream("icons/data.png"), ICON_SIZE, ICON_SIZE, true, true));

	@Override
	public void updateItem(Node item, boolean empty) {
		super.updateItem(item, empty);

		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			setText(item.getName());
			if (item instanceof HdfFile) {
				setGraphic(fileIcon);
			} else if (item instanceof Group) {
				setGraphic(groupIcon);
			} else if (item instanceof Dataset) {
				setGraphic(dataIcon);
			} else {
				setGraphic(null);
				System.out.println(item);
			}
		}
	}

}
