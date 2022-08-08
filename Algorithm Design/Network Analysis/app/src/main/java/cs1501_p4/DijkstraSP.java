package cs1501_p4;

import java.util.ArrayList;
import java.util.Stack;

public class DijkstraSP{
    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private STE[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices
	/*
	*
  9
0 1 optical 10000 60
0 2 copper 100 40
	* */


	/**
	 * Computes a shortest-paths tree from the source vertex {@code s} to every other
	 * vertex in the edge.getLatencyed digraph {@code G}.
	 *
	 * @param  G the edge.getLatencyed digraph
	 * @param  s the source vertex
	 * @throws IllegalArgumentException if an edge.getLatency is negative
	 * @throws IllegalArgumentException unless {@code 0 <= s < V}
	 */
	public DijkstraSP(NetworkGraph G, int s) {
		for (STE e : G.edges()) {
			if (e.getLatency() < 0)
				throw new IllegalArgumentException("edge " + e + " has negative latency");
		}

		distTo = new double[G.V()];
		edgeTo = new STE[G.V()];

		validateVertex(s);

		for (int v = 0; v < G.V(); v++)
			distTo[v] = Double.POSITIVE_INFINITY;
		distTo[s] = 0.0;

		// relax vertices in order of distance from s
		pq = new IndexMinPQ<Double>(G.V());
		pq.insert(s, distTo[s]);
		while (!pq.isEmpty()) {
			int v = pq.delMin();
			for (STE e : G.adj(v))
				relax(e,v);
		}

		// check optimality conditions
		assert check(G, s);
	}

	// relax edge e and update pq if changed
	private void relax(STE e, int v) {
		//int v = e.either();
		int w = e.other(v);
		if (distTo[w] > distTo[v] + e.getLatency()) {
			distTo[w] = distTo[v] + e.getLatency();
			edgeTo[w] = e;
			if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
			else                pq.insert(w, distTo[w]);
		}
	}

	/**
	 * Returns the length of a shortest path from the source vertex {@code s} to vertex {@code v}.
	 * @param  v the destination vertex
	 * @return the length of a shortest path from the source vertex {@code s} to vertex {@code v};
	 *         {@code Double.POSITIVE_INFINITY} if no such path
	 * @throws IllegalArgumentException unless {@code 0 <= v < V}
	 */
	public double distTo(int v) {
		validateVertex(v);
		return distTo[v];
	}

	/**
	 * Returns true if there is a path from the source vertex {@code s} to vertex {@code v}.
	 *
	 * @param  v the destination vertex
	 * @return {@code true} if there is a path from the source vertex
	 *         {@code s} to vertex {@code v}; {@code false} otherwise
	 * @throws IllegalArgumentException unless {@code 0 <= v < V}
	 */
	public boolean hasPathTo(int v) {
		validateVertex(v);
		return distTo[v] < Double.POSITIVE_INFINITY;
	}

	/**
	 * Returns a shortest path from the source vertex {@code s} to vertex {@code v}.
	 *
	 * @param  v the destination vertex
	 * @return a shortest path from the source vertex {@code s} to vertex {@code v}
	 *         as an iterable of edges, and {@code null} if no such path
	 * @throws IllegalArgumentException unless {@code 0 <= v < V}
	 */
	public Iterable<STE> pathTo(int v) {
		validateVertex(v);
		if (!hasPathTo(v)) return null;
		Stack<STE> path = new Stack<STE>();

		STE e = edgeTo[v];
		while(e != null)
		{
			path.push(e);
			v = e.other(v);
			e = edgeTo[v];
		}
/*
		for (STE e = edgeTo[v]; e != null; e = edgeTo[e.other(v)]) {
			path.push(e);
			v=e.other(v);
		}

 */
		ArrayList<STE> pathInOrder = new ArrayList<STE>();
		while(!path.empty())
		{
			STE ste = path.pop();
			pathInOrder.add(ste);
		}
		return pathInOrder;
	}


	// check optimality conditions:
	// (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.getLatency()
	// (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.getLatency()
	private boolean check(NetworkGraph G, int s) {

		// check that edge.getLatencys are non-negative
		for (STE e : G.edges()) {
			if (e.getLatency() < 0) {
				System.err.println("negative edge.getLatency detected");
				return false;
			}
		}

		// check that distTo[v] and edgeTo[v] are consistent
		if (distTo[s] != 0.0 || edgeTo[s] != null) {
			System.err.println("distTo[s] and edgeTo[s] inconsistent");
			return false;
		}
		for (int v = 0; v < G.V(); v++) {
			if (v == s) continue;
			if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
				System.err.println("distTo[] and edgeTo[] inconsistent");
				return false;
			}
		}

		// check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.getLatency()
		for (int v = 0; v < G.V(); v++) {
			for (STE e : G.adj(v)) {
				int w = e.other(v);
				if (distTo[v] + e.getLatency() < distTo[w]) {
					System.err.println("edge " + e + " not relaxed");
					return false;
				}
			}
		}

		// check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.getLatency()
		for (int w = 0; w < G.V(); w++) {
			if (edgeTo[w] == null) continue;
			STE e = edgeTo[w];
			int v = e.other(w);
			if (w != e.other(v)) return false;
			if (distTo[v] + e.getLatency() != distTo[w]) {
				System.err.println("edge " + e + " on shortest path not tight");
				return false;
			}
		}
		return true;
	}

	// throw an IllegalArgumentException unless {@code 0 <= v < V}
	private void validateVertex(int v) {
		int V = distTo.length;
		if (v < 0 || v >= V)
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
	}

	/**
	 * Unit tests the {@code DijkstraSP} data type.
	 *
	 * @param args the command-line arguments

	public static void main(String[] args) {
		In in = new In(args[0]);
		NetworkGraph G = new NetworkGraph(in);
		int s = Integer.parseInt(args[1]);

		// compute shortest paths
		DijkstraSP sp = new DijkstraSP(G, s);


		// print shortest path
		for (int t = 0; t < G.V(); t++) {
			if (sp.hasPathTo(t)) {
				StdOut.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
				for (STE e : sp.pathTo(t)) {
					StdOut.print(e + "   ");
				}
				StdOut.println();
			}
			else {
				StdOut.printf("%d to %d         no path\n", s, t);
			}
		}
	}
	 */
}
