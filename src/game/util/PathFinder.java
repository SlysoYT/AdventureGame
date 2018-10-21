package game.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import game.level.Level;
import game.level.tile.Tile;

public class PathFinder
{
	private Level level;

	private Comparator<Node> nodeSorter = new Comparator<Node>()
	{
		public int compare(Node n0, Node n1)
		{
			if(n1.fCost < n0.fCost) return +1; //If so, move it up in the index
			if(n1.fCost > n0.fCost) return -1; //If this is the case, move it down
			return 0;
		}
	};

	public PathFinder(Level level)
	{
		this.level = level;
	}

	public List<Node> findPath(Vector2i start, Vector2i end)
	{
		List<Node> openList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();
		Node current = new Node(start, null, 0, level.getDistance(start, end));
		openList.add(current);

		while(openList.size() > 0)
		{
			Collections.sort(openList, nodeSorter); //Sorts nodes by cost, lowest cost is on top, highest on bottom
			current = openList.get(0); //Current is now the node with the lowest cost

			if(current.tile.equals(end))
			{
				ArrayList<Node> path = new ArrayList<Node>();
				//Will be looping throught till the start, because the start's parent is null
				while(current.parent != null)
				{
					path.add(current);
					current = current.parent;
				}
				openList.clear();
				closedList.clear();
				return path;
			}

			openList.remove(current); //Move lowest cost node to the closed list
			closedList.add(current);
			
			for(int i = 0; i < 9; i++)
			{
				if(i == 4) continue;
				int x = current.tile.getX();
				int y = current.tile.getY();
				int xDir = (i % 3) - 1;
				int yDir = (i / 3) - 1;

				if(!validPathTile(level.getTile(x + xDir, y + yDir))) continue;

				Vector2i a = new Vector2i(x + xDir, y + yDir);
				double gCost = current.gCost + level.getDistance(current.tile, a);
				double hCost = level.getDistance(a, end);

				Node node = new Node(a, current, gCost, hCost);
				if(vectorInList(closedList, a) && gCost >= node.gCost) continue;
				if(!vectorInList(openList, a) || gCost < node.gCost) openList.add(node);
			}
		}

		closedList.clear();
		return null;
	}

	private boolean validPathTile(Tile tile)
	{
		if(tile == null) return false;
		//Tiles we don't want to step on
		if(tile.solid() || tile.getHitbox() != null || tile.deadly()) return false;
		return true;
	}

	private boolean vectorInList(List<Node> list, Vector2i vector)
	{
		for(Node n : list)
		{
			if(n.tile.equals(vector)) return true;
		}
		return false;
	}
}
