package models;

import java.io.File;
import java.util.Scanner;
import java.util.HashMap;

public class Map {
    public int n, m;
    public char[][] grid;
    public int[][] costs;
    public Node startPos;
    public Node targetPos;
    public HashMap<Integer, Node> targetNumPosition;

    public Map() {
        targetNumPosition = new HashMap<>();
    }

    public boolean loadFromFile(String filePath) {
        try {
            File file = new File(filePath);
            Scanner sc = new Scanner(file);

            if (sc.hasNextInt()) { // barisxcolumn nxm
                this.n = sc.nextInt();
                this.m = sc.nextInt();
            }

            this.grid = new char[n][m];
            this.costs = new int[n][m];

            for (int i = 0; i < n; i++) {
                String row = sc.next();
                for (int j = 0; j < m; j++) {
                    grid[i][j] = row.charAt(j);
                    if (grid[i][j] == 'Z') startPos = new Node(i, j);
                    if (grid[i][j] == 'O') targetPos = new Node(i, j);
                    if (Character.isDigit(grid[i][j])) {
                        targetNumPosition.put(Character.getNumericValue(grid[i][j]), new Node(i, j));
                    }
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (sc.hasNextInt()) {
                        costs[i][j] = sc.nextInt();
                    }
                }
            }

            sc.close();
            return true;
        }
        catch (Exception e) {
            System.out.println("Error membaca file: " + e.getMessage());
            return false;
        }
    }

}