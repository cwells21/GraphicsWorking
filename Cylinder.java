import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

public class Cylinder extends Cone{

	static int depth = 8; // number of subdivisions
	static int cRadius = 400, flip = 2;
	static float length  = 1.0f;
	int count=0; // used to generate triangle vertex indices
	static int numofTriangleG = (2*2)*(4*(int)Math.pow(2,depth));
	float vPointsG[] = new float[3*3*numofTriangleG];
	// vertex data for the 4 triangles
	static float cVdata[][] = { { 1.0f, 0.0f, 0.0f }, { 0.0f, 1.0f, 0.0f },
			{ -1.0f, 0.0f, 0.0f }, { 0.0f, -1.0f, 0.0f } };

	
	public void display(GLAutoDrawable drawable) {		

		// when the circle is too big or small, change
		// the direction (growing or shrinking)
		//if (cRadius >= (HEIGHT / 2) || cRadius <= 1) {
		//	flip = -flip;
		//	depth++; // number of subdivisions
		//	depth = depth % 7;
		//}
		//cRadius += flip; // circle's radius change
		
		
		// use cRadius as an angle to show 3D model through rotations
		//Connect JOGL variable with shader variable by name
		//int thetaLoc = gl.glGetUniformLocation(vfPrograms,  "theta"); 
		//gl.glProgramUniform1f(vfPrograms,  thetaLoc, cRadius);
		
		// clear the framebuffer and depthbuffer 
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
				
		drawCircle(cRadius, depth);
	}

	// draw a circle with center at the origin in xy plane
	public void drawCircle(int cRadius, int depth) {
		
	    int numofTriangle=(2*2)*(4*(int)Math.pow(2,depth)); // number of triangles after subdivision
	    float vPoints[] = new float[3*3*numofTriangle]; // 3 vertices each triangle, and 3 values each vertex
	    

	    //System.out.println("vPoints[] is used to save all triangle vertex values, pervertex values to be sent to the vertex shader");	

	    count = 0; // start filling triangle array to be sent to vertex shader
	    
		subdivideCircle(vPoints, cRadius, cVdata[0], cVdata[1], depth);
		subdivideCircle(vPoints, cRadius, cVdata[1], cVdata[2], depth);
		subdivideCircle(vPoints, cRadius, cVdata[2], cVdata[3], depth);
		subdivideCircle(vPoints, cRadius, cVdata[3], cVdata[0], depth);

		vPointsG = vPoints;
		// load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoints);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
				vBuf, // the vertex array
				GL_STATIC_DRAW); 
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer
				
		gl.glDrawArrays(GL_TRIANGLES, 0, vBuf.limit()/3); 
		
	}

	// subdivide a triangle recursively, and draw them
	private void subdivideCircle(float[] vPoints, int radius, float[] v1, float[] v2, int depth) {
		float v11a[] = new float[3];
		float v11b[] = new float[3];
		float v22a[] = new float[3];
		float v22b[] = new float[3];
		float v00a[] = { 0, 0, (length/2) };
		float v00b[] = { 0, 0, ((-1 * length)/2)};
		float v12[] = new float[3];
		//float v12[] = new float[3];

	    if (depth == 0) { // draw triangle
  	
			for (int i = 0; i < 3; i++) {
				v11a[i] = v1[i] * radius/WIDTH;
				v22a[i] = v2[i] * radius/WIDTH;
				v11b[i] = v1[i] * radius/WIDTH;
				v22b[i] = v2[i] * radius/WIDTH;
			}
			//drawtriangle(v11, v22, v00);          
			// load vPoints with the triangle vertex values
			for (int i = 0; i < 3; i++)  vPoints[count++] = v11a[i] ; //For the top
			for (int i = 0; i < 3; i++)  vPoints[count++] = v22a[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v00a[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v11b[i] ;	//Bottom
			for (int i = 0; i < 3; i++)  vPoints[count++] = v22b[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v00b[i] ;
			
			//for (int i = 0; i < 3; i++)  vPoints[count++] = v22a[i] ;	//LeftSide
			//for (int i = 0; i < 3; i++)  vPoints[count++] = v11a[i] ;
			//for (int i = 0; i < 3; i++)  vPoints[count++] = v11b[i] ;
			
			//for (int i = 0; i < 3; i++)  vPoints[count++] = v11b[i] ; //Right side
			//for (int i = 0; i < 3; i++)  vPoints[count++] = v22b[i] ;
			//for (int i = 0; i < 3; i++)  vPoints[count++] = v22a[i] ;
			v11a[2] = (length/2); //Make sure outside edge has height
			v22a[2] = (length/2);
			v00a[2] = (length/2);
			v11b[2] = ((-1 * length)/2);	//Bottom
			v22b[2] = ((-1 * length)/2);
			v00b[2] = ((-1 * length)/2);
				
			for (int i = 0; i < 3; i++)  vPoints[count++] = v22a[i] ;	//LeftSide
			for (int i = 0; i < 3; i++)  vPoints[count++] = v11a[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v11b[i] ;
				
			for (int i = 0; i < 3; i++)  vPoints[count++] = v11b[i] ; //Right side
			for (int i = 0; i < 3; i++)  vPoints[count++] = v22b[i] ;
			for (int i = 0; i < 3; i++)  vPoints[count++] = v22a[i] ;
             return;

        }


		v12[0] = v1[0] + v2[0];
		v12[1] = v1[1] + v2[1];
		v12[2] = v1[2] + v2[2];

		normalize(v12);

		// subdivide a triangle recursively, and draw them
		subdivideCircle(vPoints, radius, v1, v12, depth - 1);
		subdivideCircle(vPoints, radius, v12, v2, depth - 1);
	}
	
	public void normalize(float[] vector) {
		// TODO Auto-generated method stub
		float d = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]
				+ vector[2] * vector[2]);

		if (d == 0) {
			System.err.println("0 length vector: normalize().");
			return;
		}
		vector[0] /= d;
		vector[1] /= d;
		vector[2] /= d;

	}


	
	public void init(GLAutoDrawable drawable) {
				
			gl = (GL4) drawable.getGL();
			String vShaderSource[], fShaderSource[] ;
						
			vShaderSource = readShaderSource("src/JOGL1_4_5_V.shader"); // read vertex shader
			fShaderSource = readShaderSource("src/JOGL1_4_3_F.shader"); // read fragment shader
			vfPrograms = initShaders(vShaderSource, fShaderSource);		
			
			// 1. generate vertex arrays indexed by vao
			gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
			gl.glBindVertexArray(vao[0]); // use handle 0
			
			// 2. generate vertex buffers indexed by vbo: here vertices and colors
			gl.glGenBuffers(vbo.length, vbo, 0);
			
			// 3. enable VAO with loaded VBO data
			gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position
			
			// if you don't use it, you should not enable it
			//gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color
						
			//4. specify drawing into only the back_buffer
			gl.glDrawBuffer(GL.GL_BACK); 
			
			// 5. Enable zbuffer and clear framebuffer and zbuffer
			gl.glEnable(GL.GL_DEPTH_TEST); 

			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
 		}
		
	
	public static void main(String[] args) {
		 new Cylinder();

	}
	
	static public void drawCylinder( float radius, float givenLength)
	{
		length = givenLength;
		cRadius = (int)radius;
		new Cylinder();
	}
	
	static public void drawCylinder()
	{
		new Cylinder();
	}
	
}
