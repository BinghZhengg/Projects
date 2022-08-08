package cs1501_p4;

/******************************************************************************
 *  Compilation:  javac NetworkGraph.java
 *  Execution:    java NetworkGraph filename.txt
 *  Dependencies: ArrayList.java STE.java
 *
 *  An edge-weighted undirected graph, implemented using adjacency lists.
 *  Parallel edges and self-loops are permitted.
 *
 *  % java NetworkGraph network_data1.txt
 5
 0 2 optical 10000 10
 0 3 optical 10000 10
 1 2 optical 10000 10
 1 3 optical 10000 10
 0 4 copper 100 8
 1 4 copper 100 8
 2 4 copper 100 6
 3 4 copper 100 6
 *
 ******************************************************************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

//import cs1501_p4.STE;

/**
 *  The {@code NetworkGraph} class represents an edge-weighted
 *  graph of vertices named 0 through <em>V</em> \u2013 1, where each
 *  undirected STE is of type {@link STE} and has a real-valued weight.
 *  It supports the following two primary operations: add an STE to the graph,
 *  iterate over all of the edges incident to a vertex. It also provides
 *  methods for returning the degree of a vertex, the number of vertices
 *  <em>V</em> in the graph, and the number of edges <em>E</em> in the graph.
 *  Parallel edges and self-loops are permitted.
 *  By convention, a self-loop <em>v</em>-<em>v</em> appears in the
 *  adjacency list of <em>v</em> twice and contributes two to the degree
 *  of <em>v</em>.
 *  <p>
 *  This implementation uses an <em>adjacency-lists representation</em>, which
 *  is a vertex-indexed array of {@link ArrayList} objects.
 *  It uses &Theta;(<em>E</em> + <em>V</em>) space, where <em>E</em> is
 *  the number of edges and <em>V</em> is the number of vertices.
 *  All instance methods take &Theta;(1) time. (Though, iterating over
 *  the edges returned by {@link #adj(int)} takes time proportional
 *  to the degree of the vertex.)
 *  Constructing an empty edge-weighted graph with <em>V</em> vertices takes
 *  &Theta;(<em>V</em>) time; constructing a edge-weighted graph with
 *  <em>E</em> edges and <em>V</em> vertices takes
 *  &Theta;(<em>E</em> + <em>V</em>) time. 
 *  <p>
 *  For additional documentation,
 *  see <a href="https://algs4.cs.princeton.edu/43mst">Section 4.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 */
