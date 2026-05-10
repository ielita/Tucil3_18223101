package gui;

import javax.swing.*;
import java.awt.*;
import models.Map;
import models.SlidingState;

public class MatrixPanel extends JPanel {
    private Map map;
    private SlidingState currState;
    private final int TILE_SIZE = 60;

    public void setMap(Map map) {
        this.map = map;
        this.currState = null;
    }

    public void setCurrentState(SlidingState state) {
        this.currState = state;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (map == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int i = 0; i < map.n; i++) {
            for (int j = 0; j < map.m; j++) {
                int x = j * TILE_SIZE;
                int y = i * TILE_SIZE;
                Color color1, color2;
                char tile = map.grid[i][j];
                switch(tile) {
                    case 'X':  // Rintangan/batu
                        color1 = new Color(33, 48, 66);
                        color2 = new Color(33, 48, 66);
                        break;
                    case 'L': // Lava
                        color1 = new Color(233, 35, 35);
                        color2 = new Color(233, 193, 35);
                        break;
                    case 'O': // Target
                        color1 = new Color(90, 221, 79);
                        color2 = new Color(40, 119, 42);
                        break;
                    case '*': // path
                        color1 = new Color(221, 232, 243);
                        color2 = new Color(221, 232, 243);
                        break;
                    default:
                        if (Character.isDigit(tile)) { // Angka
                            color1 = new Color(192, 121, 179);
                            color2 = new Color(153, 19, 42);
                            break;
                        } 
                        else {
                            color1 = new Color(221, 232, 243);
                            color2 = new Color(221, 232, 243);
                            break;
                        }
                }
                
                GradientPaint gp = new GradientPaint(x, y, color1, x, y + TILE_SIZE, color2);
                g2d.setPaint(gp);
                g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);

                g2d.setColor(new Color(40, 26, 103, 50)); // Hitam transparan
                g2d.drawRect(x, y, TILE_SIZE, TILE_SIZE);

                if (Character.isDigit(tile)) {
                    g2d.setColor(new Color(40, 26, 103));
                    g2d.setFont(new Font("Baskerville", Font.BOLD, 30));
                    
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(String.valueOf(tile));
                    int textHeight = fm.getAscent();
                    int textX = x + (TILE_SIZE - textWidth) / 2;
                    int textY = y + (TILE_SIZE + textHeight) / 2 - 2;
                    g2d.drawString(String.valueOf(tile), textX, textY);
                }
            }
        }

        if (currState != null || map.startPos != null) {
            int r = (currState != null) ? currState.currentPos.x : map.startPos.x;
            int c = (currState != null) ? currState.currentPos.y : map.startPos.y;
            
            int px = c * TILE_SIZE + 5;
            int py = r * TILE_SIZE + 5;
            int pSize = TILE_SIZE - 10;

            GradientPaint playerGp = new GradientPaint(px, py, new Color(33, 28, 64), px + pSize, py + pSize, new Color(87, 73, 166));
            g2d.setPaint(playerGp);
            g2d.fillOval(px, py, pSize, pSize);
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        if (map == null) return new Dimension(400, 400);
        return new Dimension(map.m * TILE_SIZE, map.n * TILE_SIZE);
    }
}
