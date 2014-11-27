package fi.arcada.prog.blindlabyrinth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Linus on 11/14/2014.
 */
public class Graphic {

    protected Bitmap image;
    protected Rect hitbox = new Rect();
    protected Point dimension = new Point();
    protected Point position = new Point();

    public Graphic(Bitmap img) {
        image = img;
        dimension.x = image.getWidth();
        dimension.y = image.getHeight();
        setPosition(20, 20);
    }

    public void setSize(int width, int height) {
        dimension.x = width;
        dimension.y = height;
        image = Bitmap.createScaledBitmap(image, width, height, true);
        updateHitbox();
    }


    public void setPosition(int x, int y) {
        position.set(x, y);
        updateHitbox();
    }

    public void updateHitbox() {
        hitbox.set(position.x, position.y, position.x + dimension.x, position.y + dimension.y);
    }

    public void draw(Canvas c) {
        c.drawBitmap(image, null, hitbox, null);
    }

    public Rect getHitbox()
    {
        return hitbox;
    }

    public boolean contains(int x, int y) {

        if(hitbox.contains(x, y)) {
            //so the hitbox contains the pixels, lets make sure that it isn't transparent

            //int relativeX = Math.round(hitbox.left - x);
            //int relativeY = Math.round(hitbox.top - y);
            //Log.d("XY", Integer.toString(relativeX) + ":" + Integer.toString(relativeY));

            //int color = image.getPixel(x, y);
            //boolean transparent = (color & 0xff000000) == 0x0;
            //if(!transparent) return true;
            return true;
        }
        return false;
    }
}
