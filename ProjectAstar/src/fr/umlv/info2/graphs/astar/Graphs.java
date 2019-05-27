package fr.umlv.info2.graphs.astar;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.atomic.LongAdder;

public class Graphs {
	public static List<Integer> DFS(Graph g) {
		List<Integer> list = new ArrayList<>();
		boolean[] tab = new boolean[g.numberOfVertices()];
		for(int i=0; i<g.numberOfVertices(); i++) {
			if(!tab[i]) {
				DFSRec(g, i, tab, list);
			}
		}
		return list;
	}

	private static void DFSRec(Graph g, int i, boolean[] tab, List<Integer> list) {
		tab[i] = true;
		list.add(i);
		g.forEachEdge(i, (e) -> {
			if (!tab[e.getEnd()]) DFSRec(g, e.getEnd(), tab, list);
		});
	}

	public static List<Integer> BFS(Graph g) {
		List<Integer> list = new ArrayList<>();
		ArrayDeque<Integer> file = new ArrayDeque<Integer>();
		boolean[] tab = new boolean[g.numberOfVertices()];
		for(int i=0; i<g.numberOfVertices(); i++) {
			if(!tab[i]) {
				BFSRec(g, i, tab, list, file);
			}
		}
		return list;
	}

	private static void BFSRec(Graph g, int i, boolean[] tab, List<Integer> list, ArrayDeque<Integer> file) {
		file.addFirst(i);
		tab[i] = true;
		while(!file.isEmpty()) {
			int polled = file.removeLast();
			list.add(polled);
			g.forEachEdge(polled, (e) -> {
				if (!tab[e.getEnd()]) {
					file.addFirst(e.getEnd());
					tab[e.getEnd()] = true;
				}
			});
		}
	}

	public static int[][] timedDepthFirstSearch(Graph g, int s0) {
		var nbVertices = g.numberOfVertices();
		var tab = new int[nbVertices][2];
		var adder = new LongAdder();
		var passed = new boolean[nbVertices];

		passed[s0] = true;
		tab[s0][0] = adder.intValue();
		adder.increment();
		g.forEachEdge(s0, (e) -> {
			if (!passed[e.getEnd()]) {
				timedDepthFirstRec(g, e.getEnd(), passed, adder, tab);
			}
		});
		tab[s0][1] = adder.intValue();
		adder.increment();

		for (int i=0; i<nbVertices; i++) {
			if (!passed[i]) {
				passed[i] = true;
				tab[i][0] = adder.intValue();
				adder.increment();
				g.forEachEdge(i, (e) -> {
					if (!passed[e.getEnd()]) {
						timedDepthFirstRec(g, e.getEnd(), passed, adder, tab);
					}
				});
				tab[i][1] = adder.intValue();
				adder.increment();
			}
		}
		return tab;

	}

	private static void timedDepthFirstRec(Graph g, int i, boolean[] passed, LongAdder adder, int[][] tab) {
		passed[i] = true;
		tab[i][0] = adder.intValue();
		adder.increment();
		g.forEachEdge(i, (e) -> {
			if (!passed[e.getEnd()]) {
				timedDepthFirstRec(g, e.getEnd(), passed, adder, tab);
			}
		});
		tab[i][1] = adder.intValue();
		adder.increment();
	}

	public static List<Integer> topologicalSort(Graph g, boolean cycleDetect) {
		List<Integer> l = new ArrayList<>();
		boolean[] tab = new boolean[g.numberOfVertices()];
		for (int i=0; i<g.numberOfVertices(); i++) {
			if (!tab[i]) {
				topologicalSortNoCycle(g, i, tab, l);
			}
		}
		return l;
	}

	private static void topologicalSortNoCycle(Graph g, int i, boolean[] tab, List<Integer> l) {
		tab[i] = true;
		l.add(i);
		g.forEachEdge(i, (s) -> {
			if (!tab[s.getEnd()]) {
				topologicalSortNoCycle(g, s.getEnd(), tab, l);
			}
		});
	}

