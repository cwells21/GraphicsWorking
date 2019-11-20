
#version	450	

uniform float theta; // input angle for rotation 
uniform mat4 mv_matrix; 
uniform mat4 proj_matrix;
uniform mat4 rotation_matrix;
uniform mat4 translation_matrix;
uniform mat4 scale_matrix;


layout (location = 0) in vec4 iPosition; // VBO: vbo[0]
layout (location = 1) in vec3 iColor;    // VBO: vbo[1]



out vec3 color; // output to fragment shader

void	main(void)	{	
	vec3 vColor; // vertex color

	float dt = theta/50; 
	
//we do the rotation in shader
			 

	
	gl_Position = proj_matrix*mv_matrix*vec4(iPosition.x, iPosition.y, iPosition.z, 1.0);	

	if (iPosition.x == 0 && iPosition.y == 0 && iPosition.z == 0) vColor = vec3(0.5, 0.5, 0.5); 
	else vColor = normalize(iPosition); // avoid zero length vector

	// color specified according to the vertex position
    color =	vec3(abs(vColor.x), abs(vColor.y), abs(vColor.x*vColor.y));

}