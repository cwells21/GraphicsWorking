#version	450	

in  vec3 color; // (interpolated) value from vertex shader
out vec4 fColor; // out to display


void main(void) { 

	 fColor = vec4(color, 1.0);  
	 
	if (gl_FragCoord.x	<	200)  //fragment in device coordinates 
		fColor =	vec4(0.2,	0.2,	0.2,	1.0);	
	else
	if (gl_FragCoord.x	>	600)  //fragment in device coordinates 
		fColor =	vec4(0.2,	0.2,	0.2,	1.0);	

	if (gl_FragCoord.y	<	200)  //fragment in device coordinates 
		fColor =	vec4(0.2,	0.2,	0.2,	1.0);	
	else 	
	if (gl_FragCoord.y	>	600)  //fragment in device coordinates 
		fColor =	vec4(0.2,	0.2,	0.2,	1.0);	

		
 
		
		
}
