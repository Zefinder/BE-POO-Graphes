package org.insa.graphs.algorithm.shortestpath;

import javax.swing.JOptionPane;

import org.insa.graphs.algorithm.AbstractSolution.Status;

public class ElectricCarAlgorithm extends ShortestPathAlgorithm {

	public ElectricCarAlgorithm(ShortestPathData data) {
		super(data);
	}

	@Override
	protected ShortestPathSolution doRun() {
		JOptionPane.showMessageDialog(null, "Non, c'est toujours pas prêt en fait");
		final ShortestPathData data = getInputData();
		ShortestPathSolution solution = null;
		solution = new ShortestPathSolution(data, Status.INFEASIBLE);
		return solution;
	}

}
