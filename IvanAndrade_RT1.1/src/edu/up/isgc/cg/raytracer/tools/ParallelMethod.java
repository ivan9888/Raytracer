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
                        System.out.println((i/(double)(positionsToRaytrace.length))*100+"%");

                        Ray shadowRay = new Ray(closestIntersection.getPosition(),Vector3D.substract(light.getPosition(),
                                closestIntersection.getPosition()));
                        Intersection shadowIntersection =Raytracer.raycast(shadowRay, objects, closestIntersection.getObject(),
                                new double[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});

                        double distanceShadow=Vector3D.magnitude(Vector3D.substract(light.getPosition(),closestIntersection.getPosition()));
                        if(shadowIntersection!=null) {
                            if (shadowIntersection.getDistance() > distanceShadow) {
                                shadowIntersection = null;
                            }
                        }

                        Color lightColor = light.getColor();

                        double[] lightColors = new double[]{lightColor.getRed() / 255.0, lightColor.getGreen() / 255.0, lightColor.getBlue() / 255.0};
                        double[] objColors = new double[]{objColor.getRed() / 255.0, objColor.getGreen() / 255.0, objColor.getBlue() / 255.0};
                        double[] reflectColor1= {0,0,0};//penultimo ligar numero de reflejos y ultimo la distancia total

                        double fallOffLight=Raytracer.lightFO(closestIntersection,light,ray);

                        if(closestIntersection.getObject().getLayers().isReflection()){
                            objColors=Raytracer.reflection(ray,closestIntersection,objects, new double[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]},reflectColor1,light);
                            for(int index=0;index<3;index++)
                            {
                                objColors[index]*=fallOffLight*lightColors[index];
                            }
                        }
                        if(closestIntersection.getObject().getLayers().isRefraction())
                        {
                            objColors=Raytracer.refraction(ray,closestIntersection,objects, new double[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]},true,true,reflectColor1,light);

                        }
                        for (int colorIndex = 0; colorIndex < objColors.length; colorIndex++) {

                            if(!closestIntersection.getObject().getLayers().isReflection() && !closestIntersection.getObject().getLayers().isRefraction() )
                            {
                                if(shadowIntersection!=null) {
                                    objColors[colorIndex] *= fallOffLight * lightColors[colorIndex]*(1-closestIntersection.getObject().getLayers().getShininess());
                                }
                                else {
                                    objColors[colorIndex] *= fallOffLight * lightColors[colorIndex];
                                }
                            }
                            else {
                                if (closestIntersection.getObject().getLayers().isReflection())

                                {
                                    if(shadowIntersection!=null)
                                    {
                                        objColors[colorIndex] *=(1-closestIntersection.getObject().getLayers().getShininess());;
                                    }
                                }
                            }

                        }
                        Color diffuse =new Color(Raytracer.clamp(objColors[0], 0, 1),Raytracer.clamp(objColors[1], 0, 1),Raytracer.clamp(objColors[2], 0, 1));
//
                        if(shadowIntersection!=null)
                        {
                            if(!closestIntersection.getObject().getLayers().isReflection() && !closestIntersection.getObject().getLayers().isReflection())
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