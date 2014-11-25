package fi.arcada.prog.blindlabyrinth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Linus on 11/18/2014.
 */

public class Map {
    Graphic mask;
    Graphic skin;
    GameView view;
    HashSet<Point> collisions = new HashSet<Point>();
    Point start;
    Point end;

    int padding = 20; //no idea how to calculate this in a dynamic manner


    public Map(Bitmap maskImage, Bitmap skinImage, GameView view) {
        mask = new Graphic(maskImage);
        skin = new Graphic(skinImage);
        //mask.setSize(view.getWidth(), view.getHeight());
        //skin.setSize(view.getWidth(), view.getHeight());

        parseCollisions();
    }

    public void parseCollisions() {
        int w = mask.image.getWidth();
        int h = mask.image.getHeight();

        for(int i = 0; i < w; i++) {
            for(int j = 0; j < h; j++) {
                if(mask.image.getPixel(i, j) == Color.BLACK) {
                    collisions.add(new Point(padding + i, padding + j));
                }
            }
        }
    }

    public boolean checkCollision(Point pos) {
        if(collisions.contains(pos)) return true;
        return false;
    }

    public void draw(Canvas c) {
        skin.draw(c);

        //Uncomment this to see the collision points (drops framerate to 0, unplayable)
        /*Paint t = new Paint();
        t.setColor(Color.RED);
        for(Point p: collisions) {
            c.drawPoint(p.x, p.y, t);
        }*/
    }

}