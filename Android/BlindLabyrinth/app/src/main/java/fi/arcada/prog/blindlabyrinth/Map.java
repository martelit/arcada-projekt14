package fi.arcada.prog.blindlabyrinth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Linus on 11/18/2014.
 */

public class Map {
    protected Graphic mask;
    protected Graphic skin;
    protected HashSet<Point> collisions = new HashSet<Point>();
    protected HashSet<Rect> tokens = new HashSet<Rect>();
    protected Point start = new Point(0, 0);
    protected Point goal = new Point(0, 0);
    protected Rect goalBox = new Rect();
    protected int size;
    protected int maxTokens = 0;
    protected int foundTokens = 0;

    int mapPadding = 20; //no idea how to calculate this in a dynamic manner
    int objectSize; //ie token/goal

    public Map(Bitmap maskImage, Bitmap skinImage, Bitmap goalImage, Bitmap tokenImage, int mapSize) {
        size = mapSize - (mapPadding * 2);
        objectSize = size / 30;

        mask = new Graphic(maskImage);
        skin = new Graphic(skinImage);

        mask.setSize(size, size);
        skin.setSize(size, size);

        parseMask();
    }

    public void parseMask() {
        int w = mask.image.getWidth();
        int h = mask.image.getHeight();

        for(int i = 0; i < w; i++) {
            for(int j = 0; j < h; j++) {
                int pixel = mask.image.getPixel(i, j);

                int x = i + mapPadding;
                int y = j + mapPadding;

                if(pixel == Color.BLACK) {
                    collisions.add(new Point(x, y));

                } else if(start.equals(0, 0) && pixel == Color.GREEN) {
                    start.set(x, y);
                    Log.v("Map", "Found start");

                } else if(goal.equals(0, 0) && pixel == Color.RED) {
                    goal.set(x, y);
                    createGoal();
                    Log.v("Map", "Found goal");

                } else if(pixel == Color.BLUE) {
                    addToken(x, y);
                }
            }
        }
    }

    public void addToken(int x, int y) {
        for(Rect token: tokens) {
            if(token.contains(x, y)) return;
        }
        int radius = objectSize;
        tokens.add(new Rect(x  - radius, y - radius, x + radius, y + radius));
        maxTokens++;
        Log.v("Map", "Added token");
    }

    public boolean checkCollision(Point pos) {
        return collisions.contains(pos);
    }

    public boolean isCompleted(Point pos) {
        return goalBox.contains(pos.x, pos.y);
    }

    public boolean findsToken(Point pos) {
        for(Rect token: tokens) {
            if(token.contains(pos.x, pos.y)) {
                tokens.remove(token);
                foundTokens++;
                return true;
            }
        }
        return false;
    }

    //this could be made more dynamic later
    //ie. looking for the narrowest gap in collision points and creating the hitbox within these constraints
    public void createGoal() {
        int radius = objectSize;
        goalBox.set(goal.x  - radius, goal.y - radius, goal.x + radius, goal.y + radius);
    }

    public int startX() {
        return start.x;
    }

    public int startY() {
        return start.y;
    }

    public void draw(Canvas c) {
        skin.draw(c);


        Paint tp = new Paint();
        tp.setColor(Color.BLUE);
        for(Rect token: tokens) {
            c.drawRect(token, tp);
        }

        Paint gp = new Paint();
        gp.setColor(Color.RED);
        c.drawRect(goalBox, gp);

        //Uncomment this to see the collision points (drops framerate to 0, unplayable)
        /*Paint t = new Paint();
        t.setColor(Color.RED);
        for(Point p: collisions) {
            c.drawPoint(p.x, p.y, t);
        }*/
    }

}