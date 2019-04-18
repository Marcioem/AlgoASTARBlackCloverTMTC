package fr.umlv.info2.graphs.astar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShortestPathFromOneVertex {
	private final int source;
	private final int[] d;
	private final int[] pi;

	ShortestPathFromOneVertex(int source, int[] d, int[] pi) {
		this.source = source;
		this.d = d;
		this.pi = pi;
	}

	public void printShortestPathTo(int destination) {
		List<Integer> list = new ArrayList<>();
		int current = destination;
		while (current != source) {
			list.add(current);
			current = pi[current];
		}
		list.add(source);
		StringBuilder sb = new StringBuilder();
		for (int i=list.size()-1; i>=0; i--) {
			sb.append("("+list.get(i)+")---->");
		}
		sb.setLength(sb.length()-5);
		System.out.println(sb.toString());
	}

	public void printShortestPaths() {
		for (int i = 0; i < d.length; i++) {
			if (i == source) {
				continue;
			}
			printShortestPathTo(i);
		}
	}

	@Override
	public String toString() {
		return source + " " + Arrays.toString(d) + " " + Arrays.toString(pi);
	}
}
