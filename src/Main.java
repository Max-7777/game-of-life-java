import processing.core.PApplet;
import processing.core.PVector;

import java.util.Arrays;

public class Main extends PApplet {

    int[][] grid; //2d array containing squares alive or dead (0 or 1)
    int res; //pixel size of each grid square
    boolean paused; //simulation paused
    int gridWidth, gridHeight; //width and height in grid tile units
    boolean gridLines; //toggle show blue gridlines


    public void settings() {
        size(700,700);
    }

    public void setup() {
        frameRate(10);
        res = 70;
        gridWidth = width / res;
        gridHeight = height / res;
        grid = newArray(gridWidth, gridHeight, true);
        paused = true;
        gridLines = false;
        noStroke();
        drawArray(grid);
        System.out.println("COMMANDS: ");
        System.out.println("'space' - pause");
        System.out.println("'r' - reset grid random");
        System.out.println("'b' - reset grid blank");
        System.out.println("'g' - toggle gridlines");
        System.out.println("'left click' - create alive square at mouse pos. when paused");
        System.out.println("'right click' - create dead square at mouse pos. when paused");
    }

    public void draw() {
        //println(frameRate);
        background(0);
        //update grid (only if !paused)
        grid = updatedLife(grid);
        //draw grid and gridlines
        drawArray(grid);
        if (gridLines) drawGridLines();
        checkMouse();
    }

    public int[][] newArray(int width, int height, boolean random) {
        int[][] a = new int[width][height];
        //randomize each square 0 or 1
        if (random) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    a[x][y] = (int) Math.floor(Math.random()*2);
                }
            }
        }
        //each row filled every square 0
        else {
            for (int x = 0; x < width; x++) {
                Arrays.fill(a[x],0);
            }
        }
        return a;
    }

    public void drawArray(int[][] a) {
        strokeWeight(0);
        for (int x = 0; x < a.length; x++) {
            for (int y = 0; y < a[x].length; y++) {
                //either 255*1 or 255*0
                fill(255*a[x][y]);
                //rectangle in x,y position with res width & height
                rect(x*res,y*res,res,res);
            }
        }
    }

    public void drawGridLines() {
        strokeWeight(1);
        stroke(0,0,200);
        //from top left go diagonal down right by each grid square drawing lines to the right,left,up,down
        for (int i = 0; i < gridWidth+1; i++) {
            PVector p = new PVector((i*res),i*res);
            line(p.x,p.y,p.x,0);
            line(p.x,p.y,p.x,height);
            line(p.x,p.y,0,p.y);
            line(p.x,p.y,width,p.y);
        }
    }

    public int[][] updatedLife(int[][] a) {
        //if paused don't update grid
        if (paused) return a;
        //updated array that will be written to based off of information read in grid array
        int[][] updated = newArray(a.length,a[0].length,false);

        for (int x = 0; x < a.length; x++) {
            for (int y = 0; y < a[x].length; y++) {

                //state of current pixel (0 or 1)
                int state = a[x][y];
                //count of alive neighbors of current pixel
                int neighbors = neighbors(a,x,y);

                //repopulation
                if (state == 0 && neighbors == 3) updated[x][y] = 1;
                //underpopulation or overpopulation
                else if (state == 1 && (neighbors < 2 || neighbors > 3)) updated[x][y] = 0;
                //no change
                else updated[x][y] = state;
            }
        }
        return updated;
    }

    public int neighbors(int[][] a, int x, int y) {
        //number of live neighbors
        int sum = 0;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                //if needed to look for neighbors wrapped around other side of screen (for squares bordering screen)
                int col = (x + i + gridWidth) % gridWidth;
                int row = (y + j + gridHeight) % gridHeight;
                //add the neighbor 0 or 1 to sum
                sum += a[col][row];
            }
        }
        //exclude self, original square at (a[x][y])
        sum -= a[x][y];
        return sum;
    }

    public void keyPressed() {
        //toggle pause (xor)
        if (key == ' ') paused ^= true;
        //set grid = new random array
        if (key == 'r') grid = newArray(grid.length, grid[0].length, true);
        //set grid to new blank array (all 0)
        if (key == 'b') grid = newArray(grid.length, grid[0].length, false);
        //toggle gridlines (xor)
        if (key == 'g') gridLines ^= true;
    }

    public void checkMouse() {
        if (!paused || !mousePressed) return;

        int posX = mouseX / res;
        int posY = mouseY / res;

        if (mouseButton == LEFT && grid[posX][posY] == 0) grid[posX][posY] = 1;
        if (mouseButton == RIGHT && grid[posX][posY] == 1) grid[posX][posY] = 0;
    }

    public static void main(String[] args) {
        PApplet.main("Main");
    }

}
