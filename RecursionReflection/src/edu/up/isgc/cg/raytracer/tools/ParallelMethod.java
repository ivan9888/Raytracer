package edu.up.isgc.cg.raytracer.tools;

import edu.up.isgc.cg.raytracer.Intersection;
import edu.up.isgc.cg.raytracer.Ray;
import edu.up.isgc.cg.raytracer.Raytracer;
import edu.up.isgc.cg.raytracer.Vector3D;
import edu.up.isgc.cg.raytracer.lights.Light;
import edu.up.isgc.cg.raytracer.objects.Camera;
import edu.up.isgc.cg.raytracer.objects.Object3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author Ivan
 * @author2 Jafet
 */

/**
 * calculates the rgb color per pixel using parallel technique
 */

public class ParallelMethod {
    /**
     *
     * @param i receives the "x" position of the pixel
     * @param j receives the position "y" position of the pixel
     * @param mainCamera receives the camera
     * @param objects receives the objects of the scene
     * @param lights receives the lights of the scene
     * @param positionsToRaytrace receives the position of the scene from the camera
     * @param image receives the image where is going to be drawn the scene
     * @return
     */
    public static Runnable draw(int i, int j, Camera mainCamera, List<Object3D> objects , List<Light> lights, Vector3D[][] positionsToRaytrace,
                                BufferedImage image){
        double[] nearFarPlanes = mainCamera.getNearFarPlanes();
        double cameraZ = mainCamera.getPosition().getZ();
        java.lang.Runnable aRunnable = new Runnable(){
            @Override
            public void run() {
                double x = positionsToRaytrace[i][j].getX() + mainCamera.getPosition().getX();
                double y = positionsToRaytrace[i][j].getY() + mainCamera.getPosition().getY();
                double z = positionsToRaytrace[i][j].getZ() + mainCamera.getPosition().getZ();

                Ray ray = new Ray(mainCamera.getPosition(), new Vector3D(x, y, z));//se normaliza la direccion en RAY
                Intersection closestIntersection =Raytracer.raycast(ray, objects, null,
                        new double[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});

                Color pixelColor = Color.BLACK;
                if (closestIntersection != null) {
                    Color objColor = closestIntersection.getObject().getColor();

                    for (Light light : lights) {

                        Ray shadowRay = new Ray(closestIntersection.getPosition(),Vector3D.substract(light.getPosition(),
                                closestIntersection.getPosition()));
                        Intersection shadowIntersection =Raytracer.raycast(shadowRay, objects, closestIntersection.getObject(),
                                new double[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});

                        Vector3D intersectionToLight=Vector3D.substract(light.getPosition(),closestIntersection.getPosition());
                        Vector3D intersectionToCamera=Vector3D.substract(ray.getOrigin(),closestIntersection.getPosition());

                        double nDotL = light.getNDotL(closestIntersection);
                        double intensity = light.getIntensity() * nDotL;
                        double d= Vector3D.magnitude(intersectionToLight);
                        double fallOff=intensity/(Math.pow(d,2));
                        Color lightColor = light.getColor();


                        double specular=0;
                        //ambient
                        double ambient=closestIntersection.getObject().getLayers().getAmbient();

                        //specular
                        Vector3D halfVector=Vector3D.normalize(Vector3D.add(intersectionToLight,intersectionToCamera));
                        double normalDotHalf=Vector3D.dotProduct(closestIntersection.getNormal(),halfVector);//proyeccion de H sobre normal
                        double shininess=closestIntersection.getObject().getLayers().getShininess();
                        if(normalDotHalf>0)
                        {
                            specular=Math.pow(normalDotHalf,1/shininess);

                        }

                        double[] lightColors = new double[]{lightColor.getRed() / 255.0, lightColor.getGreen() / 255.0, lightColor.getBlue() / 255.0};
                        double[] objColors = new double[]{objColor.getRed() / 255.0, objColor.getGreen() / 255.0, objColor.getBlue() / 255.0};
                        double[] reflectColor=null;
                        double[] reflectColor1= {0,0,0,0};//penultimo ligar numero de reflejos y ultimo la distancia total
                        int numReflexions=3;

                        if(closestIntersection.getObject().getLayers().isReflection()){
                            reflectColor=Raytracer.reflection(ray,closestIntersection,objects,
                                    new double[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]},numReflexions,reflectColor1,light.getPosition());

                        }

                        for (int colorIndex = 0; colorIndex < objColors.length; colorIndex++) {

                            double lightFallOff = (specular + fallOff + ambient) * lightColors[colorIndex];
                            double lightNonFallOff = (specular + ambient) * lightColors[colorIndex];

                            if(closestIntersection.getObject().getLayers().isReflection())
                            {
                                if(reflectColor!=null)
                                {
                                    if(shadowIntersection==null)
                                    {
                                        if (fallOff > 0) {

                                            objColors[colorIndex] = (reflectColor[colorIndex] * lightFallOff * (shininess)) + (objColors[colorIndex] * lightFallOff * (1-shininess));
                                        } else {
                                            objColors[colorIndex] = (reflectColor[colorIndex] * lightNonFallOff * (shininess)) + (objColors[colorIndex] * lightNonFallOff * (1-shininess));
                                        }
                                    }
                                    else {
                                        if (fallOff > 0) {

                                            objColors[colorIndex] = (reflectColor[colorIndex] * lightFallOff)*0.4;
                                        } else {
                                            objColors[colorIndex] = (reflectColor[colorIndex] * lightNonFallOff)*.4;
                                        }
                                    }

                                }
                                else
                                {
                                    //En caso de que color no llegue a la luz en la reflexion, checamos si desde la interseccion se llega a ala camara
                                    if(shadowIntersection==null)
                                    {
                                        if(fallOff>0){
                                            objColors[colorIndex] *= lightFallOff;
                                        }
                                        else{
                                            objColors[colorIndex] *= lightNonFallOff;
                                        }
                                    }
                                    else
                                    {
                                        objColors[colorIndex]=0f;
                                    }
                                }
                            }
                            else{
                                if(fallOff>0){
                                    objColors[colorIndex] *= lightFallOff;
                                }
                                else{
                                    objColors[colorIndex] *= lightNonFallOff;
                                }
                            }
                        }
                        Color diffuse =new Color(Raytracer.clamp(objColors[0], 0, 1),Raytracer.clamp(objColors[1], 0, 1),Raytracer.clamp(objColors[2], 0, 1));
                        if(!closestIntersection.getObject().getLayers().isReflection())
                        {
                            if(shadowIntersection!=null)
                            {
                                diffuse=Color.BLACK;
                            }
                        }

                        pixelColor =Raytracer.addColor(pixelColor, diffuse);
                    }
                }
                setRGB(i, j, pixelColor,image);
            }
        };

        return aRunnable;
    }

    public static synchronized void setRGB(int x, int y, Color pixelColor,BufferedImage image){
        image.setRGB(x, y, pixelColor.getRGB());
    }
}
