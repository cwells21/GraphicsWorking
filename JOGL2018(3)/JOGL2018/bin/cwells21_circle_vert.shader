#version	450	

layout (location = 0) in vec3 iPosition;
layout (location = 1) in vec3 iColor;

out vec4 color;

void main(void)
{ 
	gl_Position = vec4(iPosition.x, iPosition.y, iPosition.z, 1.0);	
	color = iColor;
}