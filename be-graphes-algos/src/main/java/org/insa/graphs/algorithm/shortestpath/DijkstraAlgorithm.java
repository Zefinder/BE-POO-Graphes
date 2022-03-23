package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

	private HashMap<Node, Label> labelMap;

	private BinaryHeap<Node> priorityQueue;

	public DijkstraAlgorithm(ShortestPathData data) {
		super(data);
		labelMap = new HashMap<Node, Label>();
		priorityQueue = new BinaryHeap<>();
	}

	@Override
	protected ShortestPathSolution doRun() {
		final ShortestPathData data = getInputData();
		ShortestPathSolution solution = null;
		// TODO:
		Graph graph = data.getGraph();
		final int nbNodes = graph.size();
		Node origin = data.getOrigin();
		int unmarkedNodes = nbNodes;

		// Notify observers about the first event (origin processed).
		notifyOriginProcessed(data.getOrigin());

		for (int i = 0; i < nbNodes; i++) {
			labelMap.put(graph.get(i), new Label(graph.get(i), false, Double.POSITIVE_INFINITY, null));
		}

		labelMap.get(origin).setCost(0);
		priorityQueue.insert(origin);

		Node currentNode = origin;
		while (unmarkedNodes > 0) {
			currentNode = priorityQueue.findMin();
			labelMap.get(currentNode).mark();
			unmarkedNodes--;
			for (Arc successor : currentNode.getSuccessors()) {
				// Labels des deux noeuds concernés pour y accéder facilement
				Label currentNodeLabel = labelMap.get(currentNode);
				Label nextNodeLabel = labelMap.get(successor.getDestination());

				if (!nextNodeLabel.isMarked()) {
					// Le coût du noeud suivant est le minimum entre son coût actuel et le coût du
					// noeud actuel + le coût de l'arc
					if (currentNodeLabel.getCost() + successor.getMinimumTravelTime() < nextNodeLabel.getCost()) {
						nextNodeLabel.setCost(currentNodeLabel.getCost() + successor.getMinimumTravelTime());

						// Si le coût minimum a été modifié, le noeud actuel fait partie du chemin le
						// plus court donc il faut le mettre en père du noeud suivant
						try {
							priorityQueue.remove(nextNodeLabel.getCurrentNode());
							priorityQueue.insert(nextNodeLabel.getCurrentNode());

						} catch (ElementNotFoundException e) {
							priorityQueue.insert(nextNodeLabel.getCurrentNode());
						}

						nextNodeLabel.setFather(successor);

					}
				}
			}
		}

		// Construction du ShortestPath
		ArrayList<Arc> arcs = new ArrayList<>();
		// On parcourt les noeuds dans le bon ordre et ajoute leurs arcs pères
		for (int i = 0; i < priorityQueue.size(); i++) {
			Arc arc = labelMap.get(priorityQueue.deleteMin()).getFather();
			if (arc != null)
				arcs.add(labelMap.get(priorityQueue.deleteMin()).getFather());
		}

		Collections.reverse(arcs);
		Path path = new Path(graph, arcs);
		solution = new ShortestPathSolution(data, Status.OPTIMAL, path);

		return solution;
	}

}
