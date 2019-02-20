package kr.pe.burt.android.dice;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.renderscript.Float3;
import android.renderscript.Matrix4f;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import kr.pe.burt.android.dice.glkit.ShaderProgram;
import kr.pe.burt.android.dice.glkit.ShaderUtils;
import kr.pe.burt.android.dice.glkit.TextureUtils;



public class OGLRenderer implements GLSurfaceView.Renderer {

   // private static final float ONE_SEC = 1000.0f; // 1 second

    private Context context;
    private Cube   cube;
    private   float xx, yy, zz;
    private  int[] textures = new  int[15];

    public float getXx() {
        return xx;
    }

    public void setXx(float xx) {
        this.xx = xx;
    }

    public float getYy() {
        return yy;
    }

    public void setYy(float yy) {
        this.yy = yy;
    }

    public float getZz() {
        return zz;
    }

    public void setZz(float zz) {
        this.zz = zz;
    }

    private  float xrot, yrot, zrot;
    //private long lastTimeMillis = 0L;

    public OGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        ShaderProgram shader = new ShaderProgram(
                ShaderUtils.readShaderFileFromRawResource(context, R.raw.simple_vertex_shader),
                ShaderUtils.readShaderFileFromRawResource(context, R.raw.simple_fragment_shader)
        );

      //  int textureName = TextureUtils.loadTexture(context, R.drawable.dice);
        texture_init();
        cube = new Cube(shader);
        cube.setPosition(new Float3(0.0f, 0.0f, 0.0f));
        cube.setTexture(textures[0]);
      //  cube.setTexture(textureName);

