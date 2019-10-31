/*
 * Created on 2004-3-12
 * @author Jim X. Chen: 3D three segments arm transformation
 */
//import net.java.games.jogl.*;
//import javax.media.opengl.*;
import static com.jogamp.opengl.GL4.*;

public class J2_8_Robot3d extends Sphere {

  float O = 0;
  float A = (float)0.3*WIDTH;
  float B = (float)0.55*WIDTH;
  float C = (float)0.7*WIDTH;


  public void display(GLAutoDrawable glDrawable) {

    depth = (cnt/100)%7;
    cnt++;
    alpha += dalpha;
    beta += dbeta;
    gama += dgama;

    myClear(GL.GL_COLOR_BUFFER_BIT|
               GL.GL_DEPTH_BUFFER_BIT);
    drawRobot(O, A, B, C, alpha, beta, gama);
  }


  void drawArm(float End1, float End2) {

    float scale = End2-End1;

    myPushMatrix();
    // the cylinder lies in the z axis;
    // rotate it to lie in the x axis
    Rotatef(90.0f, 0.0f, 1.0f, 0.0f);
    myScale(scale/5.0f, scale/5.0f, scale);
    if (cnt%1500<500) {
      drawCylinder();
    } else if (cnt%1500<1000) {
      drawCone();
    } else {
      myScale(0.5f, 0.5f, 0.5f);
      mylTranslatef(0, 0, 1);
      drawSphere();
    }
    myPopMatrix();
  }


  void drawRobot(float O, float A, float B, float C,
                 float alpha, float beta, float gama) {
    // the robot arm is rotating around y axis
    myRotatef(5.0f, 0.0f, 1.0f, 0.0f);
    myPushMatrix();

    myRotate(alpha, 0.0f, 0.0f, 1.0f);
    // R_z(alpha) is on top of the matrix stack
    drawArm(O, A);

    myTranslate(A, 0.0f, 0.0f);
    myRotate(beta, 0.0f, 0.0f, 1.0f);
    // R_z(alpha)T_x(A)R_z(beta) is on top of the stack
    drawArm(A, B);

    myTranslate(B-A, 0.0f, 0.0f);
    myRotate(gama, 0.0f, 0.0f, 1.0f);
    // R_z(alpha)T_x(A)R_z(beta)T_x(B)R_z(gama) is on top
    drawArm(B, C);

    gl.glPopMatrix();
  }


  public static void main(String[] args) {
    J2_8_Robot3d f = new J2_8_Robot3d();

    f.setTitle("JOGL J2_8_Robot3d");
    f.setSize(WIDTH, HEIGHT);
    f.setVisible(true);
  }
}


