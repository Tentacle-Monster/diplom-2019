package kr.pe.burt.android.dice.glkit;

import android.graphics.Color;
import android.opengl.GLES20;
import android.renderscript.Float2;
import android.renderscript.Float3;
import android.renderscript.Float4;
import android.renderscript.Matrix3f;
import android.renderscript.Matrix4f;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

public class ShaderProgram {
    private String vertexShaderSource;
    private String fragmentShaderSource;

    private int vertexShaderHandle;
    private int fragmentShaderHandle;
    private int programHandle;
    private String log = "";
    private boolean isCompiled;

    private HashMap<String, Integer> uniforms = new HashMap<>();
    private HashMap<String, Integer> attributes = new HashMap<>();


    public ShaderProgram(String vertexShader, String fragmentShader) {
        if(vertexShader == null) throw new IllegalArgumentException("vertex shader must not be null");
        if(fragmentShader == null) throw new IllegalArgumentException("fragment shader must not be null");

        vertexShaderSource = vertexShader;
        fragmentShaderSource = fragmentShader;

        compileShaders(vertexShader, fragmentShader);

        if(isCompiled()) {
            fetchAttributes();
            fetchUniforms();
        }
    }

    // is this valid shader program?
    public boolean isValid() {
        return isCompiled;
    }

    // start to use shader program
    public void begin() {
        GLES20.glUseProgram(programHandle);
    }

    // end of using shader program
    public void end() {
        GLES20.glUseProgram(0);
    }

    // destory the shader program
    public void destroy() {
        GLES20.glUseProgram(0);
        GLES20.glDeleteShader(vertexShaderHandle);
        GLES20.glDeleteShader(fragmentShaderHandle);
        GLES20.glDeleteProgram(programHandle);
    }

    private void compileShaders(String vertexShader, String fragmentShader) {
        vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        fragmentShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        if(vertexShaderHandle == -1 || fragmentShaderHandle == -1) {
            isCompiled = false;
            return;
        }

        programHandle = linkProgram(createProgram());
        if(programHandle == -1) {
            isCompiled = false;
            return;
        }

        isCompiled = true;
    }

    private int createProgram() {
        int program = GLES20.glCreateProgram();
        return program != 0 ? program : -1;
    }

