package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.algorithm.utils.EmptyPriorityQueueException;
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
		Node origin = graph.get(data.getOrigin().getId());
		int unmarkedNodes = nbNodes;

		// Notify observers about the first event (origin processed).
		notifyOriginProcessed(data.getOrigin());

		for (int i = 0; i < nbNodes; i++) {
			// On ajoute tous les nodes à notre HashMap avec un coût infini
			labelMap.put(graph.get(i), new Label(graph.get(i), false, Double.POSITIVE_INFINITY, null));
		}

		// On met l'origine à un coût de 0 et on l'ajoute à notre file
		labelMap.get(origin).setCost(0);
		priorityQueue.insert(origin);

		Node currentNode = origin;
		while (unmarkedNodes > 0) {
			try {
				// On récupère le premier noeud de la file et on le marque
				currentNode = priorityQueue.deleteMin();
				labelMap.get(currentNode).mark();
				unmarkedNodes--;

				// Pour tous les arcs qui le relient...
				for (Arc successor : currentNode.getSuccessors()) {
					// Labels des deux noeuds concernés pour y accéder facilement
					Label currentNodeLabel = labelMap.get(currentNode);
					Label nextNodeLabel = labelMap.get(successor.getDestination());

					// ... s'il est déjà marqué on l'ignore
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
			} catch (EmptyPriorityQueueException e) {
				System.out.println("Empty BinaryHeap, engaging next step !");
				break;
			}
		}

		// Construction du ShortestPath
		// On part du noeud qu'on veut (destination) et on remonte jusqu'à l'origine
		ArrayList<Arc> arcs = new ArrayList<>();

		Arc father = labelMap.get(data.getDestination()).getFather();
		while (father != null) {
			arcs.add(father);
			father = labelMap.get(father.getOrigin()).getFather();
		}

		Collections.reverse(arcs);
		Path path = new Path(graph, arcs);
		solution = new ShortestPathSolution(data, Status.OPTIMAL, path);

		return solution;
	}

}
