package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;

import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Point;

public class AStarAlgorithm extends DijkstraAlgorithm {

	public AStarAlgorithm(ShortestPathData data) {
		super(data);
	}

	@Override
	public ArrayList<? extends Label> registerNodes(ShortestPathData data) {
		Graph graph = data.getGraph();
		int nbNodes = graph.size();
		ArrayList<LabelStar> labelMap = new ArrayList<LabelStar>();
		for (int i = 0; i < nbNodes; i++) {
			labelMap.add(new LabelStar(graph.get(i), false, Double.POSITIVE_INFINITY, null,
					Point.distance(graph.get(i).getPoint(), data.getDestination().getPoint())));
		}

		return labelMap;
	}

	/*
	 * Pas besoin d'un doRun car il hérite de Dijkstra !
	 */

}
