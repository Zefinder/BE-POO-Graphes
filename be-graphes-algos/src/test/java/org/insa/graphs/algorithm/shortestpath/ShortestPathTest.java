package org.insa.graphs.algorithm.shortestpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.AlgorithmFactory;
import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.BinaryPathReader;
import org.insa.graphs.model.io.GraphReader;
import org.insa.graphs.model.io.PathReader;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public abstract class ShortestPathTest {

	private Graph graph;
	private Path insaBikiniPath, insaAeroportLengthPath, insaAeroportTimePath;
	private ShortestPathAlgorithm algorithm, algorithmBellman;
	private ShortestPathSolution nullSolution, sameNodeSolution, insaBikiniSolution, insaAeroportLengthSolution, insaAeroportTimeSolution;
	private ShortestPathSolution solutionsDij, solutionsAst, solutionsBell, solution;
	private Node origin, destination;
	private ArcInspector arcFilter;
	private ShortestPathData data;
	private ShortestPathAlgorithm autreAlgo;

	public abstract ShortestPathAlgorithm setAlgorithm();

	private ShortestPathSolution getSolution(ShortestPathAlgorithm algorithm, Graph graph, Node origin,
			Node destination, ArcInspector arcFilter) {
		ShortestPathSolution solution;
		data = new ShortestPathData(graph, origin, destination, arcFilter);
		try {
			this.algorithm = (ShortestPathAlgorithm) AlgorithmFactory.createAlgorithm(algorithm.getClass(), data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		solution = this.algorithm.doRun();
		return solution;
	}

	private Path getPath(Graph graph, String pathName) throws IOException {
		Path path;
		final PathReader pathReader = new BinaryPathReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(pathName))));
		path = pathReader.readPath(graph);
		return path;
	}

	@Before
	public void init() throws IOException {
		this.algorithm = setAlgorithm();

		String mapName = "C:\\Users\\adric\\Downloads\\haute-garonne.mapgr";
		final GraphReader reader = new BinaryGraphReader(
				new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

		graph = reader.read();
	}

	// En anglais, un graphe connexe est un graphe connecté
	@Test
	public void testDisconnectedPath() {
		// Noeuds dans graphe non connexe
		origin = graph.get(137956);
		destination = graph.get(105620);
		// Toutes routes autorisées
		arcFilter = ArcInspectorFactory.getAllFilters().get(0);

		nullSolution = getSolution(algorithm, graph, origin, destination, arcFilter);
		
		Assume.assumeNotNull(algorithm, nullSolution);
		assertTrue(nullSolution.getStatus() == Status.INFEASIBLE);
	}
	
	@Test
	public void testSameNodePath() {
		// Noeuds dans graphe non connexe
		origin = graph.get(20);
		destination = graph.get(20);
		// Toutes routes autorisées
		arcFilter = ArcInspectorFactory.getAllFilters().get(0);
		
		sameNodeSolution = getSolution(algorithm, graph, origin, destination, arcFilter);
		
		Assume.assumeNotNull(algorithm, sameNodeSolution);
		assertTrue(sameNodeSolution.getStatus() == Status.INFEASIBLE);
	}

	@Test
	public void testInsaBikiniPath() throws IOException {
		// Chemin INSA-Bikini
		String pathName = "C:\\Users\\adric\\Downloads\\path_fr31_insa_bikini_canal.path";
		insaBikiniPath = getPath(graph, pathName);

		origin = graph.get(10991);
		destination = graph.get(63104);
		// Toutes routes autorisées
		arcFilter = ArcInspectorFactory.getAllFilters().get(0);

		insaBikiniSolution = getSolution(algorithm, graph, origin, destination, arcFilter);
		
		Assume.assumeNotNull(algorithm, insaBikiniSolution);
		assertTrue(insaBikiniSolution.getStatus() == Status.OPTIMAL);
		assertEquals(insaBikiniPath.getArcs(), insaBikiniSolution.getPath().getArcs());
	}

	@Test
	public void testInsaAirportLength() throws IOException {
		// Chemin INSA-Aéroport (longueur)
		String pathName = "C:\\Users\\adric\\Downloads\\path_fr31_insa_aeroport_length.path";
		insaAeroportLengthPath = getPath(graph, pathName);

		origin = graph.get(10991);
		destination = graph.get(89149);
		// Routes que pour les voitures, length
		arcFilter = ArcInspectorFactory.getAllFilters().get(1);

		insaAeroportLengthSolution = getSolution(algorithm, graph, origin, destination, arcFilter);
		
		Assume.assumeNotNull(algorithm, insaAeroportLengthSolution);
		assertTrue(insaAeroportLengthSolution.getStatus() == Status.OPTIMAL);
		assertEquals(insaAeroportLengthPath.getArcs(), insaAeroportLengthSolution.getPath().getArcs());
	}

	@Test
	public void testInsaAirportTime() throws IOException {
		// Chemin INSA-Aéroport (temps)
		String pathName = "C:\\Users\\adric\\Downloads\\path_fr31_insa_aeroport_time.path";
		insaAeroportTimePath = getPath(graph, pathName);

		origin = graph.get(10991);
		destination = graph.get(89149);
		// Routes que pour les voitures, time
		arcFilter = ArcInspectorFactory.getAllFilters().get(2);

		insaAeroportTimeSolution = getSolution(algorithm, graph, origin, destination, arcFilter);
		
		Assume.assumeNotNull(algorithm, insaAeroportTimeSolution);
		assertTrue(insaAeroportTimeSolution.getStatus() == Status.OPTIMAL);
		assertEquals(insaAeroportTimePath.getArcs(), insaAeroportTimeSolution.getPath().getArcs());
	}

	@Test
	public void testRandomNodesDijAstar() {
		if (algorithm instanceof DijkstraAlgorithm) {
			this.autreAlgo = new AStarAlgorithm(null);
		} else if (algorithm instanceof AStarAlgorithm) {
			this.autreAlgo = new DijkstraAlgorithm(null);
		}
		
		// Prenons des Noeuds au hasard
		for (int i = 0; i < 25; i++) {
			generateRandomSolutionDijAstar();

			assertTrue(solutionsDij.getStatus() == Status.OPTIMAL);
			assertTrue(solutionsAst.getStatus() == Status.OPTIMAL);

			assertEquals(solutionsDij.getPath().getArcs(), solutionsAst.getPath().getArcs());
		}
	}

	private void generateRandomSolutionDijAstar() {
		do {
			origin = graph.get((int) Math.floor(Math.random() * graph.size()));
			destination = graph.get((int) Math.floor(Math.random() * graph.size()));

			arcFilter = ArcInspectorFactory.getAllFilters().get((int) Math.floor(Math.random() * 5));

			solutionsDij = getSolution(algorithm, graph, origin, destination, arcFilter);
			solutionsAst = getSolution(autreAlgo, graph, origin, destination, arcFilter);
		} while (solutionsDij.getStatus() == Status.INFEASIBLE || solutionsAst.getStatus() == Status.INFEASIBLE);
	}

	@Test
	public void testRandomNodesBellman() {
		this.algorithmBellman = new BellmanFordAlgorithm(null);
		
		// Prenons des Noeuds au hasard
		for (int i = 0; i < 5; i++) {
			generateRandomSolutionBellman();

			assertTrue(solutionsBell.getStatus() == Status.OPTIMAL);
			assertTrue(solution.getStatus() == Status.OPTIMAL);

			assertEquals(solution.getPath().getArcs(), solutionsBell.getPath().getArcs());
		}
	}
	
	private void generateRandomSolutionBellman() {
		do {
			origin = graph.get((int) Math.floor(Math.random() * graph.size()));
			destination = graph.get((int) Math.floor(Math.random() * graph.size()));

			arcFilter = ArcInspectorFactory.getAllFilters().get((int) Math.floor(Math.random() * 5));

			solution = getSolution(algorithm, graph, origin, destination, arcFilter);
			solutionsBell = getSolution(algorithmBellman, graph, origin, destination, arcFilter);
		} while (solution.getStatus() == Status.INFEASIBLE || solutionsBell.getStatus() == Status.INFEASIBLE);
	}
}
