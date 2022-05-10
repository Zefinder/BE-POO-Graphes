package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Collections;

import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.RoadInformation.RoadType;

public class ElectricCarAlgorithm extends ShortestPathAlgorithm {

	private ArrayList<ArrayList<LabelElectric>> tableauDeTableau;
	private ArrayList<LabelElectric> labelMap;
	private BinaryHeap<LabelElectric> priorityQueue;
	private int maxProf;

	public ElectricCarAlgorithm(ShortestPathData data) {
		super(data);
		maxProf = 0;
	}

	/*
	 * @Override protected ShortestPathSolution doRun() {
	 * JOptionPane.showMessageDialog(null, "En grève du travail"); final
	 * ShortestPathData data = getInputData(); ShortestPathSolution solution = null;
	 * solution = new ShortestPathSolution(data, Status.INFEASIBLE); return
	 * solution; }
	 */

	protected ShortestPathSolution doRun() {
		final ShortestPathData data = getInputData();
		ShortestPathSolution solution = null;

		// Création des variables qui vont servir
		Graph graph = data.getGraph();
		final int nbNodes = graph.size();
		Node origin = graph.get(data.getOrigin().getId());
		Node destination = graph.get(data.getDestination().getId());
		int unmarkedNodes = nbNodes;
		boolean found = false;

		// Notify observers about the first event (origin processed).
		notifyOriginProcessed(data.getOrigin());

		this.labelMap = registerNodes(data);

		// On met l'origine à un coût de 0 et on l'ajoute à notre file
		labelMap.get(origin.getId()).setCost(0);
		priorityQueue.insert(labelMap.get(origin.getId()));

		LabelElectric currentNodeLabel = labelMap.get(origin.getId());
		while (unmarkedNodes > 0 && !found && !priorityQueue.isEmpty()) {
			// On récupère le premier noeud de la file et on le marque
			currentNodeLabel = priorityQueue.deleteMin();

			currentNodeLabel.mark();
			
			// Si on est supérieurs à 200, on se casse
			if (currentNodeLabel.getDistanceEtape() > 200) {
				continue;
			}

			notifyNodeMarked(currentNodeLabel.getCurrentNode());
			unmarkedNodes--;

			// Si on est sur la destination, on peut partir
			if (currentNodeLabel.getCurrentNode().equals(destination)) {
				found = true;
			} else {
				LabelElectric nextNodeLabel = null;
				// Pour tous les arcs qui le relient...
				for (Arc successor : currentNodeLabel.getCurrentNode().getSuccessors()) {

					if (data.isAllowed(successor)) {

						// Labels des deux noeuds concernés pour y accéder facilement
						nextNodeLabel = labelMap.get(successor.getDestination().getId());
						

						// ... s'il est déjà marqué on l'ignore
						if (!nextNodeLabel.isMarked()) {
							
							double old = nextNodeLabel.getCost();
							double neww = currentNodeLabel.getCost() + data.getCost(successor);

							if (Double.isInfinite(old) && Double.isFinite(neww)) {
								notifyNodeReached(successor.getDestination());
							}

							// Le coût du noeud suivant est le minimum entre son coût actuel et le coût du
							// noeud actuel + le coût de l'arc
							if (neww < old) {
								
								// On ajoute le coût du noeud à l'étape
								nextNodeLabel.addDistanceEtape(data.getCost(successor));

								// Si le coût minimum a été modifié, le noeud actuel fait partie du chemin le
								// plus court donc il faut le mettre en père du noeud suivant
								try {
									priorityQueue.remove(nextNodeLabel);

								} catch (ElementNotFoundException e) {

								}
								nextNodeLabel.setCost(neww);
								nextNodeLabel.setFather(successor);
								priorityQueue.insert(nextNodeLabel);

								// Si c'est une autoroute (et donc il y a une borne de recharge),
								// On duplique le noeud pour envisager les deux cas :
								// 1. On a continué sans recharger
								// 2. On a continué après recharge
								if (successor.getRoadInformation().getType() == RoadType.MOTORWAY) {
									LabelElectric newNextNodeLabel = (LabelElectric) nextNodeLabel.clone();

									// On remet l'autonomie à 200km
									newNextNodeLabel.setDistanceEtape(0);

									// Si on est en temps, il faut prendre le temps de recharge en compte
									if (data.getMode() == Mode.TIME) {
										newNextNodeLabel.setEstimatedCost(newNextNodeLabel.getCost() + 600);
									}

									// On distingue le noeud de l'ancien
									newNextNodeLabel.incProfondeur();

									priorityQueue.insert(newNextNodeLabel);
								}
							}
						}
					}
				}
			}
		}

		// The destination has been found, notify the observers.
		notifyDestinationReached(data.getDestination());

		// Construction du ShortestPath
		// On part du noeud qu'on veut (destination) et on remonte jusqu'à l'origine
		ArrayList<Arc> arcs = new ArrayList<>();

		Node current = destination;
		Arc father = labelMap.get(destination.getId()).getFather();
		while (father != null) {
			arcs.add(father);
			current = father.getOrigin();
			father = labelMap.get(current.getId()).getFather();
		}

		if (!current.equals(origin)) {
			solution = new ShortestPathSolution(data, Status.INFEASIBLE);
		} else {

			Collections.reverse(arcs);
			Path path = new Path(graph, arcs);
			solution = new ShortestPathSolution(data, Status.OPTIMAL, path);

		}

		return solution;
	}

	private ArrayList<LabelElectric> registerNodes(ShortestPathData data) {
		Graph graph = data.getGraph();
		int nbNodes = graph.size();

		ArrayList<LabelElectric> labelMap = new ArrayList<LabelElectric>();
		double time = (data.getMode() == Mode.TIME ? graph.getGraphInformation().getMaximumSpeed() / 3.6 : 1);

		for (int i = 0; i < nbNodes; i++) {
			labelMap.add(new LabelElectric(graph.get(i), false, Double.POSITIVE_INFINITY, null,
					graph.get(i).getPoint().distanceTo(data.getDestination().getPoint()) / time));
		}

		return labelMap;
	}

}
