package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.algorithm.utils.EmptyPriorityQueueException;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.Point;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

	// On crée une liste qui contiendra soit Label soit LabelStar !
	// Attention, ? signifie qu'on ne peut rien ajouter ! (d'où la fonction)
	private ArrayList<? extends Label> labelMap;

	private BinaryHeap<Label> priorityQueue;

	public DijkstraAlgorithm(ShortestPathData data) {
		super(data);
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

		this.labelMap = registerNodes(data);

		// On met l'origine à un coût de 0 et on l'ajoute à notre file
		labelMap.get(origin.getId()).setCost(0);
		priorityQueue.insert(labelMap.get(origin.getId()));

		Label currentNodeLabel = labelMap.get(origin.getId());
		while (unmarkedNodes > 0 && !found && !priorityQueue.isEmpty()) {
			// On récupère le premier noeud de la file et on le marque
			currentNodeLabel = priorityQueue.deleteMin();
			currentNodeLabel.mark();
			notifyNodeMarked(currentNodeLabel.getCurrentNode());
			unmarkedNodes--;

			// Si on est sur la destination, on peut partir
			if (currentNodeLabel.getCurrentNode().equals(destination)) {
				found = true;
			} else {
				Label nextNodeLabel = null;
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

								// Si le coût minimum a été modifié, le noeud actuel fait partie du chemin le
								// plus court donc il faut le mettre en père du noeud suivant
								try {
									priorityQueue.remove(nextNodeLabel);

								} catch (ElementNotFoundException e) {

								}
								nextNodeLabel.setCost(neww);
								nextNodeLabel.setFather(successor);
								priorityQueue.insert(nextNodeLabel);

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

	public ArrayList<? extends Label> registerNodes(ShortestPathData data) {
		int nbNodes = data.getGraph().size();
		ArrayList<Label> labelMap = new ArrayList<Label>();
		for (int i = 0; i < nbNodes; i++) {
			// On ajoute tous les nodes à notre HashMap avec un coût infini
			labelMap.add(new Label(data.getGraph().get(i), false, Double.POSITIVE_INFINITY, null));
		}

		return labelMap;
	}

}
