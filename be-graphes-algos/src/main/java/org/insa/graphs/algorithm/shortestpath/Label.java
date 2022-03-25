package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class Label implements Comparable<Label> {

	private Node currentNode;

	private boolean mark;

	private float cost;

	private Arc father;

	public Label(Node currentNode, boolean mark, float cost, Arc father) {
		this.currentNode = currentNode;
		this.mark = mark;
		this.cost = cost;
		this.father = father;
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(Node currentNode) {
		this.currentNode = currentNode;
	}

	public boolean isMarked() {
		return mark;
	}

	public void mark() {
		this.mark = true;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public Arc getFather() {
		return father;
	}

	public void setFather(Arc father) {
		this.father = father;
	}

	@Override
	public int compareTo(Label o) {
		return Float.compare(getCost(), o.getCost());
	}

}
