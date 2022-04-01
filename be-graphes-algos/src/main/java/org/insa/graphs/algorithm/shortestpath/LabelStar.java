package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class LabelStar extends Label implements Comparable<Label>{

	private double estimatedCost;
	
	public LabelStar(Node currentNode, boolean mark, double cost, Arc father, double estimatedCost) {
		super(currentNode, mark, cost, father);
		this.setEstimatedCost(estimatedCost);
	}

	public double getEstimatedCost() {
		return estimatedCost;
	}

	public void setEstimatedCost(double estimatedCost) {
		this.estimatedCost = estimatedCost;
	}
	
	@Override
	public double getTotalCost() {
		return getCost() + getEstimatedCost();
	}

}
