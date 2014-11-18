package fi.arcada.prog.blindlabyrinth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Linus on 11/14/2014.
 */
public class Controller {
    private Graphic arrowUp;
    private Graphic arrowRight;
    private Graphic arrowDown;
    private Graphic arrowLeft;

    private Point basePos = new Point();

    private Point direction = new Point();

    public Controller(Bitmap u, Bitmap r, Bitmap d, Bitmap l) {
        arrowUp = new Graphic(u);
        arrowRight = new Graphic(r);
        arrowDown = new Graphic(d);
        arrowLeft = new Graphic(l);
        setPosition(20, 20);
    }

    public void setPosition(int x, int y) {
        basePos.x = x;
        basePos.y = y;

        arrowUp.setPosition(basePos.x + 60, basePos.y);
        arrowRight.setPosition(basePos.x + 180, basePos.y + 60);
        arrowDown.setPosition(basePos.x + 60, basePos.y + 180);
        arrowLeft.setPosition(basePos.x, basePos.y + 60);
    }

    public Point getDirection() {
        return direction;
    }

    public void draw(Canvas c) {
        arrowUp.draw(c);
        arrowRight.draw(c);
        arrowDown.draw(c);
        arrowLeft.draw(c);
    }

    public void updateDirection(float x, float y) {
        if(arrowUp.contains(x, y)) {
            direction.y = -1;
        } else if(arrowDown.contains(x, y)) {
            direction.y = 1;
        } else {
            direction.y = 0;
        }
        if(arrowLeft.contains(x, y)) {
            direction.x = -1;
        } else if(arrowRight.contains(x, y)) {
            direction.x = 1;
        } else {
            direction.x = 0;
        }
    }

    public void resetDirection() {
        direction.x = 0;
        direction.y = 0;
    }
}
