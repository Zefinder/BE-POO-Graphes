package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;

public class Label {

	private Node currentNode;
	
	private boolean mark;
	
	private double cost;
	
	private Node parent;
	
	public Label() {
		// TODO Auto-generated constructor stub
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(Node currentNode) {
		this.currentNode = currentNode;
	}

	public boolean isMark() {
		return mark;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	

}