       // lastTimeMillis = System.currentTimeMillis();
    }


    private void  texture_init(){
        textures[0] = TextureUtils.loadTexture(context, R.drawable.nothing);
        textures[1] = TextureUtils.loadTexture(context, R.drawable.d2);
        textures[2] = TextureUtils.loadTexture(context, R.drawable.d4);
        textures[3] = TextureUtils.loadTexture(context, R.drawable.d8);
        textures[4] = TextureUtils.loadTexture(context, R.drawable.d16);
        textures[5] = TextureUtils.loadTexture(context, R.drawable.d32);
        textures[6] = TextureUtils.loadTexture(context, R.drawable.d64);
        textures[7] = TextureUtils.loadTexture(context, R.drawable.d128);
        textures[8] = TextureUtils.loadTexture(context, R.drawable.d256);
        textures[9] = TextureUtils.loadTexture(context, R.drawable.d512);
        textures[10] = TextureUtils.loadTexture(context, R.drawable.d1024);
        textures[11] = TextureUtils.loadTexture(context, R.drawable.d2048);
        textures[12] = TextureUtils.loadTexture(context, R.drawable.error);
        textures[13] = TextureUtils.loadTexture(context, R.drawable.gayower);
        textures[14] = TextureUtils.loadTexture(context, R.drawable.youwin);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        GLES20.glViewport(0, 0, w, h);

        Matrix4f perspective = new Matrix4f();
        perspective.loadPerspective(85.0f, (float)w / (float)h, 1.0f, -150.0f);

        if(cube != null) {
            cube.setProjection(perspective);
        }
    }

    /**
     * GLSurfaceView has default 16bit depth buffer
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        Matrix4f camera2 = new Matrix4f();
        camera2.translate(0.0f, 0.0f, -20.5f);



       // zz = -160f;
       // yy = -160f;

        zrot = (float)(zrot * 0.95 + zz*0.05);
        yrot = (float)(yrot * 0.95 + yy*0.05);
        xrot = (float)(xrot * 0.95 + xx*0.05);

        camera2.rotate(xrot,1,0,0);
        camera2.rotate(yrot,0,1,0);
        camera2.rotate(zrot,0,0,1);

        if(MainActivity.curworld.gamemode== 1){
        camera2.rotate(-90f,0,0,1);
        cube.setCamera(camera2);
       // cube.setRotationX(xrot);
       // cube.setRotationY(yrot);
       // cube.setRotationZ(zrot);
        cube.setScale((float)1.0);

        Float3 newpos = new  Float3(0f, 0f, 0f);
        newpos.x = -3.5f;
        newpos.y = -3.5f;
        newpos.z = -3.5f;
        //cube.SetTextureMigration(0f, 0.25f, 0f, 0.25f);
        //cube.setupVertexBuffer();
        //int textureName = TextureUtils.loadTexture(context, R.drawable.dice);
        //cube.setTexture(textureName);
        for(int x1=0; x1<4;x1++){
            //newpos.x = 2.5f;
            //newpos.y=2.5f;
            //newpos.z=0;
            //newpos.y = -5f;
            for(int y1=0; y1<4;y1++){

                for(int z1=0; z1<4;z1++){
                    int diamond = MainActivity.curworld.matrix[x1][y1][z1];
                    cube.setPosition(newpos);
                    if(diamond !=0) {




                        cube.setTexture(textures[diamond]);



                        cube.draw();
                        cube.setScale((float) 1.0);

                        newpos.x=0;
                        newpos.y=0;
                        newpos.z = 2.75f;
                    }
                    else{
                        newpos.z += 2.75f;
                    }


                        //cube.setTexture(textures[12]);

/*
                    cube.setPosition(newpos);
                    cube.draw();
                    cube.setScale((float) 1.0);

                    newpos.x=0;
                    newpos.y=0;
                    newpos.z = 2.75f;


                    */
                }







                newpos.y += 2.75f;
                newpos.z += -11f;


              //  newpos.x=-7.5f;
            }


            newpos.y+= -11f;
            newpos.x+= 2.75f;

           // newpos.y=-7.5f;

        }


        /*  for(int i=0; i<4;i++) {

            newpos.x = 0.5f;
            newpos.y =(float) 2.5 * i;
            newpos.z = 0.0f;


            cube.setPosition(newpos);

            cube.draw();

            //cube.setRotationX(0);
            //cube.setRotationY(0);
            //cube.setRotationZ(0);


            cube.setScale((float) 1.0);
            newpos.x = 2.5f;
            //newpos.y = -2.5f;
            //newpos.z = -2.5f;

            cube.setPosition(newpos);
            //+cube.setPosition(newpos);

            cube.draw();

            newpos.x = -7.5f;
            //newpos.y = -7.5f;
            cube.setPosition(newpos);
            cube.draw();

            newpos.x = 2.5f;
            //newpos.y = -2.5f;
            //newpos.z = -2.5f;
            cube.setPosition(newpos);
            cube.draw();
        }

/*
        newpos.x = 2.5f;
        //newpos.y = 7.5f;
        //newpos.z = 7.5f;
        cube.setPosition(newpos);
        cube.draw();

        newpos.x = 2.5f;
        //newpos.y = 0f;
        //newpos.z = 2.5f;
        cube.setPosition(newpos);
        cube.draw();

        newpos.x = 2.5f;
        //newpos.y = 2.5f;
        //newpos.z = 0f;
        cube.setPosition(newpos);
        cube.draw();
*/
       /* long currentTimeMillis = System.currentTimeMillis();
        updateWithDelta(currentTimeMillis - lastTimeMillis);
        lastTimeMillis = currentTimeMillis;*/
    }else {
            camera2.translate(0.0f, 0.0f, -20.0f);

            cube.setCamera(camera2);

            cube.setTexture(textures[12]);
            if(MainActivity.curworld.gamemode==2){
                cube.setTexture(textures[14]);
            }
            if(MainActivity.curworld.gamemode==3){
                cube.setTexture(textures[13]);
            }

            cube.setScale(10f);
            cube.draw();
        }
    }


    /*public void updateWithDelta(long dt) {

        Matrix4f camera2 = new Matrix4f();
        camera2.translate(0.0f, 0.0f, -5.0f);
        cube.setCamera(camera2);
        cube.setRotationX((float)(xx));
        cube.setRotationY((float)(yy));
        cube.setRotationZ((float)(zz));
        cube.draw(dt);

    }
*/
}
