package fi.arcada.prog.blindlabyrinth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.HashSet;

/**
 * Created by Linus on 11/18/2014.
 */

public class Map {
    protected Graphic mask;
    protected Graphic background;
    protected Graphic goal;
    protected Bitmap tokenBitmap;

    protected HashSet<Point> collisions = new HashSet<Point>();
    protected HashSet<Rect> tokens = new HashSet<Rect>();
    protected Point start = new Point(0, 0);
    protected int size;
    protected int maxTokens = 0;
    protected int foundTokens = 0;

    int mapPadding;
    public int objectSize; //ie token/goal
    int mode = 0;

    protected int wallColor;

    public Map(Bitmap maskImage, Bitmap backgroundImage, Bitmap goalImage, Bitmap tokenImage, int width, int height, int mapMode, int wallColor) {
        size = width;
        mode = mapMode;
        this.wallColor = wallColor;
        switch(mode) {
            case 1:
                objectSize = size / 25;
                break;
            case 2:
                objectSize = size / 30;
                break;
            case 3:
                objectSize = size / 45;
                break;
            default:
                objectSize = size / 35;
                break;
        }
        mapPadding = (height / 2) - (width / 2);


        tokenBitmap = tokenImage;

        mask = new Graphic(maskImage);
        background = new Graphic(backgroundImage);
        goal = new Graphic(goalImage);

        mask.setSize(size, size);
        mask.setPosition(0, mapPadding);

        background.setSize(size, size);
        background.setPosition(0, mapPadding);

        goal.setSize(objectSize * 2, objectSize * 2);

        parseMask();
    }

    public void release() {
        mask.release();
        background.release();
        goal.release();
        tokenBitmap.recycle();

        collisions.clear();
        tokens.clear();

        start = new Point(0, 0);
        //More coming...
    }

    public void parseMask() {
        int w = mask.image.getWidth();

        Bitmap foreground = Bitmap.createBitmap(w, w, mask.image.getConfig());

        for(int i = 0; i < w; i++) {
            for(int j = 0; j < w; j++) {
                int pixel = mask.image.getPixel(i, j);

                int x = i;
                int y = mapPadding + j;

                if(pixel == Color.BLACK) {
                    collisions.add(new Point(x, y));
                     foreground.setPixel(i, j, wallColor);

                } else if(start.equals(0, 0) && pixel == Color.GREEN) {
                    start.set(x, y);
                    Log.v("Map", "Found start: " + x + ":" + y);

                } else if(goal.position.equals(0, 0) && pixel == Color.RED) {
                    int offset = objectSize / 2;
                    goal.setPosition(x - offset, y - offset);
                    Log.v("Map", "Found goal");

                } else if(pixel == Color.BLUE) {
                    addToken(x, y);
                }
            }
        }
        mask.image.recycle();
        mask.image = foreground;
    }

    public void addToken(int x, int y) {
        for(Rect token: tokens) {
            if(token.contains(x, y)) return;
        }
        int radius = objectSize;
        x += radius / 4;
        tokens.add(new Rect(x  - radius, y - radius, x + radius, y + radius));
        maxTokens++;
        Log.v("Map", "Added token");
    }

    public boolean checkCollision(Point pos) {
        return collisions.contains(pos);
    }

    public boolean isCompleted(RectF ballHitbox) {

        Rect ball = new Rect();
        ballHitbox.round(ball);

        boolean value  = Rect.intersects(goal.getHitbox(), ball);
        if(value) {
            goal.visible = false;
            goal.setPosition(0, 0); //To avoid fireing the event 1000 times
        }
        return value;
    }

    public boolean findsToken(RectF ballHitbox) {

        Rect ball = new Rect();
        ballHitbox.round(ball);

        for(Rect token: tokens) {
            if(Rect.intersects(token, ball)) {
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
        //goalBox.set(goal.x  - radius, goal.y - radius, goal.x + radius, goal.y + radius);
    }

    public int ballSize() {
        return objectSize * 2;
    }

    public int startX() {
        return start.x - objectSize / 2;
    }

    public int startY() {
        return start.y - objectSize / 2;
    }

    public void draw(Canvas c) {
        background.draw(c);
        mask.draw(c);

        for(Rect token: tokens) {
            c.drawBitmap(tokenBitmap, null, token, null);
        }

        goal.draw(c);

        /*Paint b = new Paint();
        b.setColor(Color.BLUE);
        c.drawRect(mapBox, b);
        //Uncomment this to see the collision points (drops framerate to 0, unplayable)
        Paint t = new Paint();
        t.setColor(Color.RED);
        for(Point p: collisions) {
            c.drawPoint(p.x, p.y, t);
        }
*/
    }

}