package kr.pe.burt.android.dice;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by burt on 2016. 6. 15..
 */
public class OGLView extends GLSurfaceView {

    public OGLView(Context context) {
        super(context);
        init();
    }
    public OGLRenderer thisrend;

    public OGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // use opengl es 2.0
        setEGLContextClientVersion(2);

        // store opengl context
        setPreserveEGLContextOnPause(true);

        // set renderer
        thisrend = new OGLRenderer(getContext());
        setRenderer(thisrend);
    }

}
