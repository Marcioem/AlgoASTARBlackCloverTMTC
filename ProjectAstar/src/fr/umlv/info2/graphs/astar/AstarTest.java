package fr.umlv.info2.graphs.astar;


import org.junit.jupiter.api.Test;

class AstarTest {

	@Test
	void astarTest() {
		AdjGraph m = new AdjGraph(6);
		m.addEdge(0, 1, 1);
		m.addEdge(0, 3, 1);
		m.addEdge(0, 4, 1);
		m.addEdge(4, 5, 1);
		m.addEdge(4, 2, 1);
		System.out.println(Graphs.astar(m, 1, 2).get());

	}

}