    private int linkProgram(int program) {
        if(program == -1)
            return -1;

        GLES20.glAttachShader(program, vertexShaderHandle);
        GLES20.glAttachShader(program, fragmentShaderHandle);
        GLES20.glLinkProgram(program);

        int[] linked = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linked, 0);
        if(linked[0] == GLES20.GL_FALSE) {
            String infoLog = GLES20.glGetProgramInfoLog(program);
            log += infoLog;
            GLES20.glDeleteProgram(program);
            return  -1;
        }
        return program;
    }

    private int loadShader(int shaderType, String shaderCode) {
        int shader = GLES20.glCreateShader(shaderType);
        if(shader == 0)
            return -1;

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

        if(compiled[0] == GLES20.GL_FALSE) {
            String infoLog = GLES20.glGetShaderInfoLog(shader);
            log += infoLog;
            GLES20.glDeleteShader(shader);
            return -1;
        }
        return shader;
    }

    public String getLog() {
        if(isCompiled) {
            log = GLES20.glGetProgramInfoLog(programHandle);
            return log;
        } else {
            return log;
        }
    }

    public boolean isCompiled() {
        return isCompiled;
    }

    private int fetchAttributeLocation(String name) {

        int location = attributes.get(name);
        if(location == -1) {
            location = GLES20.glGetAttribLocation(programHandle, name);
            if(location != -1) {
                attributes.put(name, location);
            }
        }
        return location;
    }

    private int fetchUniformLocation(String name) {
        int location = uniforms.get(name);
        if(location == -1) {
            location = GLES20.glGetUniformLocation(programHandle, name);
            if(location != -1) {
                uniforms.put(name, location);
            }
        }
        return location;
    }


    private void fetchAttributes() {

        attributes.clear();

        IntBuffer params = IntBuffer.allocate(1);
        IntBuffer type = IntBuffer.allocate(1);
        GLES20.glGetProgramiv(programHandle, GLES20.GL_ACTIVE_ATTRIBUTES, params);
        int numAttributes = params.get(0);

        for(int i=0; i<numAttributes; i++) {
            params.compact();
            params.put(0, 1);
            type.clear();
            String name = GLES20.glGetActiveAttrib(programHandle, i, params, type);
            int location = GLES20.glGetAttribLocation(programHandle, name);
            attributes.put(name, location);
        }
    }

    private void fetchUniforms() {

        uniforms.clear();

        IntBuffer params = IntBuffer.allocate(1);
        IntBuffer type = IntBuffer.allocate(1);
        GLES20.glGetProgramiv(programHandle, GLES20.GL_ACTIVE_UNIFORMS, params);
        int numUniform = params.get(0);

        for(int i=0; i<numUniform; i++) {
            params.compact();
            params.put(0, 1);
            type.clear();
            String name = GLES20.glGetActiveUniform(programHandle, i, params, type);
            int location = GLES20.glGetUniformLocation(programHandle, name);
            uniforms.put(name, location);
        }
    }

    public int getUniformLocation(String name) {
        return uniforms.get(name) == null ? -1 : uniforms.get(name);
    }

    public int getAttributeLocation(String name) {
        return attributes.get(name) == null ? -1 : attributes.get(name);
    }
 public boolean hasUniform(String name) {
        return uniforms.containsKey(name);
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    public void setUniformi(String name, int value) {
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniform1i(location, value);
    }

    public void setUniformi(int location, int value) {
        if(location == -1) return;
        GLES20.glUniform1i(location, value);
    }

    public void setUniformi (String name, int value1, int value2) {
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniform2i(location, value1, value2);
    }

    public void setUniformi (int location, int value1, int value2) {
        if(location == -1) return;
        GLES20.glUniform2i(location, value1, value2);
    }

    public void setUniformi (String name, int value1, int value2, int value3) {
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniform3i(location, value1, value2, value3);

    }

    public void setUniformi (int location, int value1, int value2, int value3) {
        if(location != -1)
            GLES20.glUniform3i(location, value1, value2, value3);
    }

    public void setUniformi (String name, int value1, int value2, int value3, int value4) {
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniform4i(location, value1, value2, value3, value4);
    }

    public void setUniformi (int location, int value1, int value2, int value3, int value4) {
        if(location == -1) return;
        GLES20.glUniform4i(location, value1, value2, value3, value4);
    }

    public void setUniformf (String name, float value) {
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniform1f(location, value);
    }

    public void setUniformf (int location, float value) {
        if(location == -1) return;
        GLES20.glUniform1f(location, value);
    }

    public void setUniformf (String name, float value1, float value2) {
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniform2f(location, value1, value2);
    }

    public void setUniformf (int location, float value1, float value2) {
        if(location == -1) return;
        GLES20.glUniform2f(location, value1, value2);
    }

    public void setUniformf (String name, float value1, float value2, float value3) {
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniform3f(location, value1, value2, value3);
    }

    public void setUniformf (int location, float value1, float value2, float value3) {
        if(location == -1) return;
        GLES20.glUniform3f(location, value1, value2, value3);
    }

    public void setUniformf (String name, float value1, float value2, float value3, float value4) {
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniform4f(location, value1, value2, value3, value4);
    }

    public void setUniformf (int location, float value1, float value2, float value3, float value4) {
        if(location == -1) return;
        GLES20.glUniform4f(location, value1, value2, value3, value4);
    }

    public void setUniform1fv (String name, float[] values, int offset, int length) {
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniform1fv(location, length, values, offset);
    }

    public void setUniform1fv (int location, float[] values, int offset, int length) {
        if(location == -1) return;
        GLES20.glUniform1fv(location, length, values, offset);
    }

    public void setUniform2fv (String name, float[] values, int offset, int length) {
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniform2fv(location, length / 2, values, offset);
    }

    public void setUniform2fv (int location, float[] values, int offset, int length) {
        if(location == -1) return;
        GLES20.glUniform2fv(location, length / 2, values, offset);
    }

    public void setUniform3fv (String name, float[] values, int offset, int length) {
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniform3fv(location, length / 3, values, offset);
    }

    public void setUniform3fv (int location, float[] values, int offset, int length) {
        if(location == -1) return;
        GLES20.glUniform3fv(location, length / 3, values, offset);
    }

    public void setUniform4fv (String name, float[] values, int offset, int length) {
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniform4fv(location, length / 4, values, offset);
    }

    public void setUniform4fv (int location, float[] values, int offset, int length) {
        if(location == -1) return;
        GLES20.glUniform4fv(location, length / 4, values, offset);
    }

    public void setUniformMatrix (String name, Matrix4f matrix) {
        setUniformMatrix(name, matrix, false);
    }
    public void setUniformMatrix (String name, Matrix4f matrix, boolean transpose) {
        setUniformMatrix(fetchUniformLocation(name), matrix, transpose);
    }

    public void setUniformMatrix (int location, Matrix4f matrix) {
        setUniformMatrix(location, matrix, false);
    }

    public void setUniformMatrix (int location, Matrix4f matrix, boolean transpose) {
        if(location == -1) return;
        GLES20.glUniformMatrix4fv(location, 1, transpose, matrix.getArray(), 0);
    }

  public void setUniformMatrix (String name, Matrix3f matrix) {
        setUniformMatrix(name, matrix, false);
    }

    public void setUniformMatrix (String name, Matrix3f matrix, boolean transpose) {
        setUniformMatrix(fetchUniformLocation(name), matrix, transpose);
    }

    public void setUniformMatrix (int location, Matrix3f matrix) {
        setUniformMatrix(location, matrix, false);
    }

    public void setUniformMatrix (int location, Matrix3f matrix, boolean transpose) {
        if(location == -1) return;
        GLES20.glUniformMatrix3fv(location, 1, transpose, matrix.getArray(), 0);
    }

    public void setUniformMatrix3fv (String name, FloatBuffer buffer, int count, boolean transpose) {
        buffer.position(0);
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniformMatrix3fv(location, count, transpose, buffer);
    }
public void setUniformMatrix4fv (String name, FloatBuffer buffer, int count, boolean transpose) {
        buffer.position(0);
        int location = fetchUniformLocation(name);
        if(location == -1) return;
        GLES20.glUniformMatrix4fv(location, count, transpose, buffer);
    }

    public void setUniformMatrix4fv (int location, float[] values, int offset, int length) {
        if(location == -1) return;
        GLES20.glUniformMatrix4fv(location, length / 16, false, values, offset);
    }

    public void setUniformMatrix4fv (String name, float[] values, int offset, int length) {
        setUniformMatrix4fv(fetchUniformLocation(name), values, offset, length);
    }

    public void setUniformf (String name, Float2 vector) {
        setUniformf(name, vector.x, vector.y);
    }

    public void setUniformf (int location, Float2 vector) {
        setUniformf(location, vector.x, vector.y);
    }
    public void setUniformf (String name, Float3 vector) {
        setUniformf(name, vector.x, vector.y, vector.z);
    }

    public void setUniformf (int location, Float3 vector) {
        setUniformf(location, vector.x, vector.y, vector.z);
    }

    public void setUniformf (String name, Float4 vector) {
        setUniformf(name, vector.x, vector.y, vector.z, vector.w);
    }

    public void setUniformf (int location, Float4 vector) {
        setUniformf(location, vector.x, vector.y, vector.z, vector.w);
    }

    public void setUniformColor (String name, int color) {
        setUniformf(name, Color.red(color) / 255.0f, Color.green(color) / 255.0f, Color.blue(color) / 255.0f, Color.alpha(color) / 255.0f);
    }

    public void setUniformf (int location, int color) {
        setUniformf(location, Color.red(color) / 255.0f, Color.green(color) / 255.0f, Color.blue(color) / 255.0f, Color.alpha(color) / 255.0f);
    }

    public void setVertexAttribute (String name, int size, int type, boolean normalize, int stride, Buffer buffer) {
        int location = fetchAttributeLocation(name);
        if (location == -1) return;
        GLES20.glVertexAttribPointer(location, size, type, normalize, stride, buffer);
    }

    public void setVertexAttribute (int location, int size, int type, boolean normalize, int stride, Buffer buffer) {
        if(location == -1) return;
        GLES20.glVertexAttribPointer(location, size, type, normalize, stride, buffer);
    }

    public void setVertexAttribute (String name, int size, int type, boolean normalize, int stride, int offset) {
        int location = fetchAttributeLocation(name);
        if (location == -1)
            return;
        GLES20.glVertexAttribPointer(location, size, type, normalize, stride, offset);
    }

    public void setVertexAttribute (int location, int size, int type, boolean normalize, int stride, int offset) {
        if(location == -1) return;
        GLES20.glVertexAttribPointer(location, size, type, normalize, stride, offset);
    }
    public void disableVertexAttribute (String name) {
        int location = fetchAttributeLocation(name);
        if (location == -1) return;
        GLES20.glDisableVertexAttribArray(location);
    }

    public void disableVertexAttribute (int location) {
        if(location == -1) return;
        GLES20.glDisableVertexAttribArray(location);
    }

    public void enableVertexAttribute (String name) {
        int location = fetchAttributeLocation(name);
        if (location == -1) return;
        GLES20.glEnableVertexAttribArray(location);
    }

    public void enableVertexAttribute (int location) {
        if(location == -1) return;
        GLES20.glEnableVertexAttribArray(location);
    }

    public void setAttributef (String name, float value1, float value2, float value3, float value4) {
        int location = fetchAttributeLocation(name);
        if(location == -1) return;
        GLES20.glVertexAttrib4f(location, value1, value2, value3, value4);
    }

    public void setAttributef (String name, float value1, float value2, float value3) {
        int location = fetchAttributeLocation(name);
        if(location == -1) return;
        GLES20.glVertexAttrib3f(location, value1, value2, value3);
    }

    public void setAttributef (String name, float value1, float value2) {
        int location = fetchAttributeLocation(name);
        if(location == -1) return;
        GLES20.glVertexAttrib2f(location, value1, value2);
    }
}
