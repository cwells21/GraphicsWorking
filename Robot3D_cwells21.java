import java.util.Stack;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

public class Robot3D_cwells21 extends JOGL1_4_5_Circle{
	
	float O = 0;	//Shoulder
	float A = (float)0.3*WIDTH;  //Hand Length
	float B = (float)0.55*WIDTH;	//Forearm length
	float C = (float)0.7*WIDTH;	//Arm
	int cnt;
	float alpha=-40, beta=-40, gama=60, dalpha = 1f, dbeta = 1.2f, dgama = -2f;
	float dt = 0; //Change in time
	float dx = 0; //Change in x
	float dy = 0; //Change in y
	float dz = 0; //Change in z
	ObjectManipulate om;
	private static float my3dMatStack[][][] = new float[100][4][4];
	private static int stackPtr = 0;
	//static float vdata[][] = { {1.0f, 0.0f, 0.0f, 0.0f}, {0.0f, 1.0f, 0.0f, 0.0f}, {-1.0f, 0.0f, 0.0f, 0.0f}, {0.0f, -1.0f, 0.0f, 0.0f} };

	float ROTATE_MATRIX[] = new float[16];
	float TRANSFORM_MATRIX[] = new float[16];
	float IDENTITY_MATRIX[] = { 1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1};
	float SCALE_MATRIX[] = new float[16];
	float PROJECTION_Matrix[] = new float [16]; 
	float MODELVIEW_Matrix[] = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};



	public void display(GLAutoDrawable glDrawable) 
	{

		depth = 4;
		
	    gl.glClear(gl.GL_COLOR_BUFFER_BIT|gl.GL_DEPTH_BUFFER_BIT);
	    System.out.println(MODELVIEW_Matrix.toString());

	    // connect the modelview matrix
	    int mvLoc = gl.glGetUniformLocation(vfPrograms,  "mv_matrix"); 
	    gl.glProgramUniformMatrix4fv(vfPrograms, mvLoc,  1,  false, MODELVIEW_Matrix, 0);

	    
	    
	    drawRobot(O, A, B, C, alpha, beta, gama, glDrawable);
	    dt++;
	   // my3dLoadIdentity();
		myClearMatrixs();
	  }
	
	private void my3dIdentity(float mat[][]) {

	    my3dClearMatrix(mat);
	    for (int i = 0; i<4; i++) {
	      mat[i][i] = 1.0f;
	    }
	  }
	
	public void my3dLoadIdentity() {
	    //my3dIdentity(my3dMatStack[stackPtr]);
	  }

	private void my3dClearMatrix(float mat[][]) {

	    for (int i = 0; i<4; i++) {
	      for (int j = 0; j<4; j++) {
	        mat[i][j] = 0.0f;
	      }
	    }
	  }

	
	void drawArm(float End1, float End2) {

	    float scale = End2-End1;

	    myPushMatrix();
	    // the cylinder lies in the z axis;
	    // rotate it to lie in the x axis
	    myRotatex(90.0f);
		myRotatey(0.0f);
		myRotatez(1.0f);
	   // myRotate(90.0f, 0.0f, 1.0f, 0.0f);
	    myScale(scale/5.0f, scale/5.0f, scale, 0.0f);
	    for ( int i = 0; i < 16; i++)
	    {
	    	System.out.print(MODELVIEW_Matrix[i]);
	    }
	    System.out.println();
	    if (cnt%1500<500) {
	    	//drawCone();
	    	//drawCone();
	    	drawCircle(200,4);
	    } else if (cnt%1500<1000) {
	    	drawCircle(200,4);
	    	//drawCone();
	    } else {
	    	myScale(0.5f, 0.5f, 0.5f, 0.0f);
	    	myTranslate(0, 0, 1.0f, 0.0f);
	    	drawCircle(200,4);
	    	//drawCone();
	    }
	    myClearMatrixs();
	    myPopMatrix();
	  }

	
	
	  void drawRobot(float O, float A, float B, float C,
	                 float alpha, float beta, float gama, GLAutoDrawable glDrawable) {
	    // the robot arm is rotating around y axis
		  myRotatex(5.0f);
		  myRotatey(0.0f);
		  myRotatez(0.0f);
		//myRotate(5.0f, 0.0f, 1.0f, 0.0f);
	    myPushMatrix();
		
		
	    myRotatex(alpha);
		myRotatey(0.0f);
		myRotatez(0.0f);
	    //myRotate(alpha, 0.0f, 0.0f, 1.0f);
	    // R_z(alpha) is on top of the matrix stack
	    drawArm(O, A);

	    myTranslate(A, 0.0f, 0.0f, 0.0f);
	    myRotatex(beta);
		myRotatey(0.0f);
		myRotatez(0.0f);
	    //myRotate(beta, 0.0f, 0.0f, 1.0f);
	    // R_z(alpha)T_x(A)R_z(beta) is on top of the stack
	    drawArm(A, B);

	    myTranslate(B-A, 0.0f, 0.0f, 0.0f);
	    myRotatex(gama);
		myRotatey(0.0f);
		myRotatez(0.0f);
	   // myRotate(gama, 0.0f, 0.0f, 1.0f);
	    // R_z(alpha)T_x(A)R_z(beta)T_x(B)R_z(gama) is on top
	    drawArm(B, C);
	    myClearMatrixs();
	    myPopMatrix();
	  }


	 public static void main(String[] args) 
	 {
		 Robot3D_cwells21 f = new Robot3D_cwells21();

		 f.setTitle("JOGL J2_8_Robot3d");
		 f.setSize(WIDTH, HEIGHT);
		 f.setVisible(true);
	 }
	 
	 public void myPushMatrix()
	 {
		 System.out.println("Pushing");
		 int temp = stackPtr = 1;
		 for ( int i = 0; i < 4; i++)
		 {
			 for ( int j = 0; j < 4; j++)
			 {
				 my3dMatStack[temp][i][j] = my3dMatStack[stackPtr][i][j];
			 }
		 }
		 stackPtr++;
		 
		 
	 }
	 
	 public void myPopMatrix()
	 {
		 System.out.println("Popping");
		 stackPtr--;
		 
		 
	 }
	 
	 public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) 
	 {
		 
			float l, r, b, t, n, f; 
			
			l = -width/2; r = width/2; 
			b = -height/2; t = height/2; 
			n = -width; f = width; 
			
			PROJECTION_Matrix[0] = 2/(r-l);              PROJECTION_Matrix[1] = 0;             PROJECTION_Matrix[2] = 0;              PROJECTION_Matrix[3] = 0; 
			PROJECTION_Matrix[4] = 0;                    PROJECTION_Matrix[5] = 2/(t-b);       PROJECTION_Matrix[6] = 0;              PROJECTION_Matrix[7] = 0; 
			PROJECTION_Matrix[8] = 0;                    PROJECTION_Matrix[9] = 0;             PROJECTION_Matrix[10] = -2/(f-n);      PROJECTION_Matrix[11] = 0; 
			PROJECTION_Matrix[12] = -(r+l)/(r-l);        PROJECTION_Matrix[13] = -(t+b)/(t-b); PROJECTION_Matrix[14] = -(f+n)/(f-n);  PROJECTION_Matrix[15] = 1; 

			// connect the projection matrix
			int projLoc = gl.glGetUniformLocation(vfPrograms,  "proj_matrix"); 
			gl.glProgramUniformMatrix4fv(vfPrograms, projLoc,  1,  false, PROJECTION_Matrix, 0);
			
			System.out.println("b) reshape is called whenever the frame is resized.");
			
	}
	 
	public void myRotatex( float xVal)
	{
		//x matrix for the multiplication
		float rotx[] = new float[16];
		xVal = (float)( xVal*3.14)/180;
		rotx[0] = 1.0f;
		rotx[1] = 0.0f;
		rotx[2] = 0.0f;
		rotx[3] = 0.0f;
		rotx[4] = 0.0f;
		rotx[5] = (float)( Math.cos(xVal * dt));
		rotx[6] = (-1) * (float)(Math.sin(xVal * dt));
		rotx[7] = 0;
		rotx[8] = 0;
		rotx[9] = (float)(Math.sin(xVal * dt));
		rotx[10] = (float)( Math.cos(xVal * dt));
		rotx[11] = 0;
		rotx[12] = 0;
		rotx[13] = 0;
		rotx[14] = 0;
		rotx[15] = 1;
		multMatrix(rotx);
		//int rotxLoc = gl.glGetUniformLocation(vfPrograms,  "rotation_matrixx"); 
		//gl.glProgramUniformMatrix4fv(vfPrograms, rotxLoc,  1,  false, rotx, 0);
	}
	
	
	public void myRotatey( float yVal)
	{
		//x matrix for the multiplication
		yVal = (float)( yVal*3.14)/180;
		float roty[] = new float[16];	
		//y matrix for the multiplication
		roty[0] = (float)( Math.cos(yVal * dt));
		roty[1] = 0.0f;
		roty[2] = (float)(Math.sin(yVal * dt));
		roty[3] = 0.0f;
		roty[4] = 0.0f;
		roty[5] = 1.0f;
		roty[6] = 0.0f;
		roty[7] = 0.0f;
		roty[8] = (-1) * (float)(Math.sin(yVal * dt));
		roty[9] = 0;
		roty[10] = (float)( Math.cos(yVal * dt));
		roty[11] = 0;
		roty[12] = 0;
		roty[13] = 0;
		roty[14] = 0;
		roty[15] = 1.0f;
		multMatrix(roty);
		//int rotyLoc = gl.glGetUniformLocation(vfPrograms,  "rotation_matrixy"); 
		//gl.glProgramUniformMatrix4fv(vfPrograms, rotyLoc,  1,  false, roty, 0);
		
	}
	
	public void myRotatez( float zVal)
	{
		float rotz[] = new float[16];
		zVal = (float)( zVal*3.14)/180;
		//z matrix for the multiplication
		rotz[0] = (float)( Math.cos(zVal * dt));
		rotz[1] = (-1) * (float)(Math.sin(zVal * dt));
		rotz[2] = 0.0f;
		rotz[3] = 0.0f;
		rotz[4] = (float)(Math.sin(zVal * dt));
		rotz[5] = (float)( Math.cos(zVal * dt));
		rotz[6] = 0.0f;
		rotz[7] = 0.0f;
		rotz[8] = 0.0f;
		rotz[9] = 0.0f;
		rotz[10] = 1.0f;
		rotz[11] = 0;
		rotz[12] = 0;
		rotz[13] = 0;
		rotz[14] = 0;
		rotz[15] = 1;
		
		/*
		float temp1 = ( Xw1 * Yw1) + ( Xw2 * Yx1) + ( Xw3 * Yy1) + ( Xw4 * Yz1);
		float temp2 = ( Xw1 * Yw2) + ( Xw2 * Yx2) + ( Xw3 * Yy2) + ( Xw4 * Yz2);
		float temp3 = ( Xw1 * Yw3) + ( Xw2 * Yx3) + ( Xw3 * Yy3) + ( Xw4 * Yz3);
		float temp4 = ( Xw1 * Yw4) + ( Xw2 * Yx4) + ( Xw3 * Yy4) + ( Xw4 * Yz4);
		
		float temp5 = ( Xx1 * Yw1) + ( Xx2 * Yx1) + ( Xx3 * Yy1) + ( Xx4 * Yz1);
		float temp6 = ( Xx1 * Yw2) + ( Xx2 * Yx2) + ( Xx3 * Yy2) + ( Xx4 * Yz2);
		float temp7 = ( Xx1 * Yw3) + ( Xx2 * Yx3) + ( Xx3 * Yy3) + ( Xx4 * Yz3);
		float temp8 = ( Xx1 * Yw4) + ( Xx2 * Yx4) + ( Xx3 * Yy4) + ( Xx4 * Yz4);
		
		float temp9 = ( Xy1 * Yw1) + ( Xy2 * Yx1) + ( Xy3 * Yy1) + ( Xy4 * Yz1);
		float temp10 = ( Xy1 * Yw2) + ( Xy2 * Yx2) + ( Xy3 * Yy2) + ( Xy4 * Yz2);
		float temp11 = ( Xy1 * Yw3) + ( Xy2 * Yx3) + ( Xy3 * Yy3) + ( Xy4 * Yz3);
		float temp12 = ( Xy1 * Yw4) + ( Xy2 * Yx4) + ( Xy3 * Yy4) + ( Xy4 * Yz4);
		
		float temp13 = ( Xz1 * Yw1) + ( Xz2 * Yx1) + ( Xz3 * Yy1) + ( Xz4 * Yz1);
		float temp14 = ( Xz1 * Yw2) + ( Xz2 * Yx2) + ( Xz3 * Yy2) + ( Xz4 * Yz2);
		float temp15 = ( Xz1 * Yw3) + ( Xz2 * Yx3) + ( Xz3 * Yy3) + ( Xz4 * Yz3);
		float temp16 = ( Xz1 * Yw4) + ( Xz2 * Yx4) + ( Xz3 * Yy4) + ( Xz4 * Yz4);
		
		
		float tempMatrix[] = new float [16];
		tempMatrix[0] = (temp1 * Zw1) + ( temp2 * Zx1) + ( temp3 * Zy1) + ( temp4 * Zz1);
		tempMatrix[1] = (temp1 * Zw2) + ( temp2 * Zx2) + ( temp3 * Zy2) + ( temp4 * Zz2);
		tempMatrix[2] = (temp1 * Zw3) + ( temp2 * Zx3) + ( temp3 * Zy3) + ( temp4 * Zz3);
		tempMatrix[3] = (temp1 * Zw4) + ( temp2 * Zx4) + ( temp3 * Zy4) + ( temp4 * Zz4);
		tempMatrix[4] = (temp5 * Zw1) + ( temp6 * Zx1) + ( temp7 * Zy1) + ( temp8 * Zz1);
		tempMatrix[5] = (temp5 * Zw2) + ( temp6 * Zx2) + ( temp7 * Zy2) + ( temp8 * Zz2);
		tempMatrix[6] = (temp5 * Zw3) + ( temp6 * Zx3) + ( temp7 * Zy3) + ( temp8 * Zz3);
		tempMatrix[7] = (temp5 * Zw4) + ( temp6 * Zx4) + ( temp7 * Zy4) + ( temp8 * Zz4);
		tempMatrix[8] = (temp9 * Zw1) + ( temp10 * Zx1) + ( temp11 * Zy1) + ( temp12 * Zz1);
		tempMatrix[9] = ( temp9 * Zw2) + ( temp10 * Zx2) + ( temp11 * Zy2) + ( temp12 * Zz2);
		tempMatrix[10] = ( temp9 * Zw3) + ( temp10 * Zx3) + ( temp11 * Zy3) + ( temp12 * Zz3);
		tempMatrix[11] = ( temp9 * Zw4) + ( temp10 * Zx4) + ( temp11 * Zy4) + ( temp12 * Zz4);
		tempMatrix[12] = ( temp13 * Zw1) + ( temp14 * Zx1) + ( temp15 * Zy1) + ( temp16 * Zz1);
		tempMatrix[13] = ( temp13 * Zw2) + ( temp14 * Zx2) + ( temp15 * Zy2) + ( temp16 * Zz2);
		tempMatrix[14] = ( temp13 * Zw3) + ( temp14 * Zx3) + ( temp15 * Zy3) + ( temp16 * Zz3);
		tempMatrix[15] = ( temp13 * Zw4) + ( temp14 * Zx4) + ( temp15 * Zy4) + ( temp16 * Zz4);
		*/
		multMatrix(rotz);
		//int rotzLoc = gl.glGetUniformLocation(vfPrograms,  "rotation_matrixz"); 
		//gl.glProgramUniformMatrix4fv(vfPrograms, rotzLoc,  1,  false, rotz, 0);
		
		System.out.println("b) rotation is called whenever the frame is resized.");
	}
	
	public void myScale( float x, float y, float z, float alpha) 
	 {
		//SCALE_MATRIX[]
		SCALE_MATRIX[0] = x;
		SCALE_MATRIX[1] = 0.0f;
		SCALE_MATRIX[2] = 0.0f;
		SCALE_MATRIX[3] = 0.0f;
		
		SCALE_MATRIX[4] = 0.0f;
		SCALE_MATRIX[5] = y;
		SCALE_MATRIX[6] = 0.0f;
		SCALE_MATRIX[7] = 0.0f;
		
		SCALE_MATRIX[8] = 0.0f;
		SCALE_MATRIX[9] = 0.0f;
		SCALE_MATRIX[10] = z;
		SCALE_MATRIX[11] = 0.0f;
		
		SCALE_MATRIX[12] = 0.0f;
		SCALE_MATRIX[13] = 0.0f;
		SCALE_MATRIX[14] = 0.0f;
		SCALE_MATRIX[15] = 0.0f;
			// connect the projection matrix
		//int scaleLoc = gl.glGetUniformLocation(vfPrograms,  "scale_matrix"); 
			//gl.glProgramUniformMatrix4fv(vfPrograms, scaleLoc,  1,  false, SCALE_MATRIX, 0);
			
			System.out.println("b) scale is called whenever the frame is resized.");
			multMatrix(SCALE_MATRIX);
	}
	
	public void myTranslate(float x, float y, float z, float a)
	{
		TRANSFORM_MATRIX[0] = x;
		TRANSFORM_MATRIX[1] = 0.0f;
		TRANSFORM_MATRIX[2] = 0.0f;
		TRANSFORM_MATRIX[3] = 0.0f;
		
		TRANSFORM_MATRIX[4] = 0.0f;
		TRANSFORM_MATRIX[5] = y;
		TRANSFORM_MATRIX[6] = 0.0f;
		TRANSFORM_MATRIX[7] = 0.0f;
		
		TRANSFORM_MATRIX[8] = 0.0f;
		TRANSFORM_MATRIX[9] = 0.0f;
		TRANSFORM_MATRIX[10] = z;
		TRANSFORM_MATRIX[11] = 0.0f;
		
		TRANSFORM_MATRIX[12] = 0.0f;
		TRANSFORM_MATRIX[13] = 0.0f;
		TRANSFORM_MATRIX[14] = 0.0f;
		TRANSFORM_MATRIX[15] = 1.0f;
		
		//int moveLoc = gl.glGetUniformLocation(vfPrograms,  "translation_matrix"); 
		//gl.glProgramUniformMatrix4fv(vfPrograms, moveLoc,  1,  false, TRANSFORM_MATRIX, 0);
		multMatrix(TRANSFORM_MATRIX);
		System.out.println("b) translate is called whenever the frame is resized.");
	}

	public void myClearMatrixs()
	{
		//float ROTATE_MATRIX[] = new float[16];
		//float TRANSFORM_MATRIX[] = new float[16];
		//float IDENTITY_MATRIX[] = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
		//float SCALE_MATRIX[] = new float[16];
		//float PROJECTION_Matrix[] = new float [16]; 

		int index = 0;
		while ( index < 16)
		{
			ROTATE_MATRIX[index] = IDENTITY_MATRIX[index];
			TRANSFORM_MATRIX[index] = IDENTITY_MATRIX[index];
			//IDENTITY_MATRIX[index] = IDENTITY_MATRIX[index];
			SCALE_MATRIX[index] = IDENTITY_MATRIX[index];
			PROJECTION_Matrix[index] = IDENTITY_MATRIX[index]; 
			MODELVIEW_Matrix[index] = IDENTITY_MATRIX[index];
			index++;
		}
	}
	
	public void multMatrix(float[] a)
	{
		MODELVIEW_Matrix[0] = ( MODELVIEW_Matrix[0] * a[0]) + (MODELVIEW_Matrix[1] * a[4]) + (MODELVIEW_Matrix[2] * a[8]) + (MODELVIEW_Matrix[3]*a[12]);
		MODELVIEW_Matrix[1] = ( MODELVIEW_Matrix[0] * a[1]) + (MODELVIEW_Matrix[1] * a[5]) + (MODELVIEW_Matrix[2] * a[9]) + (MODELVIEW_Matrix[3]*a[13]);
		MODELVIEW_Matrix[2] = ( MODELVIEW_Matrix[0] * a[2]) + (MODELVIEW_Matrix[1] * a[6]) + (MODELVIEW_Matrix[2] * a[10]) + (MODELVIEW_Matrix[3]*a[14]);
		MODELVIEW_Matrix[3] = ( MODELVIEW_Matrix[0] * a[3]) + (MODELVIEW_Matrix[1] * a[7]) + (MODELVIEW_Matrix[2] * a[11]) + (MODELVIEW_Matrix[3]*a[15]);
		
		MODELVIEW_Matrix[4] = ( MODELVIEW_Matrix[4] * a[0]) + (MODELVIEW_Matrix[5] * a[4]) + (MODELVIEW_Matrix[6] * a[8]) + (MODELVIEW_Matrix[7]*a[12]);
		MODELVIEW_Matrix[5] = ( MODELVIEW_Matrix[4] * a[1]) + (MODELVIEW_Matrix[5] * a[5]) + (MODELVIEW_Matrix[6] * a[9]) + (MODELVIEW_Matrix[7]*a[13]);
		MODELVIEW_Matrix[6] = ( MODELVIEW_Matrix[4] * a[2]) + (MODELVIEW_Matrix[5] * a[6]) + (MODELVIEW_Matrix[6] * a[10]) + (MODELVIEW_Matrix[7]*a[14]);
		MODELVIEW_Matrix[7] = ( MODELVIEW_Matrix[4] * a[3]) + (MODELVIEW_Matrix[5] * a[7]) + (MODELVIEW_Matrix[6] * a[11]) + (MODELVIEW_Matrix[7]*a[15]);
		
		MODELVIEW_Matrix[8] = ( MODELVIEW_Matrix[8] * a[0]) + (MODELVIEW_Matrix[9] * a[4]) + (MODELVIEW_Matrix[10] * a[8]) + (MODELVIEW_Matrix[11]*a[12]);
		MODELVIEW_Matrix[9] = ( MODELVIEW_Matrix[8] * a[1]) + (MODELVIEW_Matrix[9] * a[5]) + (MODELVIEW_Matrix[10] * a[9]) + (MODELVIEW_Matrix[11]*a[13]);
		MODELVIEW_Matrix[10] = ( MODELVIEW_Matrix[8] * a[2]) + (MODELVIEW_Matrix[9] * a[6]) + (MODELVIEW_Matrix[10] * a[10]) + (MODELVIEW_Matrix[11]*a[14]);
		MODELVIEW_Matrix[11] = ( MODELVIEW_Matrix[8] * a[3]) + (MODELVIEW_Matrix[9] * a[7]) + (MODELVIEW_Matrix[10] * a[11]) + (MODELVIEW_Matrix[11]*a[15]);
		
		MODELVIEW_Matrix[12] = ( MODELVIEW_Matrix[12] * a[0]) + (MODELVIEW_Matrix[13] * a[4]) + (MODELVIEW_Matrix[14] * a[8]) + (MODELVIEW_Matrix[15]*a[12]);
		MODELVIEW_Matrix[13] = ( MODELVIEW_Matrix[12] * a[1]) + (MODELVIEW_Matrix[13] * a[5]) + (MODELVIEW_Matrix[14] * a[9]) + (MODELVIEW_Matrix[15]*a[13]);
		MODELVIEW_Matrix[14] = ( MODELVIEW_Matrix[12] * a[2]) + (MODELVIEW_Matrix[13] * a[6]) + (MODELVIEW_Matrix[14] * a[10]) + (MODELVIEW_Matrix[15]*a[14]);
		MODELVIEW_Matrix[15] = ( MODELVIEW_Matrix[12] * a[3]) + (MODELVIEW_Matrix[13] * a[7]) + (MODELVIEW_Matrix[14] * a[11]) + (MODELVIEW_Matrix[15]*a[15]);
	}
}