	public static ShortestPathFromOneVertex bellmanFord(Graph g, int source) {
		int vertices = g.numberOfVertices();
		var d = new int[vertices];
		var pi = new int[vertices];
		Arrays.fill(d, Integer.MAX_VALUE);
		Arrays.fill(pi, -1);
		d[source] = 0;

		for(int i=0; i<vertices; i++) {
			for(int j=0; j<vertices; j++) {
				g.forEachEdge(j, (s) -> {
					int end = s.getEnd();
					int start = s.getStart();
					int value = s.getValue();
					if ((d[start] + value) < d[end]) {
						d[end] = d[start]+value;
						pi[end] = start;
					}
				});
			}
		}	
		for (int j = 0; j< vertices; j++) {
			g.forEachEdge(j, (s) -> {
				int end = s.getEnd();
				int start = s.getStart();
				int value = s.getValue();
				if ((d[start] + value) < d[end]) {
					throw new IllegalStateException("Negative circuit found");
				}
			});
		}
		return new ShortestPathFromOneVertex(source, d, pi);
	}

	private static int getSmallestValue(List<Integer> nontreated, int[] d) {
		int small = Integer.MAX_VALUE;
		int index = 0;
		for(Integer i : nontreated) {
			if (d[i] < small) {
				index = i;
				small = d[i];
			}
		}
		nontreated.remove((Integer)index);
		return index;
	}
	
	private static int getSmallestValue(PriorityQueue<Integer> nontreated, int[] d) {
		int small = Integer.MAX_VALUE;
		int index = 0;
		for(Integer i : nontreated) {
			if (d[i] < small) {
				index = i;
				small = d[i];
			}
		}
		nontreated.remove((Integer)index);
		return index;
	}

	public static ShortestPathFromOneVertex dijkstra(Graph g, int source) {
		int nbVertices = g.numberOfVertices();
		var d = new int[nbVertices];
		var pi = new int[nbVertices];
		Arrays.fill(d, Integer.MAX_VALUE);
		Arrays.fill(pi, -1);
		d[source] = 0;

		List<Integer> nontreated = new ArrayList<Integer>();
		for (int i=0; i<nbVertices; i++) nontreated.add(i);

		while (!nontreated.isEmpty()) {
			int t = getSmallestValue(nontreated, d);
			g.forEachEdge(t, (s) -> {
				int end = s.getEnd();
				int start = s.getStart();
				int value = s.getValue();
				if ((d[start] + value) < d[end]) {
					d[end] = d[start]+value;
					pi[end] = start;
				}
			});
		}

		return new ShortestPathFromOneVertex(source, d, pi);
	}
	
	public static void initDistanceBetweenPoint(int[] h, int[][] coords, int dest) {
		int xDest = coords[dest][0], yDest = coords[dest][1];
		for (var x = 0 ; x < coords.length ; ++x)
			h[x] = (int) Math.sqrt(Math.pow(xDest - coords[x][0], 2) + Math.pow(yDest - coords[x][1], 2));
	}

	public static Optional<ShortestPathFromOneVertex> astar(Graph graph, int source, int dest) {
		int[][] coords = graph.getVerticesCoordinates();
		//creation of data structures
		int nbVertices = graph.numberOfVertices();
		var f = new int[nbVertices];
		var g = new int[nbVertices];
		var h = new int[nbVertices];
		var pi = new int[nbVertices];
		PriorityQueue<Integer> border = new PriorityQueue<Integer>();
		TreeMap<Integer, Integer> computed = new TreeMap<>();
		
		//Filling data structure
		initDistanceBetweenPoint(h, coords, dest);
		Arrays.fill(f, Integer.MAX_VALUE);
		Arrays.fill(g, Integer.MAX_VALUE);
		Arrays.fill(pi, -1);
		g[source] = 0;
		border.add(source);
		computed.put(source, source);
		 
		//processing
		int steps = 0;
		while (!border.isEmpty()) {
			steps++;
			int x = getSmallestValue(border, f);
			if (x == dest) {
				//vertex is accessible
				int result =  (int)Math.ceil(f[x] * 1.6);
				System.out.println("Astar: " + steps + " etapes. \n Chemin de lg."+ result +".");
				return Optional.of(new ShortestPathFromOneVertex(source, g, pi));
			}
			graph.forEachEdge(x, (edge) -> {
				int end = edge.getEnd();
				int value = edge.getValue();
				if (computed.containsKey(end)) {
					if (g[end] > g[x] + value) {
						g[end] = g[x]+value;
						pi[end] = x;
						f[end] = g[end] + h[end];
						if (!border.contains(end)) {
							border.add(end);
						}
					}
				} else {
					g[end] = g[x] + value;
					pi[end] = x;
					f[end] = g[end] + h[end];
					border.add(end);
					computed.put(end, end);
				}
			});
		}
		return Optional.empty();
	}
}
