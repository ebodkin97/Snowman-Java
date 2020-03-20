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
/**
 *
 * @author coa15ejb
 * 
 * The spotlight is an extension of the light class. This implements the variables as
 * discussed in joey de vries tutorial on spotlights. 
 */
public class SpotLight extends Light {
    //fields used to implement a spolight effect.
    private Vec3 direction;
    private float cutOff;
    private float outerCutOff;
  
    private float constant;
    private float linear;
    private float quadratic;
  

    
    //Constructor for the spotlight, giving initial values.
    public SpotLight(GL3 gl, Vec3 position){        
        super(gl, position);
        cutOff =(float)Math.cos(Math.toRadians(12.5));
        outerCutOff =(float)Math.cos(Math.toRadians(15));       
        quadratic = 0.0032f;
        constant = 0.8f;
        linear = 0.008f;
        direction = new Vec3(7f,-10.5f,-5f);
    }
    
    //Gets and sets for additional fields.
    public void setDirection(float x, float y, float z){
        direction = new Vec3(x, y, z);
    }
    
    public void setDirection(Vec3 dir){
        direction = dir;
    }
    
    public Vec3 getDirection(){
        return direction;
    }
    public void setCutOff(float cutOff){
        this.cutOff = cutOff; 
    }
    
    public float getCutOff(){
        return cutOff;
    }
    public void setOuterCutOff(float outerCutOff){
        this.outerCutOff = outerCutOff;
    }
    
    public float getOuterCutOff(){
        return outerCutOff;
    }
    public void setConstant(float constant){
        this.constant = constant;
    }
    
    public float getConstant(){
        return constant;
    }
    public void setLinear(float linear){
        this.linear = linear;
    }
    
    public float getLinear(){
        return linear;
    }
    
    public void setQuadratic(float quadratic){
        this.quadratic = quadratic;
    }
    
    public float getQuadratic(){
        return quadratic;
    }
    
    
    
}
