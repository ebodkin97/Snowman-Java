package snowman;

import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
//Based on the Model class implemented by Dr Maddock in the tutorials. With the addition of the interaction the spotlight has.
//This class contains information for each object that is rendered, sending information to the shaders.
public class Model {
  
  private Mesh mesh;
  protected int[] textureId1; 
  protected int[] textureId2; 
  private Material material;
  protected Shader shader;
  protected Mat4 modelMatrix;
  private Camera camera;
  //Two types of lights interacting with the model. The light refers to the main static world light.
  // the spotlight is the security light that lights up the appropriate part of the model. 
  private Light light;
  private SpotLight spotLight;
  
  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.light = light;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
  }
  
  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1) {
    this(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, null);
  }
  
  public Model(GL3 gl, Camera camera, Light light,Shader shader, Material material, Mat4 modelMatrix, Mesh mesh) {
    this(gl, camera, light, shader, material, modelMatrix, mesh, null, null);
  }
  
  
  //Additional Constructor is allow for setting of the spotlight field.
   public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2, SpotLight spotLight){
       this(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);
       this.spotLight = spotLight;
   }
  
  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }
  
  public void setCamera(Camera camera) {
    this.camera = camera;
  }
  
  public void setLight(Light light) {
    this.light = light;
  }

  public void setSpotLight(SpotLight light) {
    this.spotLight = light;
  }
  
  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    shader.setVec3(gl, "viewPos", camera.getPosition());

    shader.setVec3(gl, "light.position", light.getPosition());
    shader.setVec3(gl, "light.ambient", light.getMaterial().getAmbient());
    shader.setVec3(gl, "light.diffuse", light.getMaterial().getDiffuse());
    shader.setVec3(gl, "light.specular", light.getMaterial().getSpecular());
    if(light.getOn())
    shader.setFloat(gl, "light.on", 1f);
    else
    shader.setFloat(gl, "light.on", 0f);  
    
    //checks if the model has an associated spotlight
    if(spotLight != null){
        //sets the values on the fragment shader for the spotlight.
        shader.setVec3(gl, "spotLight.position", spotLight.getPosition());
        shader.setVec3(gl, "spotLight.ambient", spotLight.getMaterial().getAmbient());
        shader.setVec3(gl, "spotLight.diffuse", spotLight.getMaterial().getDiffuse());
        shader.setVec3(gl, "spotLight.specular", spotLight.getMaterial().getSpecular());
        shader.setVec3(gl, "spotLight.direction", spotLight.getDirection());
        shader.setFloat(gl, "spotLight.cutOff", spotLight.getCutOff());
        shader.setFloat(gl, "spotLight.outerCutOff", spotLight.getOuterCutOff());
        shader.setFloat(gl, "spotLight.constant", spotLight.getConstant());
        shader.setFloat(gl, "spotLight.linear", spotLight.getLinear());
        shader.setFloat(gl, "spotLight.quadratic", spotLight.getQuadratic());
        //Used to turn on and off the spotlight.
        if(spotLight.getOn())
        shader.setFloat(gl, "spotLight.on", 1f);
        else
        shader.setFloat(gl, "spotLight.on", 0f);   
    }

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());  

    if (textureId1!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
    }
    if (textureId2!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
    }
    mesh.render(gl);
  } 
  
  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }
  
  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) gl.glDeleteBuffers(1, textureId1, 0);
    if (textureId2!=null) gl.glDeleteBuffers(1, textureId2, 0);
  }
  
}