import processing.core.PApplet;
import processing.core.PVector;

import static parameters.Parameters.*;
import static save.SaveUtil.saveSketch;

public class HairyGrid extends PApplet {
    public static void main(String[] args) {
        PApplet.main(HairyGrid.class);
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
        randomSeed(SEED);
    }

    @Override
    public void setup() {
        noLoop();
    }

    @Override
    public void draw() {
        Node[][] grid =
                new Node[(width - 2 * MARGIN) / CELL_SIZE][(height - 2 * MARGIN) / CELL_SIZE];
        for (int x = MARGIN; x < width - MARGIN; x += CELL_SIZE) {
            for (int y = MARGIN; y < height - MARGIN; y += CELL_SIZE) {
                grid[(x - MARGIN) / CELL_SIZE][(y - MARGIN) / CELL_SIZE] = new Node(x, y, this);
            }
        }

        int[][] values = new int[height][width];
        int maxValue = 0;
        for (int iter = 0; iter < ITERATION_NUMBER; iter++) {
            boolean[] virtualPixels = drawGrid(grid);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (virtualPixels[x + width * y]) {
                        values[x][y]++;
                        maxValue = max(maxValue, values[x][y]);
                    }
                }
            }
            updateGrid(grid);
            moveGrid(grid);
        }

        float logMax = log(1 + maxValue);
        loadPixels();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int c = color(map(log(values[i][j] + 1), 0, logMax, 255, 0));
                pixels[i + width * j] = c;
            }
        }
        updatePixels();

        saveSketch(this);
    }

    private boolean[] drawGrid(Node[][] grid) {
        boolean[] virtualPixels = new boolean[width * height];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (i < grid.length - 1)
                    plotLine(round(grid[i][j].pos.x), round(grid[i][j].pos.y),
                            round(grid[i + 1][j].pos.x), round(grid[i + 1][j].pos.y),
                            virtualPixels);
                if (j < grid[0].length - 1)
                    plotLine(round(grid[i][j].pos.x), round(grid[i][j].pos.y),
                            round(grid[i][j + 1].pos.x), round(grid[i][j + 1].pos.y),
                            virtualPixels);
            }
        }
        return virtualPixels;
    }

    // See https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
    private void plotLine(int x0, int y0, int x1, int y1, boolean[] pixels) {
        int dx = abs(x1 - x0);
        int sx = x0 < x1 ? 1 : -1;
        int dy = -abs(y1 - y0);
        int sy = y0 < y1 ? 1 : -1;
        int error = dx + dy;

        while (true) {
            pixels[x0 + width * y0] = true;
            if (x0 == x1 && y0 == y1) {
                break;
            }
            int e2 = 2 * error;
            if (e2 >= dy) {
                error += dy;
                x0 += sx;
            }
            if (e2 <= dx) {
                error += dx;
                y0 += sy;
            }
        }
    }

    private void moveGrid(Node[][] grid) {
        for (Node[] arr : grid) {
            for (Node n : arr) {
                n.pos.add(n.speed);
            }
        }
    }

    private void updateGrid(Node[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                PVector mod = new PVector(0, 0);
                int n = 0;
                if (i > 0) {
                    mod.add(PVector.mult(grid[i - 1][j].speed, 2));
                    n += 2;
                    if (j > 0) {
                        mod.add(grid[i - 1][j - 1].speed);
                        n++;
                    }
                    if (j < grid[0].length - 1) {
                        mod.add(grid[i - 1][j + 1].speed);
                        n++;
                    }
                }
                if (i < grid.length - 1) {
                    mod.add(PVector.mult(grid[i + 1][j].speed, 2));
                    n += 2;
                    if (j > 0) {
                        mod.add(grid[i + 1][j - 1].speed);
                        n++;
                    }
                    if (j < grid[0].length - 1) {
                        mod.add(grid[i + 1][j + 1].speed);
                        n++;
                    }
                }
                if (j > 0) {
                    mod.add(PVector.mult(grid[i][j - 1].speed, 2));
                    n += 2;
                }
                if (j < grid[0].length - 1) {
                    mod.add(PVector.mult(grid[i][j + 1].speed, 2));
                    n += 2;
                }
                mod.add(PVector.mult(grid[i][j].speed, 4));
                n += 4;
                mod.div(n);
                grid[i][j].speed = mod;
            }
        }
    }
}
