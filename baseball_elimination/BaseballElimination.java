import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Bag;
import java.util.HashMap;
import java.util.ArrayList;

public class BaseballElimination {
    private final int numOfTeams;
    private final ArrayList<String> teams = new ArrayList<>();
    private final int [] w;
    private final int [] losses;
    private final int [] r;
    private final int [][] g;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
		  In in = new In(filename);
			numOfTeams = in.readInt();
      w = new int [numOfTeams];
      losses = new int [numOfTeams];
      r = new int [numOfTeams];
			g = new int [numOfTeams][numOfTeams];
			for (int i = 0; i < numOfTeams; i++)
			{
				teams.add(in.readString());
				w[i] = in.readInt();
        losses[i] = in.readInt();
        r[i] = in.readInt();
        for (int j = 0; j < numOfTeams; j++)
        {                
          g[i][j] = in.readInt();
        }
			}
	} 
	// number of teams
	public int numberOfTeams() {
		return numOfTeams;
	} 
	// all teams                       
	public Iterable<String> teams() {
      return teams;
	} 
	// number of wins for given team                               
	public int wins(String team) {
    if (!teams.contains(team))
     throw new java.lang.IllegalArgumentException();
		return w[teams.indexOf(team)];
	} 
	// number of losses for given team                     
	public int losses(String team) {
    if (!teams.contains(team))
     throw new java.lang.IllegalArgumentException();
		return losses[teams.indexOf(team)];
	} 
	// number of remaining games for given team                   
	public int remaining(String team) {
    if (!teams.contains(team))
     throw new java.lang.IllegalArgumentException();
		return r[teams.indexOf(team)];
	} 
	// number of remaining games between team1 and team2              
	public int against(String team1, String team2) {
    if (!teams.contains(team1) || !teams.contains(team2))
     throw new java.lang.IllegalArgumentException();
		return g[teams.indexOf(team1)][teams.indexOf(team2)];
	}
	// is given team eliminated?   
	public boolean isEliminated(String team) {
    if (!teams.contains(team))
     throw new java.lang.IllegalArgumentException();
    for (int i = 0; i < numOfTeams; i++)
    {
  		if (w[i] > wins(team) + remaining(team))
  			return true;
    }
    int gameVertices = (numOfTeams - 1) * (numOfTeams - 2) / 2;
    int teamVertices = numOfTeams - 1;
    FlowNetwork fn = new FlowNetwork(2 + gameVertices + teamVertices);
    int s = numOfTeams;
    int t = teams.indexOf(team);
    int count = s + 1;
    int maxWPos = wins(team) + remaining(team);
    for (int i = 0; i < numOfTeams; i++)
    {
      if (i == t)
        continue;
      fn.addEdge(new FlowEdge(i, t, maxWPos - w[i]));
      for (int j = i + 1; j < numOfTeams; j++)
      {
        if (j == t)
          continue;
        fn.addEdge(new FlowEdge(s, count, g[i][j]));
        fn.addEdge(new FlowEdge(count, i, Double.POSITIVE_INFINITY));
        fn.addEdge(new FlowEdge(count, j, Double.POSITIVE_INFINITY));
        count++;
      }
    }
    FordFulkerson f = new FordFulkerson(fn, s, t);
    for (int i = s+1; i < count; i++)
    {
      if (f.inCut(i))
      {
        return true;
      }
    }
    return false;
	}  
	// subset R of teams that eliminates given team; null if not eliminated            
	public Iterable<String> certificateOfElimination(String team) {
    if (!teams.contains(team))
     throw new java.lang.IllegalArgumentException();
    for (int i = 0; i < numOfTeams; i++)
    {
      if (w[i] > wins(team) + remaining(team))
      {
        ArrayList<String> bounced = new ArrayList<>();
        bounced.add(teams.get(i));
        return bounced;
      }
    }
    int gameVertices = (numOfTeams - 1) * (numOfTeams - 2) / 2;
    int teamVertices = numOfTeams - 1;
    FlowNetwork fn = new FlowNetwork(2 + gameVertices + teamVertices);
    int s = numOfTeams;
    int t = teams.indexOf(team);
    int count = s + 1;
    int maxWPos = wins(team) + remaining(team);
    HashMap<Integer, Bag<Integer>> hm = new HashMap<>();
    for (int i = 0; i < numOfTeams; i++)
    {
      if (i == t)
        continue;
      fn.addEdge(new FlowEdge(i, t, maxWPos - w[i]));
      for (int j = i + 1; j < numOfTeams; j++)
      {
        if (j == t)
          continue;
        fn.addEdge(new FlowEdge(s, count, g[i][j]));
        Bag<Integer> b = new Bag<>();
        b.add(i);
        b.add(j);
        hm.put(count, b);
        fn.addEdge(new FlowEdge(count, i, Double.POSITIVE_INFINITY));
        fn.addEdge(new FlowEdge(count, j, Double.POSITIVE_INFINITY));
        count++;
      }
    }
    FordFulkerson f = new FordFulkerson(fn, s, t);
    ArrayList<String> subset = new ArrayList<>();
    for (int i = s+1; i < count; i++)
    {
      if (f.inCut(i))
      {
        for (int j : hm.get(i))
        {
          if (!subset.contains(teams.get(j)))
          {
            subset.add(teams.get((j)));
          }
        }
      }
    }
    if (subset.isEmpty())
      return null;
    return subset;
  }  
    
  // testing
  public static void main(String[] args) {
    BaseballElimination division = new BaseballElimination(args[0]);
    for (String team : division.teams()) {
        if (division.isEliminated(team)) {
            StdOut.print(team + " is eliminated by the subset R = { ");
            for (String t : division.certificateOfElimination(team)) {
                StdOut.print(t + " ");
            }
            StdOut.println("}");
        }
        else {
            StdOut.println(team + " is not eliminated");
    }
  }
}  
}
