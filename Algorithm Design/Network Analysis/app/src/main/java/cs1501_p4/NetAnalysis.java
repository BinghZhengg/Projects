/**
 * Network Analysis specification interface for CS1501 Project 4
 * @author	Dr. Farnan
 */
package cs1501_p4;

import java.util.ArrayList;
import java.util.Iterator;

public class NetAnalysis implements NetAnalysis_Inter {
	 private NetworkGraph G;         // graph
	/*
	*
  9
0 1 optical 10000 60
0 2 copper 100 40
	* */
	public NetAnalysis(String file)
	{
		G = new NetworkGraph(file);
	}

	/**
	 * Find the lowest latency path from vertex `u` to vertex `w` in the graph
	 *
	 * @param	u Starting vertex
	 * @param	w Destination vertex
	 *
	 * @return	ArrayList<Integer> A list of the vertex id's representing the
	 * 			path (should start with `u` and end with `w`)
	 * 			Return `null` if no path exists
	 */
	public ArrayList<Integer> lowestLatencyPath(int u, int w)
	{
		DijkstraSP  dsp = new DijkstraSP(G, u);
		ArrayList<Integer> list = new ArrayList<Integer>();
		Iterable<STE> edges = dsp.pathTo(w);
		Iterator<STE> it = edges.iterator();
		int start = u;
		list.add(start);
		while (it.hasNext())
		{
            STE ste = it.next();
            int nextV = ste.other(start);
				list.add(nextV);
				start = nextV;
//            System.out.println(ste);
		}
		if(list.size()==0) return null;
		else return list;
	}

	/**
	 * Find the bandwidth available along a given path through the graph
	 * (the minimum bandwidth of any edge in the path). Should throw an
	 * `IllegalArgumentException` if the specified path is not valid for
	 * the graph.
	 *
	 * @param	p,  A list of the vertex id's representing the
	 * 			path
	 *
	 * @return	int The bandwidth available along the specified path
	 */
	public int bandwidthAlongPath(ArrayList<Integer> p) throws IllegalArgumentException
	{
		int minBandWidth = Integer.MAX_VALUE;
		if(p.size()<2) throw new IllegalArgumentException("size of p is < 2");

		for(int i=1;i<p.size();i++)
		{
			STE edge = G.edge(p.get(i-1),p.get(i));
			if(edge == null) throw new IllegalArgumentException("The path from " + p.get(i-1) + " to " + p.get(i) + " is invalid");
			if(minBandWidth > edge.getBandWidth())
			{
				minBandWidth = edge.getBandWidth();
			}
		}
		return minBandWidth;
	}

	/**
	 * Return `true` if the graph is connected considering only copper links
	 * `false` otherwise
	 *
	 * @return	boolean Whether the graph is copper-only connected
	 */
	public boolean copperOnlyConnected()
	{
		NetworkGraph copperOnlyGraph = G.getCopperOnlyGraph();
		DepthFirstSearch search = new DepthFirstSearch(copperOnlyGraph, 0);
		for (int v = 0; v < copperOnlyGraph.V(); v++) {
			if (search.marked(v))
				System.out.print(v + " ");
		}

		System.out.println();
		if (search.count() != copperOnlyGraph.V()) System.out.println("NOT connected");
		else                         System.out.println("connected");

		return (search.count() == copperOnlyGraph.V());
	}


	/**
	 * Return `true` if the graph would remain connected if any two vertices in
	 * the graph would fail, `false` otherwise
	 *
	 * @return	boolean Whether the graph would remain connected for any two
	 * 			failed vertices
	 */
	public boolean connectedTwoVertFail()
	{
		/* Step 1: check if there is an articulation point in current graph if so return false*/
		Biconnected biconnected = new Biconnected(G);
		int V = G.V();
		for (int j = 0; j < V; j++)
		{
			if (biconnected.isArticulation(j))
			{
				System.out.println("After " + j + " failed, the graph is diconnected!");
				return false;
			}
		}

		/* Step 2: if step 1 passed, we need to check if after a vert failed, the remained graph has an articulation point */

		/* disconnect one of vetex and see if the resulted graph having an articulation point */
		for(int i=0;i< G.V();i++)
		{
			NetworkGraph oneNodeFailedGraph = G.afterOneVetexFailed(i);
			for (int j = 0; j < V; j++)
			{
				if (j == i)
					continue;
				if (biconnected.isArticulation(j))
				{
					System.out.println("After " + i + " and " + j + " failed, the graph is diconnected!");
					return false;
				}
			}
		}

		return true;
	};

	/**
	 * Find the lowest average (mean) latency spanning tree for the graph
	 * (i.e., a spanning tree with the lowest average latency per edge). Return
	 * it as an ArrayList of STE edges.
	 *
	 * Note that you do not need to use the STE class to represent your graph
	 * internally, you only need to use it to construct return values for this
	 * method.
	 *
	 * @return	ArrayList<STE> A list of STE objects representing the lowest
	 * 			average latency spanning tree
	 * 			Return `null` if the graph is not connected
	 */
	public ArrayList<STE> lowestAvgLatST()
	{
		PrimMST pmst = new PrimMST(G);
		Iterable<STE> edges = pmst.edges();
		ArrayList<STE> list = new ArrayList<STE>();
		for(STE e:edges)
		{
			list.add(e);
		}
		return list;

	}


}
