package fr.umlv.info2.graphs.astar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public interface Graph {
	public final static int maxGraph = 1000;
	public final static int minGraph = -1000;

	/**
	 * Le nombre d'arêtes du graphe.
	 * @return le nombre d'arêtes du graphe
	 */
	int numberOfEdges();

	/**
	 * Le nombre de sommets du graphe.
	 * @return le nombre de sommets du graphe
	 */
	int numberOfVertices();

	default void addEdge(int i, int j) {
		addEdge(i, j, 1);
	}

	/**
	 * Permet d'ajouter une arête orientée et pondérée au graphe.
	 * @param i la première extrémité de l'arête
	 * @param j la seconde extrémité de l'arête
	 * @param value le poids de l'arête
	 * @throws IndexOutOfBoundsException si i ou j n'est pas un sommet du graphe
	 * @throws IllegalArgumentException si value vaut 0 ou si il existe déjà une arête entre i et j
	 */
	void addEdge(int i, int j, int value);
	
	/**
	 * Permet d'ajouter un sommet à un index donné
	 * @param index du sommet
	 * @param lon longitude du sommet
	 * @param lat lattitude du sommet
	 * @throws IndexOutOfBoundsException si index n'est pas un index du graphe
	 */
	public void addVertex(int index, int lon, int lat);

	/**
	 * Teste l'existence d'une arête donnée
	 * @param i la première extrémité de l'arête
	 * @param j la seconde extrémité de l'arête
	 * @return true s'il existe une arête entre i et j; false sinon
	 * @throws IndexOutOfBoundsException si i ou j n'est pas un sommet du graphe
	 */
	boolean isEdge(int i, int j);

	/**
	 * Renvoie le poids d'une arête donnée.
	 * @param i la première extrémité de l'arête
	 * @param j la seconde extrémité de l'arête
	 * @return le poids de l'arête entre i et j
	 * @throws IndexOutOfBoundsException si i ou j n'est pas un sommet du graphe
	 */
	int getWeight(int i, int j);

	/**
	 * Un itérateur sur tous les voisins d'un sommet donné.
	 * @param i le sommet à partir duquel partent les arêtes fournies par l'itérateur
	 * @return un itérateur sur tous les voisins du sommet i
	 * @throws IndexOutOfBoundsException si i n'est pas un sommet du graphe
	 */
	Iterator<Edge> edgeIterator(int i);

	/**
	 * Effectue une action sur tous les voisins (les arêtes) d'un sommet donné.
	 * @param i le sommet à partir duquel partent les arêtes traitées
	 * @param consumer l'acction effectuée sur toutes les arêtes voisines de i
	 * @throws IndexOutOfBoundsException si i n'est pas un sommet du graphe
	 */
	void forEachEdge(int i, Consumer<Edge> consumer);
	
	Integer[][] getVerticesCoordinates();

	/**
	 * Fournit une réprésentaiuon du graphe au format .dot
	 * @return une réprésentaiuon du graphe au format .dot
	 */
	default String toGraphviz() {
		StringBuffer bf = new StringBuffer();
		bf.append("digraph G {\n\t").append(numberOfVertices() - 1).append(";\n");

		for (int i = 0; i < numberOfVertices(); i++) {
			final int fi = i;
			bf.append("\t").append(fi).append(";\n");

			forEachEdge(i, neighbour -> bf.append("\t").append(fi).append("->").append(neighbour.getEnd())
					.append("[label =\"").append(neighbour.getValue()).append("\"] ;\n"));
		}

		bf.append("}\n");
		return bf.toString();
	}

	default void toGraphviz(Path path) {
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(toGraphviz());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Création d'un graphe aléatoire avec un nombre de sommets et d'arêtes fixé
	 * @param n nombre de sommets
	 * @param nbEdges nombre d'arêtes
	 * @param wmax poids maximal (en valeur absolue) sur les arêtes
	 * @param factory une méthode qui étant donné un nombre de sommets n, fabrique et renvoie yun graphe vide à n sommets
	 * @return un graphe aléatoire construit à l'aide de factory yant exactement n sommets et nbEdges arêtes
	 * @throws IllegalArgumentException si le nombre d'arêtes est trop élevé par rapport au nombre de sommets ou si le poids max est nul
	 */
	public static Graph makeRandomGraph(int n, int nbEdges, int wmax, IntFunction<Graph> factory){
		Graph g = factory.apply(n);
		if (nbEdges > n * n) {
			throw new IllegalArgumentException("Trop d'arêtes pour " + n + " sommets!");
		}
		if (wmax==0) {
			throw new IllegalArgumentException("Le poids max doit être non nul");
		}
		Random r = new Random();
		int v1, v2;
		for (int k = 0; k < nbEdges; k++) {
			do {
				v1 = r.nextInt(n);
				v2 = r.nextInt(n);

			} while (g.isEdge(v1, v2));
			if(r.nextBoolean()){
				g.addEdge(v1, v2, 1 + r.nextInt(wmax));
			}
			else{
				g.addEdge(v1, v2, -(1 + r.nextInt(wmax)));
			}	
		}
		return g;
	}

	/**
	 * Création d'un graphe à partir d'un fichier contenant le nombre de sommets et sa matrice
	 * @param path le chemin du fichier contenant la matrice du graphe
	 * @param factory une méthode qui étant donné un nombre de sommets n, fabrique et renvoie yun graphe vide à n sommets
	 * @return un graphe construit à l'aide de factory et dont les arêtes sont données dans le fihier indiqué dans path
	 * @throws IOException
	 */
	public static Graph makeGraphFromMatrixFile(Path path, IntFunction<Graph> factory) throws IOException {
		Graph g;

		try (BufferedReader reader = Files.newBufferedReader(path)) {
			int size = Integer.parseInt(reader.readLine());
			g = factory.apply(size);

			for (int i = 0; i < size; i++) {
				String[] s = reader.readLine().split("\\s+");
				for (int j = 0; j < size; j++) {
					int w = Integer.parseInt(s[j]);
					if (w != 0) {
						g.addEdge(i, j, w);
					}
				}
			}
		}

		return g;
	}

	// fabrique un graphe vide de la même taille.
	public Graph newGraphOfSameSize();

	default public Graph transpose() {
		Graph t = newGraphOfSameSize();
		IntStream.range(0, numberOfVertices()).forEach(i -> {
			forEachEdge(i, neighbour -> {
				t.addEdge(neighbour.getEnd(), i, neighbour.getValue());
			});
		});
		return t;
	}

}