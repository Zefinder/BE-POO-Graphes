package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;

import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.model.Graph;

public class AStarAlgorithm extends DijkstraAlgorithm {

	public AStarAlgorithm(ShortestPathData data) {
		super(data);
	}

	@Override
	public ArrayList<? extends Label> registerNodes(ShortestPathData data) {
		Graph graph = data.getGraph();
		int nbNodes = graph.size();
		
		ArrayList<LabelStar> labelMap = new ArrayList<LabelStar>();
		double time = (data.getMode() == Mode.TIME ? graph.getGraphInformation().getMaximumSpeed() / 3.6 : 1);


		for (int i = 0; i < nbNodes; i++) {
			labelMap.add(new LabelStar(graph.get(i), false, Double.POSITIVE_INFINITY, null,
					graph.get(i).getPoint().distanceTo(data.getDestination().getPoint()) / time));
		}

		return labelMap;
	}

	/*
	 * Pas besoin d'un doRun car il hérite de Dijkstra !
	 */

}
