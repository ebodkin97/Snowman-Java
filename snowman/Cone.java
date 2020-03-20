/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snowman;
import gmaths.*;
/**
 *
 * @author coa15ejb
 * Vertices and indicies defined for a square base pyramid.
 */
public class Cone {
    
    public static final float[] vertices = {      // position, colour, tex coords
    -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.25f, 0.0f,  // top left 0
    -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.5f, 0.0f,  // bottom left 1 
    0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.75f, 0.0f,  // bottom right 2
     0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f,   // top right 3
     0.0f, 0.5f,  0.0f,  0.0f, 1.0f, 0.0f,  0.5f, 1.0f   // 4
    }; 
    
    
    public static final int[] indices = {
      0, 1, 2,
      0, 2, 3,
      0, 1, 8,
      1, 2, 8,
      2, 3, 8,
      3, 4, 8,
      4, 5, 8,
      5, 6, 8,
      6, 7, 8,
      7, 0, 8
    };
}
