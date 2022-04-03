package org.insa.graphs.algorithm.shortestpath;

public class AStarTest extends ShortestPathTest {

	@Override
	public ShortestPathAlgorithm setAlgorithm() {
		// On a juste besoin de la classe
		return new AStarAlgorithm(null);
	}

}
