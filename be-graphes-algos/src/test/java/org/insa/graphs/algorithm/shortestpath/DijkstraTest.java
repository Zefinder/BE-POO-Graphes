package org.insa.graphs.algorithm.shortestpath;

public class DijkstraTest extends ShortestPathTest {

	@Override
	public ShortestPathAlgorithm setAlgorithm() {
		// On a juste besoin de la classe
		return new DijkstraAlgorithm(null);
	}	
	
}
