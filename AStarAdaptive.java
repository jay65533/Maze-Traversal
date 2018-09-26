import java.util.LinkedHashSet;
import java.util.Set;

public class AStarAdaptive {
	private Maze maze;
	private MazeBlocks MazeBlocks;
	private Node Start;
	private Node End;
	private int length;
	private int width;
	private int expanded;
	private boolean reached;
	private boolean[][] explored;
	private Node[][] board;
	private boolean[][] closedSet; 
	
	AStarAdaptive(Maze m){
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
        closedSet = new boolean[length+2][width+2]; // +2 so it works with DFS maze
        for(int i=0;i<length;i++){
        	for(int j=0;j<width;j++){
                explored[i][j] = false;
                closedSet[i][j] = false;
        	}
        }
        
	}
	
	AStarAdaptive(MazeBlocks m){
		this.MazeBlocks = m;
		this.length = m.length;
		this.width = m.width;
		this.Start = m.Start;
		this.End = m.End;
        this.expanded = 0;
        this.reached = false;
        explored = new boolean[length][width];
        for(int i=0;i<length;i++){
        	for(int j=0;j<width;j++){
        		explored[i][j] = false;
        	}
        }
        this.board= new Node[length][width];
        for(int i=0;i<length;i++){
        	for(int j=0;j< width;j++){
        		this.board[i][j] = m.board[i][j];
        		this.board[i][j].isVisited = false;
        	}
        }
        closedSet = new boolean[length][width];
        for(int i=0;i<length;i++){
        	for(int j=0;j<width;j++){
                explored[i][j] = false;
                closedSet[i][j] = false;
        	}
        }
	}
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

    // If a path is found, trace which nodes were the optimal path
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
        long  startTime, endTime, elapsedTime;

        startTime = System.currentTimeMillis();
        aStarSearchNormal();
        endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;
        System.out.println("Time elapsed on Repeat Forward A*: " + elapsedTime + " ms");
        System.out.println("Number of expanded node: " + getExpanded());
        MazeBlocks.print();

        //reset expended node array
        for(int i=0;i<length;i++){
        	for(int j=0;j<width;j++){
                explored[i][j] = false;
                MazeBlocks.board[i][j].isVisited = false;
                if(closedSet[i][j] == true)
                {
                    MazeBlocks.board[i][j].h = End.g - MazeBlocks.board[i][j].g;
                    closedSet[i][j] = false;
                }
            }
        }

