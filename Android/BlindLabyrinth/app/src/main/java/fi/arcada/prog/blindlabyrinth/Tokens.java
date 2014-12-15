package fi.arcada.prog.blindlabyrinth;

import android.graphics.Bitmap;

/**
 * Created by Linus on 12/11/2014.
 */
//Stub that never got developed, there wasn't enough time
public class Tokens extends Graphic {

    protected int size;

    //We just need one instance of the bitmap, hence the use of plural
    public Tokens(Bitmap img, int size) {
        super(img);
        this.size = size;
    }


}
