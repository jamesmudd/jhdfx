package io.jhdf.fx.tree;

import java.util.Collection;

import io.jhdf.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class HdfTreeItem extends TreeItem<Node> {

	public HdfTreeItem(Node node) {
		super(node);
	}

	// We cache whether the File is a leaf or not. A File is a leaf if
	// it is not a directory and does not have any files contained within
	// it. We cache this as isLeaf() is called often, and doing the
	// actual check on File is expensive.
	private boolean isLeaf;

	// We do the children and leaf testing only once, and then set these
	// booleans to false so that we do not check again during this
	// run. A more complete implementation may need to handle more
	// dynamic file system situations (such as where a folder has files
	// added after the TreeView is shown). Again, this is left as an
	// exercise for the reader.
	private boolean isFirstTimeChildren = true;
	private boolean isFirstTimeLeaf = true;

	@Override
	public ObservableList<TreeItem<Node>> getChildren() {
		if (isFirstTimeChildren) {
			isFirstTimeChildren = false;

			// First getChildren() call, so we actually go off and
			// determine the children of the File contained in this TreeItem.
			super.getChildren().setAll(buildChildren(this));
		}
		return super.getChildren();
	}

	@Override
	public boolean isLeaf() {
		if (isFirstTimeLeaf) {
			isFirstTimeLeaf = false;
			Node f = getValue();
			isLeaf = !f.isGroup();
		}

		return isLeaf;
	}

	private ObservableList<TreeItem<Node>> buildChildren(HdfTreeItem item) {
		Node f = item.getValue();
		if (f != null && f.isGroup()) {
			Collection<Node> files = f.getChildren().values();
			if (files != null) {
				ObservableList<TreeItem<Node>> children = FXCollections.observableArrayList();

				for (Node childFile : files) {
					children.add(createNode(childFile));
				}

				return children;
			}
		}

		return FXCollections.emptyObservableList();
	}

	private TreeItem<Node> createNode(Node childFile) {
		return new HdfTreeItem(childFile);
	}

	@Override
	public String toString() {
		return getValue().getName();
	}

}