        reached = false;
        this.expanded = 0;
        startTime = System.currentTimeMillis();
        aStarSearchAdaptive();
        endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;
        System.out.println("Time elapsed on Adaptive A*: " + elapsedTime + " ms");
        System.out.println("Number of expanded node: " + getExpanded());
        MazeBlocks.print();
    }

    public void aStarSearchAdaptive()
    {
        boolean hasPath = false;

        Set<Node> openSet = new LinkedHashSet();
        // keeps track of the nodes we've checked to be the best 'f' value

        // Add the Starting node to the openset
        Start.g = 0;
        Start.h = 0;
        Start.f = 0;
        openSet.add(Start);

        Node lowestFNode = null;
        //System.out.println("End.G value: " + End.g);
        while(!openSet.isEmpty() && !reached)
        {
        	
            //lowestFNode = getLowestNode(openSet);

            // For testing adaptive A* to see if choosing by lowest h
            // will give a lower number of expanded nodes
            lowestFNode = getLowestNodeByH(openSet);    

            //System.out.println("Next node: \t x:" + lowestFNode.x + "\ty:" + lowestFNode.y);

            // pop the lowestFNode from the open set
            // while adding it to the closedSet.
            // This indicates that the node was found to be the best option
            openSet.remove(lowestFNode);
            if (closedSet == null)
            {
                System.out.println("is null");
                return;
            }
            
            closedSet[lowestFNode.x][lowestFNode.y] = true;

            // Generate the the neighbors and recalculate the f(n) = g(n) + h(n)
            //this.expanded += lowestFNode.neighbors.size() - 1;
            double newG, newF, newH;
            //System.out.println("End.G value in while: " + End.g);
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
                    currentNeighbor.g = lowestFNode.g + 1.0;
                    tracePath(currentNeighbor);
                    hasPath = true;
                    reached = true;
                    
                }
                // Check if the neighbor is on the closed list
                // or if it's blocked 
                else if (closedSet[cx][cy] == false)
                {
                	this.expanded++;
                	//this.explored[cx][cy] = true;
                    // Recalculate the neighbor's 'f'
                    if(currentNeighbor.f >= (Double.MAX_VALUE-100)) //quick way to avoid floating point comparision
                    {
                        newH = calculateHManhattan(cx, cy);
                    }
                    else
                    {
                        newH = currentNeighbor.h;
                    }
                    newG = lowestFNode.g + 1.0;
                    newF = newG + newH;
                    // Add the current neighbor to the open list
                    // since it's a candidate to be checked for the lowest 'f'
                    currentNeighbor.g = newG;
                    currentNeighbor.h = newH;
                    currentNeighbor.f = newF;
                    currentNeighbor.parent = lowestFNode;
                    /*
                    System.out.println("x: " + cx + "y: "+ cy);
                    System.out.println("F value:" + newF);
                    System.out.println("H value:" + newH);
                    System.out.println("G value:" + newG);
                    System.out.println("End.G value: " + End.g);
                    */
                    // Since we're using a set, there's no need to check if 
                    // that node has already been inserted.
                    openSet.add(currentNeighbor);

                }
            }
            /*
            for(int i=0; i< lowestFNode.neighbors.size();i++){
            	Node currentNeighbor = lowestFNode.neighbors.get(i);
                currentNeighbor.h = End.g - currentNeighbor.g;
                //System.out.println(currentNeighbor.g);
            	//System.out.println("new h value: "+ currentNeighbor.h);
            }
            */
            
        // Trace that path based on the last visited node
        // If a viable path is found, then this should start at End
        //tracePath(lowestFNode);       
        }
        //System.out.println("End.G value after while: " + End.g);
        // No viable path available   
        if (hasPath&& reached)
            System.out.println("Found a viable path from Start to End");        
        else
            System.out.println("No viable path found"); 
    }

    public void aStarSearchNormal()
    {
        boolean hasPath = false;

        Set<Node> openSet = new LinkedHashSet();
        // keeps track of the nodes we've checked to be the best 'f' value

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
                    currentNeighbor.g = lowestFNode.g + 1.0;
                    tracePath(currentNeighbor);
                    hasPath = true;
                    reached = true;
                    
                }
                // Check if the neighbor is on the closed list
                // or if it's blocked 
                else if (closedSet[cx][cy] == false)
                {
                	this.expanded++;
                	//explored[cx][cy] = true;
                    // Recalculate the neighbor's 'f'
                    newG = lowestFNode.g + 1.0; 
                    newH = calculateHManhattan(cx, cy);
                    newF = newG + newH;
                    /*
                    System.out.println("x: " + cx + "y: "+ cy);
                    System.out.println("F value:" + newF);
                    System.out.println("H value:" + newH);
                    System.out.println("G value:" + newG);
                    System.out.println("End.g value: " + End.g);
                    */
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

        Node q = openArray[0];
        for (int i = 0; i < openArray.length; ++i)
        {
        	if (openArray[i].f < minF)    
            {
                minF = openArray[i].f;
                q = openArray[i];
            }
            else if (openArray[i].f == minF){
            	if(q.g < openArray[i].g){
            		q = openArray[i];
            	}
            }
        }
        return q;
    }
    
    // Testing for adaptive A* new H
    private Node getLowestNodeByH(Set<Node> openSet)
    {
        // find the node with the least f
        double minH = Double.MAX_VALUE;
        //double minH = 0; // What if we went for the larger H?
        Node[] openArray = openSet.toArray(new Node[0]);

        Node q = openArray[0];
        for (int i = 0; i < openArray.length; ++i)
        {
            //System.out.println("open.h: " + openArray[i].h);
            if (openArray[i].h < minH)    
            {
                //System.out.println("new highest H node");
                minH = openArray[i].h;
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
    	}*/
    	
    	return this.expanded;
    }

}
