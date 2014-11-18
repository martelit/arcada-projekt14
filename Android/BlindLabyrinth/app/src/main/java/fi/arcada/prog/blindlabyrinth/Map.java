package fi.arcada.prog.blindlabyrinth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
    HashSet<Point> collisions = new HashSet<Point>();
    Point start;
    Point end;

    public Map(Bitmap maskImage, Bitmap skinImage) {
        mask = new Graphic(maskImage);
        skin = new Graphic(skinImage);

        parseCollisions();
    }

    public void parseCollisions() {
        int w = mask.image.getWidth();
        int h = mask.image.getHeight();

        for(int i = 0; i < w; i++) {
            for(int j = 0; j < h; j++) {
                if(mask.image.getPixel(i, j) == Color.BLACK) {
                    collisions.add(new Point(i, j));
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
    }

}