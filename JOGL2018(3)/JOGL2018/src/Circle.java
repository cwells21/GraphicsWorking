import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_POINTS;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

public class Circle {
	private static int vao[ ] = new int[1]; // vertex array object (handle), for sending to the vertex shader
	private static int vbo[ ] = new int[2]; // vertex buffers objects (handles) to stores position, color, normal, etc
	float vPoint[] = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}; 
	float vColor[] = {1.0f, 0.0f, 1.0f};
	static GL4 gl;
	private final double DegtoRad = (double)(Math.PI/180);	//since the math trig functions work in radians, and i want my steps in degrees this was the logical constant
	double delta = 0.005;
	private final double r = 1;	//Fill 90% of the viewable area
	private static double PointIndex = 0.0;
	static int vfPrograms;
	int index = 1;
	
	
	public void display(GLAutoDrawable drawable) {		
		// 1. draw into both buffers
		System.out.println("In Display");
		gl = drawable.getGL().getGL4();
		float bgColor[] = { 0.0f, 0.0f, 0.0f, 1.0f };
   		FloatBuffer bgColorBuffer = Buffers.newDirectFloatBuffer(bgColor);
   		gl.glClearBufferfv(GL_COLOR, 0, bgColorBuffer); // clear every frame
    	PointIndex = PointIndex + delta;
	    gl.glDrawBuffer(GL.GL_FRONT_AND_BACK);
	    
		// 2. generate 2 random end points		
	    LoadPoints();
	    
	    DrawCircle( vPoint, vColor);
	    index++;
	    if (index >= 10)
	    {
	    	index = 1;
	    }
	    
	    // load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoint);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES, vBuf, GL_STATIC_DRAW); 
 		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer
						
		// 5. send color data to vertex shader through uniform
 		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(vColor);

		//Connect JOGL variable with shader variable by name
		int colorLoc = gl.glGetUniformLocation(vfPrograms,  "vColor"); 
		gl.glProgramUniform3fv(vfPrograms,  colorLoc, 1, cBuf);
			
		// 6. draw points: VAO has 1 array of corresponding vertices 
		gl.glDrawArrays(GL_POINTS, 0, (vBuf.limit()/3)); 	    
	  }
	
	public static void init() 
	{
		System.out.println("In init");
		String vShaderSource[], fShaderSource[] ;
		
		vShaderSource = readShaderSource("src/cwells21_CircleVShader.txt"); // read vertex shader
		fShaderSource = readShaderSource("src/cwells21_CircleFShader.txt"); // read fragment shader
		//System.out.println(vShaderSource.toString());
		//System.out.println(fShaderSource.toString());
		vfPrograms = initShaders(vShaderSource, fShaderSource);		
			
		// 1. generate vertex arrays indexed by vao
		gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
		gl.glBindVertexArray(vao[0]); // use handle 0
			
		// 2. generate vertex buffers indexed by vbo: here vertices and colors
		gl.glGenBuffers(vbo.length, vbo, 0);
			
		// 3. enable VAO with loaded VBO data
		gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position
		gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color
 	}
		
	public static String[] readShaderSource(String filename) 
	{ // read a shader file into an array
		System.out.println("In Reading Shaders");
		Vector<String> lines = new Vector<String>(); // Vector object for storing shader program
		Scanner sc;
			
		try {
			sc = new Scanner(new File(filename)); //Scanner object for reading a shader program
		} catch (IOException e) {
			System.err.println("IOException reading file: " + e);
			return null;
		}
		while (sc.hasNext()) {
			lines.addElement(sc.nextLine());
		}
		String[] shaderProgram = new String[lines.size()];
		for (int i = 0; i < lines.size(); i++) {
			shaderProgram[i] = (String) lines.elementAt(i) + "\n";
		}
		sc.close(); 
		return shaderProgram; //  a string of shader programs
	}
		
	public static int initShaders(String vShaderSource[], String fShaderSource[]) 
	{

		// 1. create, load, and compile vertex shader
		//System.out.println("In initShaders");
		
		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vShaderSource.length, vShaderSource, null, 0);
		gl.glCompileShader(vShader);

		// 2. create, load, and compile fragment shader
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fShaderSource.length, fShaderSource, null, 0);
		gl.glCompileShader(fShader);

		// 3. attach the shader programs
		int vfProgram = gl.glCreateProgram(); // for attaching v & f shaders
		gl.glAttachShader(vfProgram, vShader);
		gl.glAttachShader(vfProgram, fShader);

		// 4. link the program
		gl.glLinkProgram(vfProgram); // successful linking --ready for using
		gl.glDeleteShader(vShader); // attached shader object will be flagged for deletion until 
										// it is no longer attached
		gl.glDeleteShader(fShader);

		// 5. Use the program
		gl.glUseProgram(vfProgram);
		gl.glDeleteProgram(vfProgram); // in-use program object will be flagged for deletion until 
											// it is no longer in-use

		return vfProgram;
	}
		
	public static void main(String[] args) {
		
		Circle test = new Circle();
		
		init();
		//new Circle();
		 
	}
	

    public void LoadPoints()
    {
    	System.out.println("In Load Points");
	    vPoint[0] = (float)(r * ( Math.cos(index)));	//Starting point
	    vPoint[1] = (float)(r* (Math.sin(index)));
	    vPoint[2] = 0;
	    vColor[0] = 1.0f;
		vColor[1] = 0.0f;
		vColor[2] = 0.5f;
	    delta = Math.pow(4, index);
	    delta = ((2*Math.PI)/delta);
	    int pointIndex = 3;
	    int colorIndex = 3;
	    float angleValue = 0;
	    
	    while (angleValue<(2*Math.PI)) 
	    {
	    	angleValue = (float) (angleValue + delta);
	    	vPoint[pointIndex] = (float)(r * ( Math.cos(angleValue)));
	    	pointIndex++;
	    	vPoint[pointIndex] = (float)(r* (Math.sin(angleValue)));
	    	pointIndex++;
	    	vPoint[pointIndex] = 0;
	    	if ( ( angleValue >= 0) && (angleValue < (Math.PI/2)))
	    	{
	    		vColor[colorIndex] = 1.0f;
	    		colorIndex++;
	    		vColor[colorIndex] = 0.0f;
	    		colorIndex++;
	    		vColor[colorIndex] = 0.5f;
	    		colorIndex++;
	    	}
	    	if ( ( angleValue > (Math.PI/2)) && (angleValue < (Math.PI)))
	    	{
	    		vColor[colorIndex] = 0.0f;
	    		colorIndex++;
	    		vColor[colorIndex] = 1.0f;
	    		colorIndex++;
	    		vColor[colorIndex] = 0.5f;
	    		colorIndex++;
	    	}
	    	if ( ( angleValue >= (Math.PI)) && (angleValue < ((3*Math.PI)/2)))
	    	{
	    		vColor[colorIndex] = 1.0f;
	    		colorIndex++;
	    		vColor[colorIndex] = 0.0f;
	    		colorIndex++;
	    		vColor[colorIndex] = 0.5f;
	    		colorIndex++;
	    	}
	    	if ( ( angleValue > ((3*Math.PI)/2)) && (angleValue < 2*Math.PI))
	    	{
	    		vColor[colorIndex] = 0.0f;
	    		colorIndex++;
	    		vColor[colorIndex] = 1.0f;
	    		colorIndex++;
	    		vColor[colorIndex] = 0.5f;
	    		colorIndex++;
	    	}
	    	
	    	
		    
	    }
	    
    }

    
  
   
    public void DrawCircle(float[] vPoint, float[] vColor) {

		// 1. load vbo[0] with vertex data
    	// 3. generate a random color		
    	System.out.println("In Draw Circle");
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoint);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					vBuf, // the vertex array
					GL_STATIC_DRAW); 
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer
						
		// 2. load vbo[1] with color data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 1 		
		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(vColor);
		gl.glBufferData(GL_ARRAY_BUFFER, cBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					cBuf, //the color array
					GL_STATIC_DRAW); 		
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[1] with active vao buffer
			
		// 3. draw 3 points: VAO has two arrays of corresponding vertices and colors
		gl.glDrawArrays(GL_POINTS, 0, 1); 
	}
    
}
