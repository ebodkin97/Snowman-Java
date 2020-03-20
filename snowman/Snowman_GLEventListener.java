/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snowman;

import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import java.io.File;

/**
 *
 * @author coa15ejb
 */
public class Snowman_GLEventListener implements GLEventListener {
    
  //Fields
  private static final boolean DISPLAY_SHADERS = false;

  private double savedTime = 0;
  private Camera camera;
  private Mat4 perspective;
  private Model floor, hatHorn, woolSphere, dirtySnowballs, coalSpheres,shinyObject, spotlightPole, spotlightTop;
  private AnimatedTextureModel background;
  private Light light;
  private SpotLight spotLight;
  private SGNode snowmanRoot;
  
  private float xPosition = 0;
  private TransformNode translateX, translateZ, snowmanMoveTranslate, snowmanRock,snowmanRotateHead, snowmanRoll, leftArmRotate, rightArmRotate;
  
  //booleans used to control the animation of the snowman.
  private boolean isRolled = false;
  private boolean isRockedX = false;
  private boolean isRockedZ = false;
  private boolean isRockedXZ = false;
  private boolean isMove = false;

  private float bodyScale = 3f;
  private float headScale = 2f;
  
  //times stored to allow the animation to take off where it left off.
  private double startTime;
  private double startTimeRockX;
  private double startTimeRockZ;
  private double startTimeRockXZ;
  private double startTimeRoll;
  private double startTimeSlide;
  
  private double savedTimeRockX;
  private double savedTimeRockZ;
  private double savedTimeRockXZ;

  private double savedTimeRoll;  
  private double savedTimeSlide;
  
  public Snowman_GLEventListener(Camera camera) {
      //inits the camera in a start position.
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    woolSphere.dispose(gl);
    dirtySnowballs.dispose(gl);
    shinyObject.dispose(gl);
    spotlightPole.dispose(gl);

  }
  
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
  //sets the state for each of the animation booleans. along with assigning their saved times etc
  public void rockBodyX(){
      if(isRockedX)
      {
        isRockedX = false;
        double elapsedTime = getSeconds()-startTimeRockX;
        savedTimeRockX = elapsedTime;
      }else{
        isRockedX = true;
        startTimeRockX = getSeconds()-savedTimeRockX;
      }
  }
  public void rockBodyZ(){
      if(isRockedZ)
      {
        isRockedZ = false;
        double elapsedTime = getSeconds()-startTimeRockZ;
        savedTimeRockZ = elapsedTime;
      }else{
        isRockedZ = true;
        startTimeRockZ = getSeconds()-savedTimeRockZ;
      }
  }
  
  public void rockBodyXZ(){
      if(isRockedXZ)
      {
        isRockedXZ = false;
        double elapsedTime = getSeconds()-startTimeRockXZ;
        savedTimeRockXZ = elapsedTime;
      }else{
        isRockedZ = true;
        startTimeRockXZ = getSeconds()-savedTimeRockXZ;
      }
  }
  
  
  public void rollHead(){
      if(isRolled){    
        isRolled = false;
        double elapsedTime = getSeconds()-startTimeRoll;
        savedTimeRoll = elapsedTime;
        
      } else{        
        isRolled = true;
        startTimeRoll = getSeconds()-savedTimeRoll;
      }
  }
  
  public void slideAround(){
    if(isMove){    
        isMove = false;
        double elapsedTime = getSeconds()-startTimeSlide;
        savedTimeSlide = elapsedTime;
        
      } else{        
        isMove = true;
        savedTimeSlide = getSeconds()-startTimeSlide;
      }
}
  
  public void ToggleLight(){
      light.toggleOn();
  }
  public void ToggleSpotlight(){
      spotLight.toggleOn();
  }
  
  public void Reset(){
      isMove = false;
      isRolled = false;
      isRockedXZ = false;
      isRockedX = false;
      isRockedZ = false;
      
      resetPositions();
      
  }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  
  
