package cs1501_p4;

public class Biconnected {
    private int[] low;
    private int[] pre;
    private int cnt;
    private boolean[] articulation;

    public Biconnected(NetworkGraph G) {
        low = new int[G.V()];
        pre = new int[G.V()];
        articulation = new boolean[G.V()];
        for (int v = 0; v < G.V(); v++)
            low[v] = -1;
        for (int v = 0; v < G.V(); v++)
            pre[v] = -1;
        
        for (int v = 0; v < G.V(); v++)
            if (pre[v] == -1)
                dfs(G, v, v);
    }

    private void dfs(NetworkGraph G, int u, int v) {
        int children = 0;
        pre[v] = cnt++;
        low[v] = pre[v];
        for (STE e : G.adj(v)) {
            int w = e.other(v);
            if (pre[w] == -1) {
                children++;
                dfs(G, v, w);

                // update low number
                low[v] = Math.min(low[v], low[w]);

                // non-root of DFS is an articulation point if low[w] >= pre[v]
                if (low[w] >= pre[v] && u != v) 
                    articulation[v] = true;
            }

            // update low number - ignore reverse of edge leading to v
            else if (w != u)
                low[v] = Math.min(low[v], pre[w]);
        }

        // root of DFS is an articulation point if it has more than 1 child
        if (u == v && children > 1)
            articulation[v] = true;
    }

    // is vertex v an articulation point?
    public boolean isArticulation(int v) { return articulation[v]; }


}
