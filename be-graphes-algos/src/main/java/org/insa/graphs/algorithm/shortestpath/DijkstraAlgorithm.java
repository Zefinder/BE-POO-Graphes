package org.insa.graphs.algorithm.shortestpath;

import java.util.HashMap;

import org.insa.graphs.model.Node;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

	private HashMap<Node, Label> labelMap;
	
    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
        labelMap = new HashMap<Node, Label>();
    }

    @Override
    protected ShortestPathSolution doRun() {
        final ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;
        // TODO:
        
        return solution;
    }

}
