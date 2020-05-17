
import java.util.ArrayList;
import java.awt.Color;

class Helper {
    
    static final Color LIGHT_GREEN = new Color(150, 201, 100);
    static final Color LIGHT_RED = new Color(255, 121, 121);
    static final Color LIGHT_YELLOW = new Color(250, 250, 210);
    static final Color YELLOW = new Color(207, 181, 59);
    static final Color LIGHT_BLUE = new Color(176, 224, 230);
    
	private static final ArrayList<ArrayList<Integer>> graph = new ArrayList<>();

	static void createAdjacencyGraph(int m, int n) {
		for(int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				ArrayList<Integer> adjacentNodes = new ArrayList<>();
				if ((j + 1) < n) {
					adjacentNodes.add((i * n) + j + 1);
				}
				if ((i + 1) < m) {
					if ((j - 1) >= 0) {
						adjacentNodes.add(((i + 1) * n) + j - 1);
					}
					adjacentNodes.add(((i + 1) * n) + j);
					if ((j + 1) < n) {
						adjacentNodes.add(((i + 1) * n) + j + 1);
					}
				}
				graph.add(adjacentNodes);
			}
		}
	}

	static boolean areNeighbours(int source, int destination) {
		int min = Math.min(source, destination);
		int max = Math.max(source, destination);
		return graph.get(min).contains(max);
	}
}