package kr.pe.burt.android.dice;

import kr.pe.burt.android.dice.glkit.ShaderProgram;

/**
 * Created by burt on 2016. 6. 23..
 */
public class Cube extends Model {

    static float vertices[] = {
            // Front
             1, -1, 1,      1, 0, 0, 1,     1.0f, 0.0f,      // 0
             1,  1, 1,      0, 1, 0, 1,     1.0f, 1.0f,   // 1
            -1,  1, 1,      0, 0, 1, 1,     0.0f, 1.0f,      // 2
            -1, -1, 1,      0, 0, 0, 1,     0.0f, 0.0f,         // 3

            // Back
            -1, -1, -1,     0, 0, 1, 1,     0.0f, 1.0f,       // 4
            -1,  1, -1,     0, 1, 0, 1,     0.0f, 0.0f,    // 5
             1,  1, -1,     1, 0, 0, 1,     1.0f, 0.0f,   // 6
             1, -1, -1,     0, 0, 0, 1,     1.0f, 1.0f,      // 7

            // Left
            -1, -1,  1,     0, 0, 1, 1,     1.0f, 0.0f,     // 8
            -1,  1,  1,     0, 1, 0, 1,     1.0f, 1.0f,   // 9
            -1,  1, -1,     1, 0, 0, 1,     0.0f, 1.0f,    // 10
            -1, -1, -1,     0, 0, 0, 1,     0.0f, 0.0f,     // 11

            // Right
             1, -1, -1,     1, 0, 0, 1,     1.0f, 0.0f,     // 12
             1,  1, -1,     0, 1, 0, 1,     1.0f, 1.0f,     // 13
             1,  1,  1,     0, 0, 1, 1,     0.0f, 1.0f,   // 14
             1, -1,  1,     0, 0, 0, 1,     0.0f, 0.0f,     // 15

            // Top
             1, 1,  1,      1, 0, 0, 1,     1.0f, 0.0f,   // 16
             1, 1, -1,      0, 1, 0, 1,     1.0f, 1.0f,    // 17
            -1, 1, -1,      0, 0, 1, 1,     0.0f, 1.0f,     // 18
            -1, 1,  1,      0, 0, 0, 1,     0.0f, 0.0f,     // 19

            // Bottom
             1, -1, -1,     1, 0, 0, 1,     1.0f, 0.0f,    // 20
             1, -1,  1,     0, 1, 0, 1,     1.0f, 1.0f,     // 21
            -1, -1,  1,     0, 0, 1, 1,     0.0f, 1.0f,    // 22
            -1, -1, -1,     0, 0, 0, 1,     0.0f, 0.0f,  // 23

    };

    static final short indices[] = {

            // Front
            0, 1, 2,
            2, 3, 0,

            // Back
            4, 5, 6,
            6, 7, 4,

            // Left
            8, 9, 10,
            10, 11, 8,

            // Right
            12, 13, 14,
            14, 15, 12,

            // Top
            16, 17, 18,
            18, 19, 16,

            // Bottom
            20, 21, 22,
            22, 23, 20
    };

/*
    public void SetTextureMigration( float xmin, float xmax, float ymin, float ymax){
        for( int i=0; i<6; i++){
            int slice = i*36;
            vertices[slice + 7] = vertices[slice + 9+7] = xmax;
            vertices[slice + 18+ 7] = vertices[slice + 27 +7] = xmin;
            vertices[slice + 8] = vertices[slice + 27+8] = ymin;
            vertices[slice + 9+8] = vertices[slice + 18+ 8] = ymax;
        }
       // super("cube", shader, vertices, indices);
        //Cube(shader);
       // setupVertexBuffer();
      //  setupIndexBuffer();
    }

*/
    public Cube(ShaderProgram shader) {
        super("cube", shader, vertices, indices);
    }
}
