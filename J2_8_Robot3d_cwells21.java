/*
 * Created on 2004-3-12
 * @author Jim X. Chen: 3D three segments arm transformation
 */
//import net.java.games.jogl.*;
//import javax.media.opengl.*;
import static com.jogamp.opengl.GL4.*;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.awt.GLCanvas;

public class J2_8_Robot3d_cwells21 extends JOGL1_4_5_Circle {

  float O = 0;
  float A = (float)0.3*WIDTH;  //Hand Length
  float B = (float)0.55*WIDTH;
  float C = (float)0.7*WIDTH;
  //int cnt;
  float alpha=-40, beta=-40, gama=60, dalpha = 1f, dbeta = 1.2f, dgama = -2f;
  Cone cone = new Cone();
  private static float my3dMatStack[][][] =
	      new float[24][4][4];
	  static GLCanvas canvas; // drawable in a frame
		static GL4 gl; // handle to OpenGL functions
		static int WIDTH = 800, HEIGHT = 800; // used to set the window size
	  private static int stackPtr = 0;
	  static float vdata[][] = { {1.0f, 0.0f, 0.0f, 0.0f}
	                           , {0.0f, 1.0f, 0.0f, 0.0f}
	                           , {-1.0f, 0.0f, 0.0f, 0.0f}
	                           , {0.0f, -1.0f, 0.0f, 0.0f}
	  };
	  static int cnt = 1;
	  int flip;


  public void display(GLAutoDrawable glDrawable) {

    depth = (cnt/100)%7;
    cnt++;
    alpha += dalpha;
    beta += dbeta;
    gama += dgama;

    
    gl.glClear(gl.GL_COLOR_BUFFER_BIT|gl.GL_DEPTH_BUFFER_BIT);
    
    drawRobot(O, A, B, C, alpha, beta, gama);
  }


  void drawArm(float End1, float End2) {

    float scale = End2-End1;

    //myPushMatrix();
    // the cylinder lies in the z axis;
    // rotate it to lie in the x axis
    //myRotatef(90.0f, 0.0f, 1.0f, 0.0f);
    //myScale(scale/5.0f, scale/5.0f, scale);
    if (cnt%1500<500) {
    	cone.drawCircle(cnt, depth);
    } else if (cnt%1500<1000) {
    	cone.drawCircle(cnt, depth);
    } else {
      //myScale(0.5f, 0.5f, 0.5f);
      //mylTranslatef(0, 0, 1);
    	cone.drawCircle(cnt, depth);
    }
    //myPopMatrix();
  }


  void drawRobot(float O, float A, float B, float C,
                 float alpha, float beta, float gama) {
    // the robot arm is rotating around y axis
    //myRotatef(5.0f, 0.0f, 1.0f, 0.0f);
    //myPushMatrix();

    //myRotate(alpha, 0.0f, 0.0f, 1.0f);
    // R_z(alpha) is on top of the matrix stack
    drawArm(O, A);

    //myTranslate(A, 0.0f, 0.0f);
    //myRotate(beta, 0.0f, 0.0f, 1.0f);
    // R_z(alpha)T_x(A)R_z(beta) is on top of the stack
    drawArm(A, B);

    //myTranslate(B-A, 0.0f, 0.0f);
    //myRotate(gama, 0.0f, 0.0f, 1.0f);
    // R_z(alpha)T_x(A)R_z(beta)T_x(B)R_z(gama) is on top
    drawArm(B, C);

    //myPopMatrix();
  }


  public static void main(String[] args) {
	  J2_8_Robot3d_cwells21 f = new J2_8_Robot3d_cwells21();

    f.setTitle("JOGL J2_8_Robot3d");
    f.setSize(WIDTH, HEIGHT);
    f.setVisible(true);
    my3dLoadIdentity();
	//myClearMatrixs();
  }
  
//the vertices are transformed first then drawn
 public void transDrawTriangle(float[] v1,
                                float[] v2, float[] v3) {
   float v[][] = new float[3][3];

   my3dTransformf(v1, v[0]);
   my3dTransformf(v2, v[1]);
   my3dTransformf(v3, v[2]);
 //gl.glVertex3fv(v[0],0);
  
   int x0 = (int)v[0][0];
   int xn = (int)v[1][0];
   int y0 = (int)v[0][1];
   int yn = (int)v[1][1];
   JOGL1_4_2_Line temp = new JOGL1_4_2_Line();
   temp.line(x0, y0, xn, yn);
   /*
   //gl.glVertex3fv(v[1],0);
   x0 = (int)v[1][0];
   xn = (int)v[2][0];
   y0 = (int)v[1][1];
   yn = (int)v[2][1];
   temp.line(x0, y0, xn, yn);
   x0 = (int)v[2][0];
   xn = (int)v[0][0];
   y0 = (int)v[2][1];
   yn = (int)v[0][1];
   temp.line(x0, y0, xn, yn);
   
  */
   
 }


