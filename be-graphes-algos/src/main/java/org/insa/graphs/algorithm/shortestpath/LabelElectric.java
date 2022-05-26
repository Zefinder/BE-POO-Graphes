package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class LabelElectric implements Comparable<LabelElectric>, Cloneable {

	private double estimatedCost;
	private int profondeur;
	private double distanceEtape;
	private Node currentNode;

	private boolean mark;

	private double cost;

	private Arc father;
	
	private int indexTas = -1;
	
	public LabelElectric(Node currentNode, boolean mark, double cost, Arc father, double estimatedCost) {
		this.currentNode = currentNode;
		this.mark = mark;
		this.cost = cost;
		this.father = father;
		this.profondeur = 0;
		this.distanceEtape = 0;
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

	
	public double getEstimatedCost() {
		return estimatedCost;
	}

	public void setEstimatedCost(double estimatedCost) {
		this.estimatedCost = estimatedCost;
	}
	
	public int getIndexTas() {
		return indexTas;
	}

	public void setIndexTas(int indexTas) {
		this.indexTas = indexTas;
	}
	
	public double getTotalCost() {
		return getCost() + getEstimatedCost();
	}
	
	public void incProfondeur() {
		this.profondeur++;
	}
	
	public int getProfondeur() {
		return this.profondeur;
	}

	public double getDistanceEtape() {
		return distanceEtape;
	}

	public void setDistanceEtape(double distanceEtape) {
		this.distanceEtape = distanceEtape;
	}
	
	public void addDistanceEtape(double distance) {
		this.distanceEtape += distance/1000;
	}
	
	@Override
	public int compareTo(LabelElectric o) {
		return Double.compare(getTotalCost(), o.getTotalCost());
	}
	
	public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        } catch(CloneNotSupportedException cnse) {
            cnse.printStackTrace(System.err);
        }
        return o;
    }

}
