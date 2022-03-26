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

	private BinaryHeap<Label> priorityQueue;

	public DijkstraAlgorithm(ShortestPathData data) {
		super(data);
		labelMap = new HashMap<Node, Label>();
		priorityQueue = new BinaryHeap<Label>();
	}

	@Override
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

		for (int i = 0; i < nbNodes; i++) {
			// On ajoute tous les nodes à notre HashMap avec un coût infini
			labelMap.put(graph.get(i), new Label(graph.get(i), false, Float.POSITIVE_INFINITY, null));
		}

		// On met l'origine à un coût de 0 et on l'ajoute à notre file
		labelMap.get(origin).setCost(0);
		priorityQueue.insert(labelMap.get(origin));

		Label currentNodeLabel = labelMap.get(origin);
		while (unmarkedNodes > 0 && !found) {
			try {
				// On récupère le premier noeud de la file et on le marque
				currentNodeLabel = priorityQueue.deleteMin();
				currentNodeLabel.mark();
				unmarkedNodes--;

				// Si on est sur la destination, on peut partir
				if (currentNodeLabel.getCurrentNode().equals(destination)) {
					found = true;
				} else {
					Label nextNodeLabel = null;
					// Pour tous les arcs qui le relient...
					for (Arc successor : currentNodeLabel.getCurrentNode().getSuccessors()) {
						// Labels des deux noeuds concernés pour y accéder facilement
						nextNodeLabel = labelMap.get(successor.getDestination());

						// ... s'il est déjà marqué on l'ignore
						if (!nextNodeLabel.isMarked()) {
							
							double oldDistance = nextNodeLabel.getCost();
							double newDistance = currentNodeLabel.getCost() + successor.getLength();
							
		                    if (Double.isInfinite(oldDistance) && Double.isFinite(newDistance)) {
		                        notifyNodeReached(successor.getDestination());
		                    }
							
							// Le coût du noeud suivant est le minimum entre son coût actuel et le coût du
							// noeud actuel + le coût de l'arc
							if (newDistance < oldDistance) {

								// Si le coût minimum a été modifié, le noeud actuel fait partie du chemin le
								// plus court donc il faut le mettre en père du noeud suivant
								try {
									priorityQueue.remove(nextNodeLabel);

								} catch (ElementNotFoundException e) {

								}
								nextNodeLabel.setCost(newDistance);
								nextNodeLabel.setFather(successor);
								priorityQueue.insert(nextNodeLabel);

							}
						}
					}
				}

			} catch (EmptyPriorityQueueException e) {
				System.out.println("Empty BinaryHeap, engaging next step !");
				break;
			}
		}

        // The destination has been found, notify the observers.
        notifyDestinationReached(data.getDestination());
        
		// Construction du ShortestPath
		// On part du noeud qu'on veut (destination) et on remonte jusqu'à l'origine
		ArrayList<Arc> arcs = new ArrayList<>();

		Arc father = labelMap.get(destination).getFather();
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