  private void initialise(GL3 gl) {
    //scales for eyes mouth, nose etc done for easy changing at a later time.
    float eyeScale = 0.2f;
    
    float mouthWidth = 0.7f;
    float mouthScale = 0.2f;
    
    float noseLength = 0.5f;
    float noseScale = 0.2f;
    
    float hatBaseScale = 2.5f;    
    float hatTopScale = 1.75f;
    float hatTopHeight = 2.0f;   
    float buttonScale = 0.2f;
    float hatFeatherHeight = 1f;
    float hatFeatherScale = 0.2f;  
      
    //loading in all appropriate textures
    int[] blackWoolTexId = TextureLibrary.loadTexture(gl, "Textures/blackWool.jpg");
    int[] whiteWoolTexId= TextureLibrary.loadTexture(gl, "Textures/whiteWool.jpg");    
    int[] dirtySnowTexId = TextureLibrary.loadTexture(gl, "Textures/DirtySnow.jpg");
    int[] coalTexId = TextureLibrary.loadTexture(gl, "Textures/coal2.jpg");
    int[] winterSceneTexId = TextureLibrary.loadTexture(gl, "Textures/winterScene.jpg");    
    int[] snowTexId = TextureLibrary.loadTexture(gl, "Textures/snow.jpg");
    int[] snowingTexId = TextureLibrary.loadTexture(gl, "Textures/snowing.jpg");
    int[] diffuseShinyObjTexId = TextureLibrary.loadTexture(gl, "Textures/GlazedBricks_diffuse.jpg");
    int[] specShinyObjTexId = TextureLibrary.loadTexture(gl, "Textures/GlazedBricks_specular.jpg");
    int[] metalTexId= TextureLibrary.loadTexture(gl, "Textures/metal.jpg");
    
    //shader strings
    String multiLightFragShader = "Shaders/fs_multipleLightShader.txt";
    String staticVertShader = "Shaders/vs_cube_04.txt";
    String animatedVertShader = "Shaders/vs_tt_background.txt";
    String animatedFragShader = "Shaders/fs_background.txt";
    
    //sets up the lighting positions etc.
    light = new Light(gl);
    light.setCamera(camera);
    Vec3 spotLightPos = new Vec3(-6f,10.5f,7f);
    spotLight = new SpotLight(gl,spotLightPos);
    spotLight.setCamera(camera);
    
    //inits the models assigning the correct shaders, textures etc for each model.
    //mesh is inited as as plane (twotriangles).
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, staticVertShader, multiLightFragShader);    
    Material material = new Material(new Vec3(1.0f, 1f, 1f), new Vec3(1.0f, 1f, 1f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(20,1f,20);
    //normal model that interacts with the spotlight and has no specular component.
    floor = new Model(gl, camera, light, shader, material, modelMatrix, mesh, snowTexId,null,spotLight);
    
    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, animatedVertShader,animatedFragShader);
    material = new Material(new Vec3(1f, 1f, 1f), new Vec3(1f, 1f, 1f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90),Mat4Transform.scale(20f,1f,20f));
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,10,-10f), modelMatrix);
    //uses a different model to animate the falling snow.
    background = new AnimatedTextureModel(gl, camera, light, shader, material, modelMatrix, mesh, winterSceneTexId, snowingTexId);    
    
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, staticVertShader, multiLightFragShader);
    material = new Material(new Vec3(1.0f, 0.5f, 0.5f), new Vec3(1.0f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), 4f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    woolSphere = new Model(gl, camera, light, shader, material, modelMatrix, mesh, blackWoolTexId, null,spotLight);    
    //shader is the same for the rest of the objects so no need to reassign.
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    material = new Material(new Vec3(1.0f, 1f,1f), new Vec3(1.0f, 1.0f, 1f), new Vec3(1f, 1f, 1f), 1f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    dirtySnowballs = new Model(gl, camera, light, shader, material, modelMatrix, mesh, dirtySnowTexId, null,spotLight);
      
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    material = new Material(new Vec3(1.0f, 0.5f, 0.5f), new Vec3(1.0f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), 0.1f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    coalSpheres = new Model(gl, camera, light, shader, material, modelMatrix, mesh, coalTexId, null,spotLight);
    
    //shiny object
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    shinyObject = new Model(gl, camera, light,shader, material, modelMatrix, mesh, diffuseShinyObjTexId, specShinyObjTexId, spotLight);
    
    //pole
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    spotlightPole = new Model(gl, camera, light,shader,material, modelMatrix, mesh, metalTexId);
   
    mesh = new Mesh(gl, Cone.vertices.clone(), Cone.indices.clone());
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 4f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    hatHorn = new Model(gl, camera, light, shader, material, modelMatrix, mesh, whiteWoolTexId);
   
     
    //creates the root node
    snowmanRoot = new NameNode("root");
    //movement node for sliding around.
    snowmanMoveTranslate = new TransformNode("snowman transform",Mat4Transform.translate(xPosition,0f,0));
    //node for the rocking motion.
    snowmanRock = new TransformNode("snowman rock",Mat4Transform.rotateAroundZ(0));
    //translates immediately the body up to above the plane.
    TransformNode snowmanTranslate = new TransformNode("snowman transform",Mat4Transform.translate(0,bodyScale*0.5f,0));
    
    //defines the nodes associated with the main snowman body.
    NameNode body = new NameNode("body");
      TransformNode bodyTranslate = new TransformNode("body translate", Mat4Transform.translate(0,0,0));      
      Mat4 m = Mat4Transform.scale(bodyScale,bodyScale,bodyScale);      
      TransformNode bodyTransform = new TransformNode("body transform", m);
        //ditry snowball model.
        ModelNode bodyShape = new ModelNode("Sphere(body)", dirtySnowballs);
    
    //define the nodes associated with the head snowball     
    NameNode head = new NameNode("head");       
      TransformNode headTranslate = new TransformNode("head translate",  Mat4Transform.translate(0, 0.5f*bodyScale + 0.5f*headScale,0));
      //these are transfmation nodes that allow for the rolling head movement.
      snowmanRoll = new TransformNode("head Roll", Mat4Transform.rotateAroundZ(0));
      snowmanRotateHead = new TransformNode("head Roll", Mat4Transform.rotateAroundZ(0));
      TransformNode headTransform = new TransformNode("head transform", Mat4Transform.scale(headScale,headScale,headScale));
        ModelNode headShape = new ModelNode("Sphere(head)", dirtySnowballs);
    
    NameNode leftEye = new NameNode("leftEye");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0.3f,0.1f,0.5f*headScale));
        m = Mat4.multiply(m, Mat4Transform.scale(eyeScale,eyeScale,eyeScale));
        TransformNode leftEyeTransform = new TransformNode("head transform", m);
            ModelNode leftEyeShape = new ModelNode("Sphere(leftEye)", coalSpheres);
            
    NameNode rightEye = new NameNode("rightEye");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-0.3f,0.1f,0.5f*headScale));
        m = Mat4.multiply(m, Mat4Transform.scale(eyeScale,eyeScale,eyeScale));
        TransformNode rightEyeTransform = new TransformNode("head transform", m);
            ModelNode rightEyeShape = new ModelNode("Sphere(leftEye)", coalSpheres);
    
    NameNode mouth = new NameNode("mouth");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0,-0.4f,0.5f*headScale));
        m = Mat4.multiply(m, Mat4Transform.scale(mouthWidth,mouthScale,mouthScale));
        TransformNode mouthTransform = new TransformNode("mouth Transform", m);
            ModelNode mouthShape = new ModelNode("Sphere(mouth)", coalSpheres);
            
    NameNode nose = new NameNode("nose");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0,-0.2f,0.5f*headScale));
        m = Mat4.multiply(m, Mat4Transform.scale(noseScale,noseScale,noseLength));
        TransformNode noseTransform = new TransformNode("nose Transform", m);
            ModelNode noseShape = new ModelNode("Sphere(mouth)", coalSpheres);        
            
    NameNode hatBase = new NameNode("hatBase");
        TransformNode hatBaseTranslate = new TransformNode("hat Base translate",Mat4Transform.translate(0,0.5f*headScale,0));
        m = new Mat4(1);        
        m = Mat4.multiply(m, Mat4Transform.scale(hatBaseScale,0.2f,hatBaseScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,-2f,0));        
        TransformNode hatBaseTransform = new TransformNode("hatBase Transform", m);
            ModelNode hatBaseShape = new ModelNode("Sphere(hatbase)", woolSphere);
            
    NameNode hatTop = new NameNode("hatTop");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0,hatTopHeight,0));
        m = Mat4.multiply(m, Mat4Transform.scale(hatTopScale,hatTopHeight,hatTopScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,-1f,0));
        TransformNode hatTopTransform = new TransformNode("hatTop Transform", m);
            ModelNode hatTopShape = new ModelNode("Sphere(hatTop)", woolSphere);
    
    NameNode button1 = new NameNode("button1");
        m = new Mat4(1);         
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0.5f*bodyScale));
        m = Mat4.multiply(m, Mat4Transform.scale(buttonScale,buttonScale,buttonScale));
        TransformNode button1Transform = new TransformNode("button1 Transform", m);
            ModelNode button1Shape = new ModelNode("Sphere(button1)", coalSpheres);
            
    NameNode button2 = new NameNode("button2");
        m = new Mat4(1); 
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0.5f*bodyScale-0.1f));
        m = Mat4.multiply(m, Mat4Transform.scale(buttonScale,buttonScale,buttonScale));     
        TransformNode button2Transform = new TransformNode("button2 Transform", m);
            ModelNode button2Shape = new ModelNode("Sphere(button2)", coalSpheres);
            
    NameNode button3 = new NameNode("button3");
        m = new Mat4(1);         
        m = Mat4.multiply(m, Mat4Transform.translate(0,1f, 0.5f*bodyScale- 0.4f));
        m = Mat4.multiply(m, Mat4Transform.scale(buttonScale,buttonScale,buttonScale));     
        TransformNode button3Transform = new TransformNode("button3 Transform", m);
            ModelNode button3Shape = new ModelNode("Sphere(button3)", coalSpheres);         
            
    NameNode shinyObj = new NameNode("shinyObj");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(7f,3f, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(3f,6f,3f));     
         TransformNode shinyObjTransform = new TransformNode("crate Transform", m);
            ModelNode shinyObjShape = new ModelNode("shiny Obj", shinyObject);
            
    NameNode pole = new NameNode("pole");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-7f,6f,7f));
        m = Mat4.multiply(m, Mat4Transform.scale(0.5f,12f,0.5f));     
         TransformNode poleTransform = new TransformNode("pole Transform", m);
            ModelNode poleShape = new ModelNode("pole", spotlightPole);
            
    NameNode poleTop = new NameNode("poleTop");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-7f,10.5f,7f));
        m = Mat4.multiply(m, Mat4Transform.scale(2f,0.5f,0.5f));     
         TransformNode poleTopTransform = new TransformNode("pole top Transform", m);
            ModelNode poleTopShape = new ModelNode("pole Top", spotlightPole);                
    
    NameNode hatHornLeft = new NameNode("Hat horn left");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-0.5f,0.5f,0f));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(45));
        m = Mat4.multiply(m, Mat4Transform.scale(0.5f,2f,0.5f));     
         TransformNode leftHornTransform = new TransformNode("left Hat horn", m);
            ModelNode leftHornShape = new ModelNode("left hat horn", hatHorn); 
            
    NameNode hatHornRight  = new NameNode("Hat horn right");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0.5f,0.5f,0f));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(-45));
        m = Mat4.multiply(m, Mat4Transform.scale(0.5f,2f,0.5f));    
         TransformNode rightHornTransform = new TransformNode("right Hat horn", m);
            ModelNode rightHornShape = new ModelNode("right Hat horn", hatHorn);         
    //Defines the hierarchical structure to allow for easy use of transformations and animation.
    snowmanRoot.addChild(snowmanTranslate);    
      snowmanTranslate.addChild(snowmanMoveTranslate);      
        snowmanMoveTranslate.addChild(body);
      snowmanMoveTranslate.addChild(snowmanRock);  
        snowmanRock.addChild(body);
        //this is a general set up when a model is added as a child, this is then transformed into the correct place/position
        //and then transformed into the correct model.
        body.addChild(bodyTranslate);
            bodyTranslate.addChild(bodyTransform);
            bodyTransform.addChild(bodyShape);
        bodyTranslate.addChild(snowmanRoll);
            snowmanRoll.addChild(head);
            head.addChild(headTranslate); 
            headTranslate.addChild(snowmanRotateHead);
            snowmanRotateHead.addChild(headTransform);                
            headTransform.addChild(headShape); 
            snowmanRotateHead.addChild(leftEye);
                leftEye.addChild(leftEyeTransform);
                    leftEyeTransform.addChild(leftEyeShape);
            snowmanRotateHead.addChild(rightEye);
                rightEye.addChild(rightEyeTransform);
                    rightEyeTransform.addChild(rightEyeShape);
            snowmanRotateHead.addChild(mouth);
                mouth.addChild(mouthTransform);
                    mouthTransform.addChild(mouthShape);
            snowmanRotateHead.addChild(nose);
                nose.addChild(noseTransform);
                    noseTransform.addChild(noseShape);
            snowmanRotateHead.addChild(hatBase);
                hatBase.addChild(hatBaseTranslate);
                    hatBaseTranslate.addChild(hatBaseTransform);
                    hatBaseTransform.addChild(hatBaseShape);
                hatBaseTranslate.addChild(hatTop);
                    hatTop.addChild(hatTopTransform);
                        hatTopTransform.addChild(hatTopShape);
                      hatTop.addChild(hatHornLeft);
                        hatHornLeft.addChild(leftHornTransform);
                            leftHornTransform.addChild(leftHornShape);
                      hatTop.addChild(hatHornRight);
                        hatHornRight.addChild(rightHornTransform);
                            rightHornTransform.addChild(rightHornShape);
        body.addChild(button1);
            button1.addChild(button1Transform);
                button1Transform.addChild(button1Shape);
        body.addChild(button2);
            button2.addChild(button2Transform);
                button2Transform.addChild(button2Shape);
        body.addChild(button3);
            button1.addChild(button3Transform);
                button3Transform.addChild(button3Shape);
                
    snowmanRoot.addChild(shinyObj);
        shinyObj.addChild(shinyObjTransform);
            shinyObjTransform.addChild(shinyObjShape);
    snowmanRoot.addChild(pole);
        pole.addChild(poleTransform);
            poleTransform.addChild(poleShape);
        pole.addChild(poleTop);
            poleTop.addChild(poleTopTransform);
            poleTopTransform.addChild(poleTopShape);
   
                
            
    snowmanRoot.update();  // IMPORTANT - don't forget this
    //robotRoot.print(0, false);
    //System.exit(0);
  }
 
  //renders and draws the scene while updating various values.
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);    
    //renders the light
    light.render(gl);    
    //moves the spotlight direction
    MoveSpotlight();
    //renders the spotlight
    spotLight.render(gl);
    floor.render(gl);
    //renders the changing textures.
    background.renderAnimatedTextures(gl, getSeconds());
    
    //animats according to what has been clicked.
    if (isRolled) rollHeadAnimated();
    if(isRockedZ && isRockedX){
        rockBodyXZAnimated();
    }else
    {
    if(isRockedZ) rockBodyZAnimated();
    if(isRockedX) rockBodyXAnimated();
    }
    if(isMove) slideAroundAnimated();
    //draws the heirarchical structure.
    snowmanRoot.draw(gl);
  }
  
  //moves the spotlight around
  private void MoveSpotlight(){
    //varies the spotlight direction.   
    float spotlightXVal = 12f*(float)Math.sin(getSeconds());
    float spotlightZVal = 4f*(float)Math.cos(getSeconds());
    //varies the spotlight direction around the central point (where the snowman is!)
    spotLight.setDirection((6f-(spotlightXVal)),(-10.5f),(-7f+(spotlightZVal)));
  }
  
  private void RotateSpotlightAroundCentre(){
      //varies the spotlight direction.   
    float spotlightXVal = 12f*(float)Math.sin(getSeconds());
    float spotlightZVal = 12f*(float)Math.cos(getSeconds());
    //varies the spotlight direction around the central point (where the snowman is!)
    spotLight.setDirection((6f-(spotlightXVal)),(-10.5f),(-7f+(spotlightZVal)));
  }
  
  //animates the snowman rolling his head around and around.
  private void rollHeadAnimated(){
        //varys the angle being rotated.
        double elapsedTime = getSeconds()-startTimeRoll;
        float rotateAngleZ = 20f*(float)Math.sin(elapsedTime+180f);
        float rotateAngleX = 20f*(float)Math.cos(elapsedTime+90f);
        
        Mat4 snowmanRollMat = Mat4Transform.rotateAroundZ(rotateAngleZ);
        snowmanRollMat = Mat4.multiply(snowmanRollMat, Mat4Transform.rotateAroundX(rotateAngleX));
        snowmanRoll.setTransform(snowmanRollMat);
        snowmanRoll.update();
        // this acts as the rotation ration (2:3) - the head when rolling will 
        //rotate 1.5 times faster to seem like its rolling down the side of the body ball.
        Mat4 snowmanHeadRotateMat = Mat4Transform.rotateAroundZ(rotateAngleZ* (bodyScale/headScale));        
        snowmanHeadRotateMat = Mat4.multiply(snowmanHeadRotateMat, Mat4Transform.rotateAroundX(rotateAngleX* (bodyScale/headScale)));
        snowmanRotateHead.setTransform(snowmanHeadRotateMat); 
        snowmanRotateHead.update();
  }
  //rocks the body side to side
  private void rockBodyZAnimated(){
    double elapsedTime = getSeconds()-startTimeRockZ;    
    //including the sin inside give the speed changes in the rocking .
    float rotateAngleZ = 40f*(float)Math.sin(elapsedTime + (10*Math.sin(0.3*elapsedTime)));
    Mat4 snowmanRockZMat = Mat4Transform.rotateAroundZ(rotateAngleZ);
    
    snowmanRock.setTransform(snowmanRockZMat);
    snowmanRock.update();
  }
  
  private void rockBodyXAnimated(){
    double elapsedTime = getSeconds()-startTimeRockX;
    //including the sin inside give the speed changes in the rocking .
    float rotateAngleX = 40f*(float)Math.sin(elapsedTime + (10*Math.sin(0.3*elapsedTime)));
    Mat4 snowmanRockXMat = Mat4Transform.rotateAroundX(rotateAngleX);   
    snowmanRock.setTransform(snowmanRockXMat);
    snowmanRock.update();
  }
  //rocks in both x and z
  private void rockBodyXZAnimated(){     
    double elapsedTimeX = getSeconds()-startTimeRockX;
    double elapsedTimeZ = getSeconds()-startTimeRockZ;
    float rotateAngleX = 40f*(float)Math.sin(elapsedTimeX + (10*Math.sin(0.3*elapsedTimeX)));
    float rotateAngleZ = 40f*(float)Math.sin(elapsedTimeZ + (10*Math.sin(0.2*elapsedTimeZ)));
    Mat4 snowmanRockXZMat = Mat4Transform.rotateAroundX(rotateAngleX);
    snowmanRockXZMat = Mat4.multiply(snowmanRockXZMat, Mat4Transform.rotateAroundZ(rotateAngleZ));
    snowmanRock.setTransform(snowmanRockXZMat);
    snowmanRock.update();
  }
  
  //slides the snowman around in the x and z planes
  private void slideAroundAnimated(){
      double elapsedTime = getSeconds() - startTimeSlide;
      float xMovement = 3f*(float)Math.sin(elapsedTime);
      float zMovement = 3f*(float)Math.cos((0.5*elapsedTime)+90);
      
      Mat4 snowmanSlideXZMat = Mat4Transform.translate(xMovement, 0f, zMovement);
      snowmanMoveTranslate.setTransform(snowmanSlideXZMat);
      snowmanMoveTranslate.update();
  }
  //resets the snowman back to the origin with no transform affecting it.
  private void resetPositions(){
        Mat4 snowmanRollMat = Mat4Transform.rotateAroundZ(0);
        snowmanRollMat = Mat4.multiply(snowmanRollMat, Mat4Transform.rotateAroundX(0));
        snowmanRoll.setTransform(snowmanRollMat);
        snowmanRoll.update();
        Mat4 snowmanHeadRotateMat = Mat4Transform.rotateAroundZ(0);
        snowmanHeadRotateMat = Mat4.multiply(snowmanHeadRotateMat, Mat4Transform.rotateAroundX(0));
        snowmanRotateHead.setTransform(snowmanHeadRotateMat); 
        snowmanRotateHead.update();
        Mat4 snowmanRockXZMat = Mat4Transform.rotateAroundX(0);   
        snowmanRock.setTransform(snowmanRockXZMat);
        snowmanRock.update();
        Mat4 snowmanSlideXZMat = Mat4Transform.translate(0, 0, 0);
        snowmanMoveTranslate.setTransform(snowmanSlideXZMat);
        snowmanMoveTranslate.update();
  }
  
  //gets the current seconds.
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }
    
}
