/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snowman;

import com.jogamp.opengl.GL3;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import gmaths.Mat4;

/**
 *
 * @author coa15ejb
 * 
 * extension of the Model class from Dr Maddocks tutorials. This allows for the animation of two textures.
 */
public class AnimatedTextureModel extends Model {
    
    
    
    public AnimatedTextureModel(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2){
        super(gl,  camera,  light,  shader,  material,  modelMatrix,  mesh, textureId1, textureId2);
    }
    //Animates the first and second textures together(moving downwards).
    public void renderAnimatedTextures(GL3 gl, Mat4 modelMatrix, double elapsedTime) 
    {
         
         render(gl, modelMatrix);
         
         double t = elapsedTime*0.1;  // *0.1 slows it down a bit
        float offsetX = 0.0f;
        float offsetY = (float)(t - Math.floor(t));;
        shader.setFloat(gl, "offset", offsetX, offsetY);
    
        shader.setInt(gl, "first_texture", 0);
        shader.setInt(gl, "second_texture", 1);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
        gl.glActiveTexture(GL.GL_TEXTURE1);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
    }
    
    public void renderAnimatedTextures(GL3 gl, double elapsedTime)
    {
        renderAnimatedTextures(gl, modelMatrix, elapsedTime);
    }
     
}
