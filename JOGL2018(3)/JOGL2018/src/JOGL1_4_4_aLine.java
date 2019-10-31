/*************************************************
 * Created on August 10, 2019, @author: Jim X. Chen
 *
 * Draw randomly generated lines into both buffers with antialiasing
 * 
 * This is to implement the text book's example: J_1_4_Line
 */

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_POINTS;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import static com.jogamp.opengl.GL4.*;

import com.jogamp.opengl.util.awt.TextRenderer; 
import java.awt.Font;


public class JOGL1_4_4_aLine extends JOGL1_4_3_Triangle {
	float vColor[] = {1.0f, 0.0f, 0.0f}; 

	
	public void display(GLAutoDrawable drawable) {		

		int x0 = (int) ((2*Math.random()-1) * WIDTH);
		int y0 = (int) ((2*Math.random()-1) * HEIGHT);
		int xn = (int) ((2*Math.random()-1) * WIDTH);
		int yn = (int) ((2*Math.random()-1) * HEIGHT);

		//2. generate a random color		
		vColor[0] = (float) Math.random();
		vColor[1] = (float) Math.random();
		vColor[2] = (float) Math.random();
		
		// 3. send color data to vertex shader through uniform
 		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(vColor);

		//Connect JOGL variable with shader variable by name
		int colorLoc = gl.glGetUniformLocation(vfPrograms,  "vColor"); 
		gl.glProgramUniform3fv(vfPrograms,  colorLoc, 1, cBuf);

		
		//draw an antialiased line. Intensity is calculated in the vertex shader
		antialiasedLine(x0, y0, xn, yn);

	}

	

	
	// Bresenham's midpoint line algorithm with antialiasing
	public void antialiasedLine(int x0, int y0, int xn, int yn) {
	    int dx, dy, incrE, incrNE, d, x, y, flag = 0;	    
		float D = 0, sin_a, cos_a, sin_cos_a, Denom;
	    
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
	    
		Denom = (float) Math.sqrt((double) (dx * dx + dy * dy));
		sin_a = dy / Denom;
		cos_a = dx / Denom;
		sin_cos_a = sin_a - cos_a;

	    int nPixels = xn - x0 + 1; // number of pixels on the line 	    
	    float[][] vPoints = new float[5][3*nPixels]; // predefined number of pixels on the line
	    float[][] brights = new float[5][nPixels]; // predefined number of pixel intensity 
	    
	    while (x<xn+1) {
	       // save each pixel coordinates and intensity in an array, to be sent to the vertex shader
			IntensifyPixel(vPoints[0], brights[0], x0, x, y, D, flag);
			IntensifyPixel(vPoints[1], brights[1], x0, x, y-1, D-cos_a, flag); // N pixel
			IntensifyPixel(vPoints[2], brights[2], x0, x, y+1, D+cos_a, flag); // S pixel
			IntensifyPixel(vPoints[3], brights[3], x0, x, y-2, D-2*cos_a, flag); // 2N pixel
			IntensifyPixel(vPoints[4], brights[4], x0, x, y+2, D+2*cos_a, flag); // 2S pixel
						  	
	      	x++; /* consider next pixel */
			if (d <= 0) {
				D += sin_a; // distance to the line from E
				d += incrE;
			} else {
				D += sin_cos_a; // distance to the line: NE
				y++;
				d += incrNE;
			}
	    }
	    
	    // draw a line at a time. 5 lines with their own positions and intensities per pixel
	    IntensifyLine(vPoints[4], brights[4]); 		
	    IntensifyLine(vPoints[3], brights[3]); 		
	    IntensifyLine(vPoints[2], brights[2]); 		
	    IntensifyLine(vPoints[1], brights[1]); 		
	    IntensifyLine(vPoints[0], brights[0]); 

				
		try {
			Thread.sleep(500);
		} catch (Exception ignore) {}

	}

	void IntensifyLine(float vPoints[], float brights[]) {
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vPoints);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					vBuf, // the vertex array
					GL_STATIC_DRAW); 
 		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active VAO buffer
						
	    // load vbo[1] with distance data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]); // use handle 0 		
		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(brights);
		gl.glBufferData(GL_ARRAY_BUFFER, cBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
					cBuf, // the vertex array
					GL_STATIC_DRAW); 
 		gl.glVertexAttribPointer(1, 1, GL_FLOAT, false, 0, 0); // associate vbo[1] with active VAO buffer
 		
		//  draw points: VAO has is an array of corresponding vertices and colors
		gl.glDrawArrays(GL_POINTS, 0, (vBuf.limit()/3)); 	   
	}
	
	
	
	void IntensifyPixel(float vPoints[], float brights[], int x0, int x, int y, float D, int flag) {
		
	   float xf = x, yf = y;

	    if (flag==1) {
	      xf = y;
	      yf = x;
	    } else if (flag==10) {
	      xf = x;
	      yf = -y;
	    } else if (flag==11) {
	      xf = y;
	      yf = -x;
	    }
	 
		// write a pixel into the framebuffer, here we write into an array
	    vPoints[(x-x0)*3] = xf / (float) WIDTH; // normalize -1 to 1
	    vPoints[(x-x0)*3 + 1] = yf / (float) HEIGHT; // normalize -1 to 1
		vPoints[(x-x0)*3 + 2] = 0.0f;
	
	 
		if (D < 0) 
				D = -D; // negative if the pixel is above the line
		
	  // store corresponding distance factor in an array, sent to the vertex shader to modify intensity
	  brights[(x-x0)] =  1.0f - D/2.5f; 

  }


	
	public void init(GLAutoDrawable drawable) {
				
			gl = (GL4) drawable.getGL();
			String vShaderSource[], fShaderSource[] ;
						
			vShaderSource = readShaderSource("src/JOGL1_4_4_V.shader"); // read vertex shader
			fShaderSource = readShaderSource("src/JOGL1_4_3_F.shader"); // read fragment shader
			vfPrograms = initShaders(vShaderSource, fShaderSource);		
			
			// 1. generate vertex arrays indexed by vao
			gl.glGenVertexArrays(vao.length, vao, 0); // vao stores the handles, starting position 0
			gl.glBindVertexArray(vao[0]); // use handle 0
			
			// 2. generate vertex buffers indexed by vbo: here vertices and colors
			gl.glGenBuffers(vbo.length, vbo, 0);
			
			// 3. enable VAO with loaded VBO data
			gl.glEnableVertexAttribArray(0); // enable the 0th vertex attribute: position
			gl.glEnableVertexAttribArray(1); // enable the 1th vertex attribute: color
			
			
			// 5. send display size as uniform

			//Connect JOGL variable with shader variable by name
			int widthLoc = gl.glGetUniformLocation(vfPrograms,  "WIDTH"); 
			gl.glProgramUniform1f(vfPrograms,  widthLoc, WIDTH);
			int heightLoc = gl.glGetUniformLocation(vfPrograms,  "HEIGHT"); 
			gl.glProgramUniform1f(vfPrograms,  heightLoc, HEIGHT);

			// draw into both buffers
		    gl.glDrawBuffer(GL.GL_FRONT_AND_BACK);
 		}
		
	
	public static void main(String[] args) {
		 new JOGL1_4_4_aLine();

	}
}



