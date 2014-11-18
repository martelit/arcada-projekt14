package fi.arcada.prog.blindlabyrinth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Linus on 11/14/2014.
 */
public class Graphic {

    protected Bitmap image;
    protected RectF hitbox = new RectF();
    protected PointF dimension = new PointF();

    public Graphic(Bitmap img) {
        image = img;
        dimension.x = image.getWidth();
        dimension.y = image.getHeight();
        setPosition(20, 20);
    }

    public void setSize(int width, int height) {
        dimension.x = width;
        dimension.y = height;
    }

    public void setSize(PointF size) {
        dimension = size;
    }

    public void setPosition(float x, float y) {
        hitbox.set(x, y, x + dimension.x, y + dimension.y);
    }

    public void setPosition(PointF pos) {
        hitbox.set(pos.x, pos.y, pos.x + dimension.x, pos.y + dimension.y);
    }

    public void draw(Canvas c) {
        c.drawBitmap(image, null, hitbox, null);
    }

    public RectF getHitbox()
    {
        return hitbox;
    }

    public boolean contains(float x, float y) {

        if(hitbox.contains(x, y)) {
            //so the hitbox contains the pixels, lets make sure that it isn't transparent

            int relativeX = Math.round(hitbox.left - x);
            int relativeY = Math.round(hitbox.top - y);
            Log.d("XY", Integer.toString(relativeX) + ":" + Integer.toString(relativeY));

            //int color = image.getPixel(x, y);
            //boolean transparent = (color & 0xff000000) == 0x0;
            //if(!transparent) return true;
            return true;
        }
        return false;
    }
}
