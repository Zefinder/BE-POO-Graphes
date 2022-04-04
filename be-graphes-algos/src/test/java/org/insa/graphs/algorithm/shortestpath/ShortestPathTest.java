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
	private ShortestPathAlgorithm algorithm;
	private ShortestPathSolution nullSolution, insaBikiniSolution, insaAeroportLengthSolution, insaAeroportTimeSolution;
	private Node origin, destination;
	private ArcInspector arcFilter;
	private ShortestPathData data;

	public abstract ShortestPathAlgorithm setAlgorithm();

	private ShortestPathSolution getSolution(Graph graph, Node origin, Node destination, ArcInspector arcFilter) {
		ShortestPathSolution solution;
		data = new ShortestPathData(graph, origin, destination, arcFilter);
		try {
			this.algorithm = (ShortestPathAlgorithm) AlgorithmFactory.createAlgorithm(algorithm.getClass(), data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		solution = algorithm.doRun();
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

		// Noeuds dans graphe non connexe
		origin = graph.get(137956);
		destination = graph.get(105620);
		// Toutes routes autorisées
		arcFilter = ArcInspectorFactory.getAllFilters().get(0);

		nullSolution = getSolution(graph, origin, destination, arcFilter);

		// Chemin INSA-Bikini
		String pathName = "C:\\Users\\adric\\Downloads\\path_fr31_insa_bikini_canal.path";
		insaBikiniPath = getPath(graph, pathName);

		origin = graph.get(10991);
		destination = graph.get(63104);
		// Toutes routes autorisées
		arcFilter = ArcInspectorFactory.getAllFilters().get(0);

		insaBikiniSolution = getSolution(graph, origin, destination, arcFilter);

		// Chemin INSA-Aéroport (longueur)
		pathName = "C:\\Users\\adric\\Downloads\\path_fr31_insa_aeroport_length.path";
		insaAeroportLengthPath = getPath(graph, pathName);

		origin = graph.get(10991);
		destination = graph.get(89149);
		// Routes que pour les voitures, length
		arcFilter = ArcInspectorFactory.getAllFilters().get(1);

		insaAeroportLengthSolution = getSolution(graph, origin, destination, arcFilter);

		// Chemin INSA-Aéroport (temps)
		pathName = "C:\\Users\\adric\\Downloads\\path_fr31_insa_aeroport_time.path";
		insaAeroportTimePath = getPath(graph, pathName);

		origin = graph.get(10991);
		destination = graph.get(89149);
		// Routes que pour les voitures, time
		arcFilter = ArcInspectorFactory.getAllFilters().get(2);

		insaAeroportTimeSolution = getSolution(graph, origin, destination, arcFilter);

	}

	// En anglais, un graphe connexe est un graphe connecté
	@Test
	public void testDisconnectedPath() {
		assertTrue(nullSolution.getStatus() == Status.INFEASIBLE);
	}

	@Test
	public void testInsaBikiniPath() {
		Assume.assumeNotNull(insaBikiniSolution);
		assertTrue(insaBikiniSolution.getStatus() == Status.OPTIMAL);
		assertEquals(insaBikiniPath.getArcs(), insaBikiniSolution.getPath().getArcs());
	}

	@Test
	public void testInsaAirportLength() {
		Assume.assumeNotNull(insaAeroportLengthSolution);
		assertTrue(insaAeroportLengthSolution.getStatus() == Status.OPTIMAL);
		assertEquals(insaAeroportLengthPath.getArcs(), insaAeroportLengthSolution.getPath().getArcs());
	}

	@Test
	public void testInsaAirportTime() {
		Assume.assumeNotNull(insaAeroportTimeSolution);
		assertTrue(insaAeroportTimeSolution.getStatus() == Status.OPTIMAL);
		assertEquals(insaAeroportTimePath.getArcs(), insaAeroportTimeSolution.getPath().getArcs());
	}

}
