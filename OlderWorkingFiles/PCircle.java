import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.util.Vector;
import java.util.Scanner;
import javax.swing.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
//import com.sun.java_cup.internal.runtime.Scanner;
import com.jogamp.common.nio.Buffers;



public class PCircle extends JFrame implements GLEventListener
{
	private GLCanvas myCanvas;
	private int vao[] = new int[1];
	private int vbo[] = new int [1];
	private int rendering_program;
	private float VertArray[][];
	private float ColorArray[][];
	private float vPoints[];
	private float cPoints[];
	int index = 1;
	float radius = 0.5f;
	
	public PCircle()
	{
		setTitle("Basic Circle Drawn in Parallel");
		setSize(1200, 800);
		setLocation(200, 200);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		setVisible(true);
	}
	
	
	public void display(GLAutoDrawable drawable) 
	{		
		System.out.println("Display");
		GL4 gl = (GL4) GLContext.getCurrentGL();
		float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f};
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		//if ( index < 10)
		//{
		//	index++;
		//}
		//else
		//{
		///	index = 1;
		//}
		generateColors();
		generateVertexes();
		
		int vertexTotal = 3 * (int)(Math.pow(4, index));
		gl.glUseProgram(rendering_program);
		
		gl.glPointSize(10.0f);
		
		//DrawPoints(vertexTotal);
		//gl.glDrawArrays(GL_TRIANGLES, 0, vertexTotal);
		//gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);
	}
		
	public static void main(String[] args) 
	{
		new PCircle();
		
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL4 gl = (GL4) GLContext.getCurrentGL();
		rendering_program = createShaderProgram();
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
	}


	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	private int createShaderProgram()
	{ 
		GL4 gl = (GL4) GLContext.getCurrentGL();
		String vshaderSource[ ] =
			{ "#version 430 \n",
					"void main(void) \n",
					"{ gl_Position = vec4(0.0, 0.0, 0.0, 1.0); } \n",
			};
		String fshaderSource[ ] =
			{ "#version 430 \n",
					"out vec4 color; \n",
					"void main(void) \n",
					"{ color = vec4(0.0, 0.0, 1.0, 1.0); } \n",
			};
		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, 3, vshaderSource, null, 0); // note: 3 lines of code
		gl.glCompileShader(vShader);
		int fShader=gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, 4, fshaderSource, null, 0); // note: 4 lines of code
		gl.glCompileShader(fShader);
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		return vfprogram;
	} */
	
	private void printShaderLog(int shader)
	{ 
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[ ] len = new int[1];
		int[ ] chWrittn = new int[1];
		byte[ ] log = null;
		// determine the length of the shader compilation log
		gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0)
		{ 
			log = new byte[len[0]];
			gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
			System.out.println("Shader Info Log: ");
			for (int i = 0; i < log.length; i++)
			{ 
				System.out.print((char) log[i]);
			} 
		} 
	}
	
	void printProgramLog(int prog)
	{ 
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[ ] len = new int[1];
		int[ ] chWrittn = new int[1];
		byte[ ] log = null;
		// determine the length of the program linking log
		gl.glGetProgramiv(prog,GL_INFO_LOG_LENGTH,len, 0);
		if (len[0] > 0)
		{ 
			log = new byte[len[0]];
			gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0,log, 0);
			System.out.println("Program Info Log: ");
			for (int i = 0; i < log.length; i++)
			{ 
				System.out.print((char) log[i]);
			} 
		} 
	}
		
	boolean checkOpenGLError()
	{ 
		GL4 gl = (GL4) GLContext.getCurrentGL();
		boolean foundError = false;
		GLU glu = new GLU();
		int glErr = gl.glGetError();
		while (glErr != GL_NO_ERROR)
		{ 
			System.err.println("glError: " + glu.gluErrorString(glErr));
			foundError = true;
			glErr = gl.glGetError();
		}
		return foundError;
	}
	/*
	private int createShaderProgramVerbose()
	{ // arrays to collect GLSL compilation status values.
	// note: one-element arrays are used because the associated JOGL calls require arrays.
		int[ ] vertCompiled = new int[1];
		int[ ] fragCompiled = new int[1];
		int[ ] linked = new int[1];
		// catch errors while compiling shaders
		gl.glCompileShader(vShader);
		checkOpenGLError(); // can use returned boolean
		gl.glGetShaderiv(vShader, GL_COMPILE_STATUS, vertCompiled, 0);
		if (vertCompiled[0] == 1)
		{ 
			System.out.println(". . . vertex compilation success.");
		} 
		else
		{ 
			System.out.println(". . . vertex compilation failed.");
			printShaderLog(vShader);
		}
		gl.glCompileShader();
		checkOpenGLError(); // can use returned boolean
		gl.glGetShaderiv(fShader, GL_COMPILE_STATUS, fragCompiled, 0);
		if (fragCompiled[0] == 1)
		{ 
			System.out.println(". . . fragment compilation success.");
		} 
		else
		{ 
			System.out.println(". . . fragment compilation failed.");
			printShaderLog(fShader);
		}
		if ((vertCompiled[0] != 1) || (fragCompiled[0] != 1))
		{ 
			System.out.println("\nCompilation error; return-flags:");
			System.out.println(" vertCompiled = " + vertCompiled[0] + " ; fragCompiled = " + fragCompiled[0]);
		} else
		{ 
			System.out.println("Successful compilation");
		}
		// catch errors while linking shaders
		gl.glLinkProgram(vfprogram);
		checkOpenGLError();
		gl.glGetProgramiv(vfprogram, GL_LINK_STATUS, linked,0);
		if (linked[0] == 1)
		{ 
			System.out.println(". . . linking succeeded.");
		} 
		else
		{ 
			System.out.println(". . . linking failed.");
			printProgramLog(vfprogram);
		}
	} */
	
	private int createShaderProgram()
	{ 
		System.out.println("createShaderProgram");
		GL4 gl = (GL4) GLContext.getCurrentGL();
		//String vshaderSource[ ] =
		//{ "#version 430 \n",
		//		"void main(void) \n",
		//		"{ gl_Position = vec4(0.0, 0.0, 0.0, 1.0); } \n",
		//};
		//String fshaderSource[ ] =
		//{ "#version 430 \n",
		//		"out vec4 color; \n",
		//		"void main(void) \n",
		//		"{ color = vec4(0.0, 0.0, 1.0, 1.0); } \n",
		//};
		String vshaderSource[] = readShaderSource("src/cwells21_circle_vert.shader");
		String fshaderSource[] = readShaderSource("src/cwells21_circle_frag.shader");
		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0); // note: 3 lines of code
		gl.glCompileShader(vShader);
		int fShader=gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0); // note: 4 lines of code
		gl.glCompileShader(fShader);
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource,	null, 0);
		return vfprogram;
		//vshaderSource = readShaderSource("cwells21_circle_vert.shader");
		//fshaderSource = readShaderSource("cwells21_circle_frag.shader");
		
	}
	
	private String[ ] readShaderSource(String filename)
	{ 
		System.out.println("readShaderSource");
		Vector<String> lines = new Vector<String>();
		Scanner sc;
		try { sc = new Scanner(new File(filename)); }
		catch (IOException e)
		{ 
			System.err.println("IOException reading file: " + e);
			return null;
		}
		while (sc.hasNext())
		{ 
			lines.addElement(sc.nextLine());
		}
		String[ ] program = new String[lines.size()];
		for (int i = 0; i < lines.size(); i++)
		{ 
			program[i] = (String) lines.elementAt(i) + "\n";
		}
		return program;
	}
	
	// draw a circle with center at the origin in xy plane
	public void generateVertexes() 
	{
		System.out.println("generateVertexes");
		int depth = index;
		GL4 gl = (GL4) GLContext.getCurrentGL();
		VertArray = new float[(int)Math.pow(4, index)*3][3];
		float tempx, tempy, tempz;
		tempx = 0.0f;
		tempy = 0.0f;
		tempz = 0.0f;
		int steps = (int)Math.pow(4, index);
		double delta = (double)((2*Math.PI)/steps);
		int currentPos = 0;	//position on the outside of the circle
		int arrayIndex = 0;
		while ( currentPos < steps)
		{
			VertArray[arrayIndex][0] = 0.0f;  //origin vertex
			VertArray[arrayIndex][1] = 0.0f;
			VertArray[arrayIndex][2] = 0.0f;
			//currentPos++;
			arrayIndex++;
			if ( tempx == 0 && tempy == 0)
			{
				VertArray[arrayIndex][0] = (this.radius * (float)Math.cos(currentPos * delta)); //if this is the first triange then walk to the next point
				VertArray[arrayIndex][1] = (this.radius * (float)Math.sin(currentPos * delta));
				VertArray[arrayIndex][2] = 0.0f;
				tempx = VertArray[arrayIndex][0];
				tempy = VertArray[arrayIndex][1];
				tempz = 0.0f;
				arrayIndex++;
				currentPos++;
			}
			else
			{
				//This is not the first triangel, so we have to connect to the provious vertex.
				VertArray[arrayIndex][0] = tempx;
				VertArray[arrayIndex][1] = tempy;
				VertArray[arrayIndex][2] = tempz;
				arrayIndex++;
				//Using the previous vertex, so do not advance on circle
			}
			
			VertArray[arrayIndex][0] = (this.radius * (float)Math.cos(currentPos * delta));
			VertArray[arrayIndex][1] = (this.radius * (float)Math.sin(currentPos * delta));
			VertArray[arrayIndex][2] = 0.0f;
			
			arrayIndex++;
			//System.out.println(currentPos + " " + steps);
			currentPos++;
		}
		
		//System.out.println(VertArray.length);
		int vertexTotal = 3 * (int)(Math.pow(4, index));
		//System.out.println(VertArray.length);
		FlattentMatricies(vertexTotal);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoints);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
				vBuf, // the vertex array
				GL_STATIC_DRAW); 
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer
				
		gl.glDrawArrays(GL_TRIANGLES, 0, vBuf.limit()/3); 
				
	}

	

	
	
	private void generateColors()
	{
		System.out.println("generateColors");
		int length = 3 * (int)(Math.pow(4, index));
		ColorArray = new float[3 * (int)(Math.pow(4, index))][3];
		
		int colorIndex = 0;
		while ( colorIndex < length)
		{
			if (colorIndex % 2 == 0)
			{
				this.ColorArray[colorIndex][0] = 1.0f;
				this.ColorArray[colorIndex][1] = 0.0f;
				this.ColorArray[colorIndex][2] = 1.0f;
			}
			else
			{
				this.ColorArray[colorIndex][0] = 0.0f;
				this.ColorArray[colorIndex][1] = 1.0f;
				this.ColorArray[colorIndex][2] = 1.0f;
			}
			colorIndex++;
		}
		
	}
	
	private void DrawPoints( int vertexTotal)
	{
		System.out.println("DrawPoints");
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		// adjust OpenGL settings and draw model
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
	}
	
	private void FlattentMatricies( int vertexTotal)
	{
		System.out.println("FlattentMatricies");
		//System.out.println(VertArray.length);
		//System.out.println(ColorArray.length);
		vPoints = new float[(int)Math.pow(4, index)*9];
		cPoints = new float[(int)Math.pow(4, index)*9];
		//System.out.println(VertArray.length + " " + ColorArray.length);
		//System.out.println( vPoints.length + " " + cPoints.length);
		int colorCounter = 0;
		int ColorArrayCounter = 0;
		int VertArrayCounter = 0;
		int vertexCounter = 0;
		while ( (vertexCounter*3) < VertArray.length)
		{
			vPoints[vertexCounter] = VertArray[VertArrayCounter][0];
			System.out.println( vPoints[vertexCounter]);
			vertexCounter++;
			vPoints[vertexCounter] = VertArray[VertArrayCounter][1];
			System.out.println( vPoints[vertexCounter]);
			vertexCounter++;
			vPoints[vertexCounter] = VertArray[VertArrayCounter][2];
			System.out.println( vPoints[vertexCounter]);
			vertexCounter++;
			VertArrayCounter++;
			cPoints[colorCounter] = ColorArray[ColorArrayCounter][0];
			System.out.println( cPoints[colorCounter]);
			colorCounter++;
			cPoints[colorCounter] = ColorArray[ColorArrayCounter][1];
			System.out.println( cPoints[colorCounter]);
			colorCounter++;
			cPoints[colorCounter] = ColorArray[ColorArrayCounter][2];
			System.out.println( cPoints[colorCounter]);
			colorCounter++;
			ColorArrayCounter++;
		}
		//gl.glDrawArrays(GL_TRIANGLES, 0, vertexTotal);
	}
			
}