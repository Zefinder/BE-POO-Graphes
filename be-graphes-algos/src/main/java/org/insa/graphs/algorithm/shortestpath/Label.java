package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class Label {

	private Node currentNode;

	private boolean mark;

	private double cost;

	private Arc father;

	public Label(Node currentNode, boolean mark, double cost, Arc father) {
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

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public Arc getFather() {
		return father;
	}

	public void setFather(Arc father) {
		this.father = father;
	}

}
