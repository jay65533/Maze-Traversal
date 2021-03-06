import java.util.LinkedHashSet;
import java.util.Set;

public class AStar {
	private Maze maze;
	private Node Start;
	private Node End;
	private int length;
	private int width;
	private int expanded;
	private boolean reached;
	private MazeBlocks MazeBlocks;
	private boolean[][] explored;
	private Node [][] board;
	
	AStar(Maze m){
		this.maze = m;
		this.length = m.getLength();
		this.width = m.getWidth();
		this.Start = m.Start;
		this.End = m.End;
        this.expanded = 0;
        this.reached = false;
        this.board= new Node[length+2][width+2];
        for(int i=0;i<length+2;i++){
        	for(int j=0;j< width+2;j++){
        		this.board[i][j] = m.board[i][j];
        		this.board[i][j].isVisited = false;
        	}
        }
	}
	
	AStar(MazeBlocks m){
		this.MazeBlocks = m;
		this.length = m.length;
		this.width = m.width;
		this.Start = m.Start;
		this.End = m.End;
        this.expanded = 0;
        this.reached = false;
        this.board= new Node[length][width];
        explored = new boolean[length][width];
        for(int i=0;i<length;i++){
        	for(int j=0;j<width;j++){
        		explored[i][j] = false;
        	}
        }
        for(int i=0;i<length;i++){
        	for(int j=0;j< width;j++){
        		this.board[i][j] = m.board[i][j];
        		this.board[i][j].isVisited = false;
        	}
        }
	}
    //========================================
    // A* functions
    // =======================================

    public boolean isDestination(Node node)
    {
        if(node == End)
            return true;
        else   
            return false;
    }

    public double calculateHManhattan(int row, int col)
    {
        return Math.abs(row - End.x) + Math.abs(col - End.y);
    }

    public double calculateHValue(int row, int col)
    {
        return ((double) Math.sqrt((row-(length-1)*(row-(length-1)) + (col-(width-1)*(col-(width-1))))));
    }

    // If a path is found,  which nodes were the optimal path
    private void tracePath(Node lastVisitedNode)
    {
        Node pathPtr = lastVisitedNode;
        while (pathPtr != Start)
        {
            pathPtr.isVisited = true;
            pathPtr = pathPtr.parent;
        }
    }

    public void aStarSearch()
    {
        boolean hasPath = false;
        
        Set<Node> openSet = new LinkedHashSet();
        // keeps track of the nodes we've checked to be the best 'f' value
        boolean[][] closedSet = new boolean[length][width];

        // Add the Starting node to the openset
        Start.g = 0;
        Start.h = 0;
        Start.f = 0;
        openSet.add(Start);

        Node lowestFNode = null;
        while(!openSet.isEmpty() && !reached)
        {
            lowestFNode = getLowestNode(openSet);
            //System.out.println("Next node: \t x:" + lowestFNode.x + "\ty:" + lowestFNode.y);

            // pop the lowestFNode from the open set
            // while adding it to the closedSet.
            // This indicates that the node was found to be the best option
            openSet.remove(lowestFNode);
            closedSet[lowestFNode.x][lowestFNode.y] = true;
            
            // Generate the the neighbors and recalculate the f(n) = g(n) + h(n)
            //this.expanded += lowestFNode.neighbors.size() - 1;
            double newG, newF, newH;
            //System.out.println(lowestFNode.neighbors.size());
            for (int i = 0; i < lowestFNode.neighbors.size(); ++i)
            {
                Node currentNeighbor = lowestFNode.neighbors.get(i);
                int cx = currentNeighbor.x;
                int cy = currentNeighbor.y;

                
                // Check that the neighbor is the destination Node
                if (isDestination(currentNeighbor))
                {
                    // If it is, then set the parent node
                	
                    currentNeighbor.parent = lowestFNode;
                    tracePath(currentNeighbor);
                    hasPath = true;
                    reached = true;
                    
                }
                // Check if the neighbor is on the closed list
                // or if it's blocked 
                else if (closedSet[cx][cy] == false)
                {
                	this.expanded ++;
                	//explored[cx][cy] = true;
                    // Recalculate the neighbor's 'f'
                    newG = lowestFNode.g + 1.0; 
                    newH = calculateHManhattan(cx, cy);
                    newF = newG + newH;
                    //System.out.println("x: " + cx + "y: "+ cy);
                    //System.out.println("F value:" + newF);
                    //System.out.println("H value:" + newH);
                    //System.out.println("G value:" + newG);

                    // Add the current neighbor to the open list
                    // since it's a candidate to be checked for the lowest 'f'
                    currentNeighbor.g = newG;
                    currentNeighbor.h = newH;
                    currentNeighbor.f = newF;
                    currentNeighbor.parent = lowestFNode;

                    // Since we're using a set, there's no need to check if 
                    // that node has already been inserted.
                    openSet.add(currentNeighbor);

                }
            }
            
        // Trace that path based on the last visited node
        // If a viable path is found, then this should start at End
        //tracePath(lowestFNode);       
        }
        
        // No viable path available   
        if (hasPath&& reached)
            System.out.println("Found a viable path from Start to End");        
        else
            System.out.println("No viable path found"); 
    }
    // Returns the node with the lowest 'f' within the open set
    private Node getLowestNode(Set<Node> openSet)
    {
        // find the node with the least f
        double minF = Double.MAX_VALUE;
        Node[] openArray = openSet.toArray(new Node[0]);

        Node q = null;
        for (int i = 0; i < openArray.length; ++i)
        {
            if (openArray[i].f < minF)    
            {
                minF = openArray[i].f;
                q = openArray[i];
            }
        }
        return q;
    }
    
    // Method for debugging purposes
    // Counts and returns the number of 
    // adjecent connections for a particular node
    static int displayN(Node node)
    {
        return node.neighbors.size();
    }

    public int getExpanded(){
    	/*int num = 0;
    	for (int i=0;i< length;i++){
    		for(int j=0;j<width;j++){
    			if(explored[i][j]){
    				num++;
    			}
    		}
    	}
    	return num;
    	*/
    	return this.expanded;
    }
    
    
}
