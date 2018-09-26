
public class main {
    public static void main(String[] args) {
    	long  startTime, endTime, elapsedTime;

        /*Maze b = new Maze(20,20);
        b.print();
    	*/
    	
    	MazeBlocks m = new MazeBlocks(101,101);
    	m.print();
    	
        AStarAdaptive aAdaptive = new AStarAdaptive(m);
        aAdaptive.aStarSearch();
    	
        AStarBackward astarbackward = new AStarBackward(m);        
        startTime = System.currentTimeMillis();
        astarbackward.aStarSearch();
        endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;        
        System.out.println("Number of expanded node:" + astarbackward.getExpanded());
        System.out.println("Time elapsed on Repeated Backward A*: " + elapsedTime + " ms");
        m.print();
        
        AStarLargerG astarlarger = new AStarLargerG(m);
        startTime = System.currentTimeMillis();
        astarlarger.aStarSearch();
        endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;
        System.out.println("Time elapsed on LargerG A*: " + elapsedTime + " ms");
        System.out.println("Number of expanded node: " + astarlarger.getExpanded());
        m.print();
        
        AStarSmallerG astarsmaller = new AStarSmallerG(m);
        startTime = System.currentTimeMillis();
        astarsmaller.aStarSearch();
        endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;
        System.out.println("Time elapsed on SmallerG A*: " + elapsedTime + " ms");
        System.out.println("Number of expanded node: " + astarsmaller.getExpanded());
        m.print();

        
       
    }
}
