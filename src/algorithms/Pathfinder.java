package algorithms;

import models.*;
import models.Map;

import java.util.*;

public class Pathfinder {
    private int iterations = 0;
    private List<String> iterationLogs = new ArrayList<>();
    public List<SlidingState> solve(Map map, String algoType) {
        this.iterations = 0;
        this.iterationLogs.clear();
        PriorityQueue<SlidingState> priority = new PriorityQueue<>(Comparator.comparingInt(s -> s.totalCost));
        Set<SlidingState> visited = new HashSet<>();
        SlidingState startState = new SlidingState(map.startPos, 0, 0, 0, null, "START");
        if (algoType.equals("A*") || algoType.equals("GBFS")) {
            startState.totalCost = getHeuristic(startState, map);
        } else {
            startState.totalCost = 0;
        }
        priority.add(startState);

        while (!priority.isEmpty()) {
            SlidingState curr = priority.poll();

            // System.out.println("Current: " + curr.currentPos.x + "," + curr.currentPos.y + 
            //                 " | Goal: " + map.targetPos.x + "," + map.targetPos.y);
            
            if (visited.contains(curr)) continue;
            visited.add(curr);
            this.iterations++;

            iterationLogs.add(String.format("Iterasi %d: Pos(%d,%d) | Target: %d | g(n): %d | f(n): %d", 
            this.iterations, curr.currentPos.x, curr.currentPos.y, curr.nextTargetNum, curr.totalACost, curr.totalCost));

            if (curr.currentPos.isColliding(map.targetPos)) {
                int totalRequired = map.targetNumPosition.size();
                
                if (curr.nextTargetNum == totalRequired) {
                    return constructPath(curr);
                } else {
                }
            }

            int[][] dir = {{0,1}, {0, -1}, {1,0}, {-1, 0}};
            for (int[] d : dir) {
                SlidingState next = calcSlide(curr, d[0], d[1], map);

                if (next != null && !visited.contains(next)) {
                    if (algoType.equals("A*")) {
                        next.totalCost = next.totalACost + getHeuristic(next, map);
                    }
                    else if (algoType.equals("UCS")) {
                        next.totalCost = next.totalACost;
                    }
                    else if (algoType.equals("GBFS")) {
                        next.totalCost = getHeuristic(next, map);
                    }
                    priority.add(next);
                }
            }
        }
        return null;
    }

    private String getMapSnapshot(Map map, Node currentPos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < map.n; i++) {
            for (int j = 0; j < map.m; j++) {
                if (i == currentPos.x && j == currentPos.y) {
                    sb.append('Z');
                } else {
                    sb.append(map.grid[i][j]);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void saveFullReport(String mapName, String algo, long time, List<SlidingState> path, Map map) {
        try {
            java.io.File dir = new java.io.File("test");
            if (!dir.exists()) dir.mkdir();
            String AlgoName = algo.replace("*", "Star");

            String filename = "test/report_" + mapName + "_" + AlgoName + ".txt";
            java.io.PrintWriter writer = new java.io.PrintWriter(filename);

            if (path != null) {
                for (int i = 0; i < path.size(); i++) {
                    SlidingState s = path.get(i);
                    if (i == 0) {
                        writer.println("Initial:");
                    } else {
                        writer.println("Step " + i + " : " + s.lastMove);
                    }
                    writer.println(getMapSnapshot(map, s.currentPos));
                    writer.println(); 
                }
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getHeuristic(SlidingState state, Map map) {
        Node target;
        if (map.targetNumPosition.containsKey(state.nextTargetNum)) {
            target = map.targetNumPosition.get(state.nextTargetNum);
        }
        else {
            target = map.targetPos;
        }
        return Math.abs(state.currentPos.x - target.x) + Math.abs(state.currentPos.y - target.y);
    }
    private List<SlidingState> constructPath(SlidingState state) {
        List<SlidingState> path = new ArrayList<>();
        while (state != null) {
            path.add(0, state);
            state = state.parent;
        }
        return path;
    }

    public SlidingState calcSlide(SlidingState current, int dx, int dy, Map map) {
        int curr_x = current.currentPos.x;
        int curr_y = current.currentPos.y;
        int totCost = 0;
        int nextTarget = current.nextTargetNum;

        while (true) {
            int tx = curr_x + dx;
            int ty = curr_y + dy;
            
            if (tx < 0 || tx >= map.n || ty < 0 || ty >= map.m) return null;
            
            if (map.grid[tx][ty] == 'X') break;

            curr_x = tx;
            curr_y = ty;
            totCost += map.costs[curr_x][curr_y];

            if (map.grid[curr_x][curr_y] == 'L') return null;

            if (Character.isDigit(map.grid[curr_x][curr_y])) {
                int num = Character.getNumericValue(map.grid[curr_x][curr_y]);
                if (num == nextTarget) {
                    nextTarget++;
                }
                else if (num > nextTarget) {
                    return null;
                }
            }
        }

        if (curr_x == current.currentPos.x && curr_y == current.currentPos.y) return null;

        String moveDir = (dx == 1) ? "Down" : (dx == -1) ? "Up" : (dy == 1) ? "Right" : "Left";
        return new SlidingState(new Node(curr_x, curr_y), nextTarget, 0, current.totalACost + totCost, current, moveDir);
    }
    
    public int getIterations() {
        return this.iterations;
    }
}
