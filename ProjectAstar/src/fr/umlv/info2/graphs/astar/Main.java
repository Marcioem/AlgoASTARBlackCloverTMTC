package fr.umlv.info2.graphs.astar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Main {

	private static void usage() {
		System.out.println("Usage : Main fileCo fileGr [indexFirstVertex] [indexSecondVertex]");
	}

	private static Graph parserGraph(Path fileCoName, Path fileGrName){
		try (
			 BufferedReader brCo = Files.newBufferedReader(fileCoName, StandardCharsets.UTF_8);
			 BufferedReader brGr = Files.newBufferedReader(fileGrName, StandardCharsets.UTF_8);
				) {
			String line;
			Graph g = null;
			while ((line = brCo.readLine()) != null) {
				String[] splittedLine = line.split(" ");
				switch(splittedLine[0]) {
				case "c":
					//Commentary
					break;
				case "p":
					g = new AdjGraph(Integer.parseInt(splittedLine[4]));
					break;
				case "v":
					if(g==null) {
						throw new IllegalStateException("Intent of add a vertex without initialize the graph.");
					}
					g.addVertex(Integer.parseInt(splittedLine[1])-1, Integer.parseInt(splittedLine[2]), Integer.parseInt(splittedLine[3]));
					break;
				default:
					throw new IllegalStateException("Illegal letter in the .co file.");
				}
			}
			if(g==null) {
				throw new IllegalStateException("There's not a graph in the .co file.");
			}

			while ((line = brGr.readLine()) != null) {
				String[] splittedLine = line.split(" ");
				switch(splittedLine[0]) {
				case "c":
					//Commentary
					break;
				case "p":
					//Number of nodes and arcs 
					break;
				case "a":
					g.addEdge(Integer.parseInt(splittedLine[1])-1, Integer.parseInt(splittedLine[2])-1, Integer.parseInt(splittedLine[3]));
					break;
				default:
					throw new IllegalStateException("Illegal letter in the .gr file.");
				}
			}
			return g;
		}catch(IOException e ) {
			throw new UncheckedIOException(e);
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			usage();
			return;
		}

		String fileCoName = args[0] + ".co";
		String fileGrName = args[1] + ".gr";

		Graph g = parserGraph(Paths.get(fileCoName), Paths.get(fileGrName));
		int indexFirstVertex;
		int indexSecondVertex;
		
		if(args.length >=4) {
			indexFirstVertex = Integer.parseInt(args[2])-1;
			indexSecondVertex = Integer.parseInt(args[3])-1;
		}else{
			indexFirstVertex = 0;
			indexSecondVertex = g.numberOfVertices()-1;
		}
		Optional<ShortestPathFromOneVertex> spfov = Graphs.astar(g, indexFirstVertex, indexSecondVertex);
		if(!spfov.isEmpty()) {
			spfov.get().printShortestPathTo(indexSecondVertex);
			return;
		}
		System.out.println("vertex " + indexSecondVertex + " is not accessible by the vertex :" + indexFirstVertex);
	}
}
