package fr.umlv.info2.graphs.astar;

public class Edge {
	private final int start;
	private final int end;
	private final int value;

	
	public Edge(int start, int end, int value) {
		this.start = start;
		this.end = end;
		this.value = value;
	}

	public Edge(int start, int end) {
		this(start, end ,1);
	}
	
	public int getValue() {
		return value;
	}
	
	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}


	@Override
	public String toString() {
		return start + " -- " + end + " ( " + value + " )";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Edge)) {
			return false;
		}
		Edge tmp = (Edge) obj;
		return tmp.start == start && tmp.end == end && tmp.value == value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + start;
		result = prime * result + value;
		return result;
	}
}
