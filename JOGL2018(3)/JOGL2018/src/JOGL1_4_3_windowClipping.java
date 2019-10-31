/*************************************************
 * Created on August 10, 2019, @author: Jim X. Chen
 *
 * Achieving clipping in the fragment shader
 * 
 * This is to implement the text book's example: J_1_3_windowClipping
 */

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
//import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_POINTS;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
//import static com.jogamp.opengl.GL4.*;


public class JOGL1_4_3_windowClipping extends JOGL1_4_3_Triangle {
	
	public void display(GLAutoDrawable drawable) {		

		// This is the viewport physical coordinates
		gl.glViewport(0, 0, WIDTH, HEIGHT);

		
		// 1. draw into the back buffers
	    gl.glDrawBuffer(GL.GL_BACK);

		// clear the display every frame
		float bgColor[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bgColorBuffer = Buffers.newDirectFloatBuffer(bgColor);
		gl.glClearBufferfv(GL_COLOR, 0, bgColorBuffer); // clear every frame

		
		// generate a random triangle and display
		int v[][] = new int[3][3]; // each vertex is v[i]

		// generate three random vertices
		for (int i = 0; i < 3; i++) { 
			v[i][0] = (int) (WIDTH * (2*Math.random() - 1));
			v[i][1] = (int) (HEIGHT * (2*Math.random() - 1));
			v[i][2] = 0;
		}

		// scan-convert the triangle
		drawtriangle(v);
		
		// Drwaw triangle edges: using Bresenham's algorithm
	    bresenhamLine(v[0][0], v[0][1], v[1][0], v[1][1]); 
	    bresenhamLine(v[1][0], v[1][1], v[2][0], v[2][1]); 
	    bresenhamLine(v[2][0], v[2][1], v[0][0], v[0][1]); 
	    
	    // Draw clipping boundaries
		v[0][0] = (int) -WIDTH/2;
		v[0][1] = (int) -HEIGHT/2;
		v[1][0] = (int) WIDTH/2;
		v[1][1] = (int) -HEIGHT/2;
		bresenhamLine(v[0][0], v[0][1], v[1][0], v[1][1]); 
	    // Draw clipping boundaries
		v[0][0] = (int) WIDTH/2;
		v[0][1] = (int) -HEIGHT/2;
		v[1][0] = (int) WIDTH/2;
		v[1][1] = (int) HEIGHT/2;
		bresenhamLine(v[0][0], v[0][1], v[1][0], v[1][1]); 
	    // Draw clipping boundaries
		v[0][0] = (int) WIDTH/2;
		v[0][1] = (int) HEIGHT/2;
		v[1][0] = (int) -WIDTH/2;
		v[1][1] = (int) HEIGHT/2;
		bresenhamLine(v[0][0], v[0][1], v[1][0], v[1][1]); 
	    // Draw clipping boundaries
		v[0][0] = (int) -WIDTH/2;
		v[0][1] = (int) HEIGHT/2;
		v[1][0] = (int) -WIDTH/2;
		v[1][1] = (int) -HEIGHT/2;
		bresenhamLine(v[0][0], v[0][1], v[1][0], v[1][1]); 
		
		// slow-down for visual effect
		try {
			Thread.sleep(400);
		} catch (Exception ignore) {}

	}


	
	// Bresenham's midpoint line algorithm: here we rewrite for random pixel colors
	public void bresenhamLine(int x0, int y0, int xn, int yn) {
	    int dx, dy, incrE, incrNE, d, x, y, flag = 0;	    
	    
	    if (xn<x0) {
	      //swapd(&x0,&xn);
	      int temp = x0;
	      x0 = xn;
	      xn = temp;

	      //swapd(&y0,&yn);
	      temp = y0;
	      y0 = yn;
	      yn = temp;
	    }
	    if (yn<y0) {
	      y0 = -y0;
	      yn = -yn;
	      flag = 10;
	    }

	    dy = yn-y0;
	    dx = xn-x0;

	    if (dx<dy) {
	      //swapd(&x0,&y0);
	      int temp = x0;
	      x0 = y0;
	      y0 = temp;

	      //swapd(&xn,&yn);
	      temp = xn;
	      xn = yn;
	      yn = temp;

	      //swapd(&dy,&dx);
	      temp = dy;
	      dy = dx;
	      dx = temp;

	      flag++;
	    }

	    x = x0;
	    y = y0;
	    d = 2*dy-dx;
	    incrE = 2*dy;
	    incrNE = 2*(dy-dx);

	    int nPixels = xn - x0 + 1; // number of pixels on the line 	    
	    float[] vPoints = new float[3*nPixels]; // predefined number of pixels on the line
	    float[] vColors = new float[3*nPixels]; // predefined number of pixel colors on the line
	    float xf = x, yf = y; // taking care of different slopes
	    
	    while (x<xn+1) {
	       // taking care of different slopes (mirror vertical, horizontal, and diagonal lines) 
	    	xf = x; yf = y; 
		   if (flag==1) {
			      xf = y; yf = x;
			    } else if (flag==10) {
			      xf = x; yf = -y;
			    } else if (flag==11) {
			      xf = y;  yf = -x;
		  }
		   
	      // write a pixel into the framebuffer, here we write into an array
		  vPoints[(x-x0)*3] = xf / (float) WIDTH; // normalize -1 to 1
		  vPoints[(x-x0)*3 + 1] = yf / (float) HEIGHT; // normalize -1 to 1
		  vPoints[(x-x0)*3 + 2] = 0.0f;
		  
		  // generate corresponding pixel colors
		  vColors[(x-x0)*3] = (float) Math.random(); 
		  vColors[(x-x0)*3 + 1] = (float) Math.random(); 
		  vColors[(x-x0)*3 + 2] = (float) Math.random(); 
		  

	      x++; /* consider next pixel */
	      if (d<=0) {
	        d += incrE;
	      } else {
	        y++;
	        d += incrNE;
	      }
	    }
	       
	    
	    // load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoints);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					vBuf, // the vertex array
					GL_STATIC_DRAW); 
 		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer
						
	    // load vbo[1] with color data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 0 		
		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(vColors);
		gl.glBufferData(GL_ARRAY_BUFFER, cBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					cBuf, // the vertex array
					GL_STATIC_DRAW); 
 		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer

 		
		//  draw points: VAO has is an array of corresponding vertices and colors
		gl.glDrawArrays(GL_POINTS, 0, (vBuf.limit()/3)); 	    
	  }



	
	public void init(GLAutoDrawable drawable) {
			gl = (GL4) drawable.getGL();
			String vShaderSource[], fShaderSource[] ;
						
			vShaderSource = readShaderSource("src/JOGL1_4_3_V.shader"); // read vertex shader
			fShaderSource = readShaderSource("src/JOGL1_4_3_windowClipping_F.shader"); // read fragment shader
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
		
	
	public static void main(String[] args) {
		 new JOGL1_4_3_windowClipping();

	}
}