 // initialize a 3*3 matrix to all zeros
 private static void my3dClearMatrix(float mat[][]) {

   for (int i = 0; i<4; i++) {
     for (int j = 0; j<4; j++) {
       mat[i][j] = 0.0f;
     }
   }
 }


 // initialize a matrix to Identity matrix
 private static void my3dIdentity(float mat[][]) {

   my3dClearMatrix(mat);
   for (int i = 0; i<4; i++) {
     mat[i][i] = 1.0f;
   }
 }


 // initialize the current matrix to Identity matrix
 public static void my3dLoadIdentity() {
   my3dIdentity(my3dMatStack[stackPtr]);
 }


 // multiply the current matrix with mat
 public void my3dMultMatrix(float mat[][]) {
   float matTmp[][] = new float[3][3];

   my3dClearMatrix(matTmp);

   for (int i = 0; i<4; i++) {
     for (int j = 0; j<4; j++) {
       for (int k = 0; k<4; k++) {
         matTmp[i][j] +=
             my3dMatStack[stackPtr][i][k]*mat[k][j];
       }
     }
   }
   // save the result on the current matrix
   for (int i = 0; i<3; i++) {
     for (int j = 0; j<3; j++) {
       my3dMatStack[stackPtr][i][j] = matTmp[i][j];
     }
   }
 }


 // multiply the current matrix with a translation matrix
 public void my3dTranslatef(float x, float y) {
   float T[][] = new float[4][4];

   my3dIdentity(T);

   T[0][2] = x;
   T[1][2] = y;

   my3dMultMatrix(T);
 }


 // multiply the current matrix with a rotation matrix
 public void my3dRotatef(float angle) {
   float R[][] = new float[4][4];

   my3dIdentity(R);

   R[0][0] = (float)Math.cos(angle);
   R[0][1] = (float)-Math.sin(angle);
   R[1][0] = (float)Math.sin(angle);
   R[1][1] = (float)Math.cos(angle);

   my3dMultMatrix(R);
 }


 // multiply the current matrix with a scale matrix
 public void my3dScalef(float x, float y) {
   float S[][] = new float[3][3];

   my3dIdentity(S);

   S[0][0] = x;
   S[1][1] = y;

   my3dMultMatrix(S);
 }


 // v1 = (the current matrix) * v
 // here v and v1 are vertices in homogeneous coord.
 public void my3dTransHomoVertex(float v[], float v1[]) {
   int i, j;

   for (i = 0; i<4; i++) {
     v1[i] = 0.0f;

   }
   for (i = 0; i<3; i++) {
     for (j = 0; j<3; j++) {
       v1[i] +=
           my3dMatStack[stackPtr][i][j]*v[j];
     }
   }
 }


 // vertex = (the current matrix) * vertex
 // here vertex is in homogeneous coord.
 public void my3dTransHomoVertex(float vertex[]) {
     float vertex1[] = new float[3];

   my3dTransHomoVertex(vertex, vertex1);
   for (int i = 0; i<3; i++) {
     vertex[i] = vertex1[i];
   }
 }


 // transform v to v1 by the current matrix
 // here v and v1 are not in homogeneous coordinates
 public void my3dTransformf(float v[], float v1[]) {
   float vertex[] = new float[3];

   // extend to homogenius coord
    vertex[0] = v[0];
   vertex[1] = v[1];
   vertex[2] = 1;

   // multiply the vertex by the current matrix
   my3dTransHomoVertex(vertex);

   // return to 3D coord
   v1[0] = vertex[0]/vertex[2];
   v1[1] = vertex[1]/vertex[2];
 }


 // transform v by the current matrix
  // here v is not in homogeneous coordinates
 public void my3dTransformf(float[] v) {

   float vertex[] = new float[4];

   // extend to homogenius coord
   vertex[0] = v[0];
   vertex[1] = v[1];
   vertex[2] = (float)Math.random();
   vertex[3] = 1;

   // multiply the vertex by the current matrix
   my3dTransHomoVertex(vertex);

   // return to 3D coord
   v[0] = vertex[0]/vertex[2];
   v[1] = vertex[1]/vertex[2];
 }


 // move the stack pointer up, and copy the previous
 // matrix to the current matrix
 public void my3dPushMatrix() {
   int tmp = stackPtr+1;

   for (int i = 0; i<3; i++) {
     for (int j = 0; j<3; j++) {
       my3dMatStack[tmp][i][j] =
           my3dMatStack[stackPtr][i][j];
     }
   }
   stackPtr++;
 }


 // move the stack pointer down
 public void my2dPopMatrix() {

   stackPtr--;
 }

}