public class NetworkGraph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private int V;
    private int E;
    private ArrayList<STE>[] adj;
    
    /**
     * Initializes an empty edge-weighted graph with {@code V} vertices and 0 edges.
     *
     * @param  V the number of vertices
     * @throws IllegalArgumentException if {@code V < 0}
     */
    public NetworkGraph(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices must be non-negative");
        this.V = V;
        this.E = 0;
        adj = (ArrayList<STE>[]) new ArrayList[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new ArrayList<STE>();
        }
    }

    /**
     * Initializes a random edge-weighted graph with {@code V} vertices and <em>E</em> edges.
     *
     * @param  V the number of vertices
     * @param  E the number of edges
     * @throws IllegalArgumentException if {@code V < 0}
     * @throws IllegalArgumentException if {@code E < 0}

    public NetworkGraph(int V, int E) {
        this(V);
        if (E < 0) throw new IllegalArgumentException("Number of edges must be non-negative");
        for (int i = 0; i < E; i++) {
            int v = StdRandom.uniform(V);
            int w = StdRandom.uniform(V);
            double weight = Math.round(100 * StdRandom.uniform()) / 100.0;
            STE e = new STE(v, w, weight);
            addEdge(e);
        }
    }
     */
    /**  
     * Initializes an edge-weighted graph from an input stream.
     * The format is the number of vertices <em>V</em>,
     * followed by the number of edges <em>E</em>,
     * followed by <em>E</em> pairs of vertices and STE weights,
     * with each entry separated by whitespace.
     *
     * @param graphFile the input file
     * @throws IllegalArgumentException if {@code in} is {@code null}
     * @throws IllegalArgumentException if the endpoints of any STE are not in prescribed range
     * @throws IllegalArgumentException if the number of vertices or edges is negative
     */
    public NetworkGraph(String graphFile) {
        if (graphFile == null) throw new IllegalArgumentException("argument is null");
        try{
            System.out.println("current dir:" +System.getProperty("user.dir"));
            Scanner myReader = new Scanner(new File(graphFile));
            String aline = myReader.nextLine();
            this.V = Integer.parseInt(aline);
            adj = (ArrayList<STE>[]) new ArrayList[V];
            for (int v = 0; v < V; v++) {
                adj[v] = new ArrayList<STE>();
            }

            while (myReader.hasNextLine())
            {
                aline = myReader.nextLine();
                if(aline.isEmpty()) continue;
                String[] configs = aline.split(" ");
                int v = Integer.parseInt(configs[0]);
                int w = Integer.parseInt(configs[1]);
                validateVertex(v);
                validateVertex(w);
                STE edge = new STE(v,w,configs[2],Integer.parseInt(configs[3]),Integer.parseInt(configs[4]));
                addEdge(edge);
            }
            myReader.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public NetworkGraph getCopperOnlyGraph()
    {
        NetworkGraph copperOnlyGraph = new NetworkGraph(this.V);
        for (int v = 0; v < V; v++) {
            // reverse so that adjacency list is in same order as original
            Stack<STE> reverse = new Stack<STE>();
            for (STE e : adj[v]) {
                reverse.push(e);
            }
            for (STE e : reverse) {
                if(e.getMaterial().equals("copper"))
                {
                    copperOnlyGraph.addEdge(e);
                }
            }
        }
        return copperOnlyGraph;
    }

    public NetworkGraph afterOneVetexFailed(int pV)
    {
        NetworkGraph graphAfterFailedOneNode = new NetworkGraph(this.V);

        for (int v = 0; v < V; v++)
        {
            // skip copy of adj[pV]
            if (v == pV)
                continue;
            // modify all edges to replace last vert with pV (i.e. replace failed node with last node)
            for (STE e : adj[v])
            {
                // skip all edges contains pV as it failed
                if(e.contains(pV)) continue;

                // if not contains pV, then swap the id of vert between pV and V-1 (last node) and store it
                if (e.contains(V - 1))
                {
                    STE newE = new STE(pV, e.other(V-1));
                    graphAfterFailedOneNode.addEdge(newE);
                }
                else
                {
                    graphAfterFailedOneNode.addEdge(e);
                }
            }
        }
        graphAfterFailedOneNode.V = V-1;
        return graphAfterFailedOneNode;
    }

    /**
     * Initializes a new edge-weighted graph that is a deep copy of {@code G}.
     *
     * @param  G the edge-weighted graph to copy

    public NetworkGraph(NetworkGraph G) {
        this(G.V());
        this.E = G.E();
        for (int v = 0; v < G.V(); v++) {
            // reverse so that adjacency list is in same order as original
            Stack<STE> reverse = new Stack<STE>();
            for (STE e : G.adj[v]) {
                reverse.push(e);
            }
            for (STE e : reverse) {
                adj[v].add(e);
            }
        }
    }
     */

    /**
     * Returns the number of vertices in this edge-weighted graph.
     *
     * @return the number of vertices in this edge-weighted graph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in this edge-weighted graph.
     *
     * @return the number of edges in this edge-weighted graph
     */
    public int E() {
        return E;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * Adds the undirected STE {@code e} to this edge-weighted graph.
     *
     * @param  e the edge
     * @throws IllegalArgumentException unless both endpoints are between {@code 0} and {@code V-1}
     */
    public void addEdge(STE e) {
        int v = e.either();
        int w = e.other(v);
        validateVertex(v);
        validateVertex(w);

        if(!edgeAlreadyAdded(adj[v], e))
        {
            adj[v].add(e);
            adj[w].add(e);
            E++;
        }
    }

    private boolean edgeAlreadyAdded(ArrayList<STE> edges, STE e)
    {
        for(STE ste: edges)
        {
            if(ste.equals(e))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the edges incident on vertex {@code v}.
     *
     * @return the edges incident on vertex {@code v} as an Iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public ArrayList<STE>[] adj() {
        return adj;
    }

    /**
     * Returns the edges incident on vertex {@code v}.
     *
     * @param  v the vertex
     * @return the edges incident on vertex {@code v} as an Iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public ArrayList<STE> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    /**
     * Returns the edge incident on vertex {@code v} and vertex {@code w}.
     *
     * @param  v the vertex
     * @param  w the vertex
     * @return the edge incident on vertex {@code v} and vertex {@code w} as a STE
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public STE edge(int v, int w)
    {
        validateVertex(v);
        validateVertex(w);
        for(STE edge : adj[v])
        {
            if(edge.other(v) == w) return edge;
        }
        return null;
    }

    /**
     * Returns the degree of vertex {@code v}.
     *
     * @param  v the vertex
     * @return the degree of vertex {@code v}               
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int degree(int v) {
        validateVertex(v);
        return adj[v].size();
    }

    /**
     * Returns all edges in this edge-weighted graph.
     * To iterate over the edges in this edge-weighted graph, use foreach notation:
     * {@code for (STE e : G.edges())}.
     *
     * @return all edges in this edge-weighted graph, as an iterable
     */
    public Iterable<STE> edges() {
        ArrayList<STE> list = new ArrayList<STE>();
        for (int v = 0; v < V; v++) {
            int selfLoops = 0;
            for (STE e : adj(v)) {
                if (e.other(v) > v) {
                    list.add(e);
                }
                // add only one copy of each self loop (self loops will be consecutive)
                else if (e.other(v) == v) {
                    if (selfLoops % 2 == 0) list.add(e);
                    selfLoops++;
                }
            }
        }
        return list;
    }

    /**
     * Returns a string representation of the edge-weighted graph.
     * This method takes time proportional to <em>E</em> + <em>V</em>.
     *
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *         followed by the <em>V</em> adjacency lists of edges
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for (STE e : adj[v]) {
                s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

    /**
     * Unit tests the {@code EdgeWeightedGraph} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        NetworkGraph G = new NetworkGraph(args[0]);
        System.out.println(G.toString());
    }

}
