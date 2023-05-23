/**
 * [1968] - [2023] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.cg.raytracer;


import edu.up.isgc.cg.raytracer.lights.Light;
import edu.up.isgc.cg.raytracer.lights.PointLight;
import edu.up.isgc.cg.raytracer.objects.*;
import edu.up.isgc.cg.raytracer.tools.BlinnLayers;
import edu.up.isgc.cg.raytracer.tools.OBJReader;
import edu.up.isgc.cg.raytracer.tools.ParallelMethod;



import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
/**
 * @author Ivan Andrade
 * @author2 Jafet Rodríguez
 * @version 0.8
 */

/**
 * Raytracer is the main class where the render is created
 */
public class Raytracer {

    public static void main(String[] args) {
        System.out.println(new Date());

        Scene scene01 = new Scene();
        scene01.setCamera(new Camera(new Vector3D(0, 0.5, -4), 60, 60, 800, 800, 0.6, 20.0));
        //scene01.addLight(new DirectionalLight(new Vector3D(0.0, 0.0, 1.0), Color.CYAN, 1.1));
        //scene01.addLight(new DirectionalLight(new Vector3D(0.0, -1.0, 0.0), Color.RED, 1.4));
        scene01.addLight(new PointLight(new Vector3D(0.5, 3f, -4.5), Color.WHITE, 0.08));
        scene01.addLight(new PointLight(new Vector3D(-2.5, 4, -1), Color.WHITE, 0.08));
        //scene01.addLight(new PointLight(new Vector3D(3, 3f, 6.5), Color.WHITE, 0.05));



        scene01.addObject(OBJReader.getModel3D("SmallTeapot.obj", new Vector3D(-1, -2f, 3.5), Color.RED,
                new BlinnLayers(0.1,0.2,true,false)));

        scene01.addObject(OBJReader.getModel3D("wall.obj", new Vector3D(0, 0, 10), Color.DARK_GRAY,
                new BlinnLayers(0.1,.5,true,false)));
        //scene01.addObject(new Sphere(new Vector3D(0.5, 1, 8), 0.8, Color.RED));
//        scene01.addObject(new Sphere(new Vector3D(1, -1.5f, 2), 0.3, Color.BLUE,
//                new BlinnLayers(0.3,.3,true,false)));
        scene01.addObject(new Sphere( new Vector3D(2, 0f, 6.5), 1.5,Color.DARK_GRAY,
                new BlinnLayers(0.3,.2,true,false)));

        scene01.addObject(new Sphere(new Vector3D(1.5, -.5, 3), .3, Color.BLUE,
                new BlinnLayers(0.3,.4,true,false)));

        scene01.addObject(OBJReader.getModel3D("Cube.obj", new Vector3D(-1.5, -.9, 1), Color.RED,
                new BlinnLayers(0.1,0.5,false,true)));

        scene01.addObject(new Model3D(new Vector3D(0, -2, 4),
                new Triangle[]{
                        new Triangle(new Vector3D(20f,0f,10f), new Vector3D(-20f,0f,10f), new Vector3D(-20f,0f,-10f)),
                        new Triangle(new Vector3D(-20f,0f,-10f), new Vector3D(20f,0f,-10f), new Vector3D(10f,0f,10f))},
                Color.GRAY,new BlinnLayers(.3,0.4,true,false)));
        //scene01.addObject(OBJReader.getModel3D("Cube.obj", new Vector3D(0, -2.5, 1), Color.CYAN));
 /*
        Scene scene02 = new Scene();
        scene02.setCamera(new Camera(new Vector3D(0, 0, -4), 60, 60, 800, 800, 0.6, 50.0));
        //scene02.addLight(new DirectionalLight(new Vector3D(0.0, 0.0, 1.0), Color.WHITE, 0.8));
        //scene02.addLight(new DirectionalLight(new Vector3D(0.0, -0.1, 0.1), Color.WHITE, 0.2));
        //scene02.addLight(new DirectionalLight(new Vector3D(-0.2, -0.1, 0.0), Color.WHITE, 0.2));
        scene02.addLight(new PointLight(new Vector3D(0.0, 1.0, 3.0), Color.WHITE, 0.8));

        scene02.addObject(new Sphere(new Vector3D(0.0, 1.0, 5.0), 0.5, Color.RED));
        scene02.addObject(new Sphere(new Vector3D(0.5, 1.0, 4.5), 0.25, new Color(200, 255, 0)));
        scene02.addObject(new Sphere(new Vector3D(0.35, 1.0, 4.5), 0.3, Color.BLUE));
        scene02.addObject(new Sphere(new Vector3D(4.85, 1.0, 4.5), 0.3, Color.PINK));
        scene02.addObject(new Sphere(new Vector3D(2.85, 1.0, 304.5), 0.5, Color.BLUE));
        scene02.addObject(OBJReader.getModel3D("Cube.obj", new Vector3D(0f, -2.5, 1.0), Color.WHITE));
        scene02.addObject(OBJReader.getModel3D("CubeQuad.obj", new Vector3D(-3.0, -2.5, 3.0), Color.GREEN));
        scene02.addObject(OBJReader.getModel3D("SmallTeapot.obj", new Vector3D(2.0, -1.0, 1.5), Color.BLUE));
        scene02.addObject(OBJReader.getModel3D("Ring.obj", new Vector3D(2.0, -1.0, 1.5), Color.BLUE));


 */
        //BufferedImage image = raytrace(scene02);
        BufferedImage image = parallelRaytrace(scene01);
        //BufferedImage image = parallelRaytrace(scene02);
        File outputImage = new File("elWeno11.png");

        try {
            ImageIO.write(image, "png", outputImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(new Date());
    }

    /**
     *this method do the raytrace using a parallel technique
     *
     * @param scene receives the scene object that contains all the elements of the scene
     * @return the render of the scene
     */
    public static BufferedImage parallelRaytrace(Scene scene) {

        ExecutorService executorService = Executors.newFixedThreadPool(16);

        Camera mainCamera = scene.getCamera();
        Vector3D[][] positionsToRaytrace = mainCamera.calculatePositionsToRay();
        BufferedImage image = new BufferedImage(mainCamera.getResolutionWidth(), mainCamera.getResolutionHeight(), BufferedImage.TYPE_INT_RGB);
        List<Object3D> objects = scene.getObjects();
        List<Light> lights = scene.getLights();

        for (int i = 0; i < positionsToRaytrace.length; i++) {
            for (int j = 0; j < positionsToRaytrace[i].length; j++) {
                ParallelMethod runnable = new ParallelMethod();
                executorService.execute(runnable.draw(i, j,mainCamera,objects,lights,positionsToRaytrace,image));
            }
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (!executorService.isTerminated()) {
                System.err.println("Cancel non-finished");
            }
        }
        executorService.shutdownNow();
        return image;
    }

    public static float clamp(double value, double min, double max) {
        if (value < min) {
            return (float) min;
        }
        if (value > max) {
            return (float) max;
        }
        return (float) value;
    }

    public static Color addColor(Color original, Color otherColor) {
        float red = clamp((original.getRed() / 255.0) + (otherColor.getRed() / 255.0), 0, 1);
        float green = clamp((original.getGreen() / 255.0) + (otherColor.getGreen() / 255.0), 0, 1);
        float blue = clamp((original.getBlue() / 255.0) + (otherColor.getBlue() / 255.0), 0, 1);
        return new Color(red, green, blue);
    }

    public static Intersection raycast(Ray ray, List<Object3D> objects, Object3D caster, double[] clippingPlanes) {
        Intersection closestIntersection = null;

        for (int k = 0; k < objects.size(); k++) {
            Object3D currentObj = objects.get(k);
            if (caster == null || !currentObj.equals(caster)) {
                Intersection intersection = currentObj.getIntersection(ray);
                if (intersection != null) {
                    double distance = intersection.getDistance();
                    double intersectionZ = intersection.getPosition().getZ();
                    if (distance >= 0 &&
                            (closestIntersection == null || distance < closestIntersection.getDistance()) &&
                            (clippingPlanes == null || (intersectionZ >= clippingPlanes[0] && intersectionZ <= clippingPlanes[1]))) {
                        closestIntersection = intersection;
                    }
                }
            }
        }

        return closestIntersection;
    }


    public static double[] refraction(Ray ray,Intersection closestIntersection,List<Object3D> objects,double[] clippingPlanes,boolean in,boolean firstIntersectionIsRefraction,double[] colorReflection,Vector3D lightPos)
    {
        Vector3D normal=closestIntersection.getNormal();;
        Vector3D v0=Vector3D.normalize(ray.getDirection());
        Vector3D refractorDirection;

        double nGlass=1.5,nWater=1.3,n;
        double normalDotv0,c2;
        n=Math.pow(nGlass,2);

        normalDotv0=Vector3D.dotProduct(normal,v0);


        if(normalDotv0<0)
        {
            n=Math.pow(1/nGlass,2);
            normalDotv0=-normalDotv0;
        }
        else {
            normal=Vector3D.scalarMultiplication(normal,-1);
        }

        c2=Math.sqrt(1-n*(1-Math.pow(normalDotv0,2)));
        refractorDirection=Vector3D.add(Vector3D.scalarMultiplication(v0,Math.sqrt(n)),Vector3D.scalarMultiplication(normal,(Math.sqrt(n)*normalDotv0)-c2));

        Ray refractionRay=new Ray(closestIntersection.getPosition(),refractorDirection);
        Intersection intersectionRefraction=raycast(refractionRay,objects, closestIntersection.getObject(),clippingPlanes);



        if(intersectionRefraction!=null)
        {
            Ray shadowRay = new Ray(intersectionRefraction.getPosition(),Vector3D.substract(lightPos,
                    intersectionRefraction.getPosition()));
            Intersection shadowIntersection =Raytracer.raycast(shadowRay, objects, intersectionRefraction.getObject(),clippingPlanes);

            if(intersectionRefraction.getObject().getLayers().isRefraction())
            {
                if(in && firstIntersectionIsRefraction)//solo funciona cuando viene desde la camara
                {
                    in=false;
                    return refraction(refractionRay,intersectionRefraction,objects,clippingPlanes,in,true,colorReflection,lightPos);
                }
                else if(in && !firstIntersectionIsRefraction)
                {
                    in=false;
                    return refraction(refractionRay,intersectionRefraction,objects,clippingPlanes,in,false,colorReflection,lightPos);
                }
                else if(!in && !firstIntersectionIsRefraction)
                {
                    in=true;
                    return refraction(refractionRay,intersectionRefraction,objects,clippingPlanes,in,false,colorReflection,lightPos);
                }

            }
            else if (intersectionRefraction.getObject().getLayers().isReflection())
            {
                return reflection(refractionRay,intersectionRefraction,objects,clippingPlanes,false,colorReflection,lightPos);
            }
            else
            {
                double[] tempColor={intersectionRefraction.getObject().getColor().getRed() / 255.0, intersectionRefraction.getObject().getColor().getGreen() / 255.0, intersectionRefraction.getObject().getColor().getBlue() / 255.0};

                for (int i = 0; i < 3; i++) {

                    if (firstIntersectionIsRefraction)
                    {
                        if (shadowIntersection == null) {
                            colorReflection[i] = tempColor[i];
                        }
                        else
                        {
                           colorReflection[i]=0;
                        }
                    }
                    else//si ray no viene de la camara
                    {
                        if (shadowIntersection == null)
                        {
                            colorReflection[i] += tempColor[i];
                        }
                    }
                }
                return colorReflection;
            }
        }
        else
        {
            if(firstIntersectionIsRefraction)
            {
                colorReflection=new double[] {0,0,0};
                return colorReflection;
            }
            else
            {
                return colorReflection;

            }
        }
        return colorReflection;
    }


    public static double[] reflection(Ray ray,Intersection closestIntersection,List<Object3D> objects,double[] clippingPlanes,boolean firstIntersectionIsReflection,double[] colorReflection,Vector3D lightPos){

        double[] tempColor= {0,0,0};

        Vector3D directionFormula=ray.getDirection();// d es la direccion de camara a interseccion siguiendo for,ua

        Vector3D reflection=Vector3D.substract(directionFormula,Vector3D.scalarMultiplication(Vector3D.scalarMultiplication(closestIntersection.getNormal(),Vector3D.dotProduct(directionFormula,closestIntersection.getNormal())),2));
        Ray reflectionRay=new Ray(closestIntersection.getPosition(),reflection);
        Intersection intersectionReflection=raycast(reflectionRay,objects, closestIntersection.getObject(),clippingPlanes);

        double[] tempColor1 = new double[]{closestIntersection.getObject().getColor().getRed() / 255.0, closestIntersection.getObject().getColor().getGreen() / 255.0, closestIntersection.getObject().getColor().getBlue() / 255.0};

        if(intersectionReflection!=null)
        {

            Ray shadowRay = new Ray(intersectionReflection.getPosition(),Vector3D.substract(lightPos,
                    intersectionReflection.getPosition()));
            Intersection shadowIntersection =Raytracer.raycast(shadowRay, objects, intersectionReflection.getObject(),clippingPlanes);
            //double distance=closestIntersection.getDistance();

            if(intersectionReflection.getObject().getLayers().isReflection() || !intersectionReflection.getObject().getLayers().isRefraction())
            {

                tempColor = new double[]{intersectionReflection.getObject().getColor().getRed() / 255.0, intersectionReflection.getObject().getColor().getGreen() / 255.0, intersectionReflection.getObject().getColor().getBlue() / 255.0};
                for (int i = 0; i < 3; i++) {

                    if (shadowIntersection == null) {
                        if (firstIntersectionIsReflection) //en parallel se le asinga el color de la interseccion original
                        {
                            colorReflection[i] = tempColor[i];
                        } else //para cuando se mande a llamar la funcion desde refraction
                        {
                            colorReflection[i] = (tempColor1[i] * (1 - closestIntersection.getObject().getLayers().getShininess()) + tempColor[i] * closestIntersection.getObject().getLayers().getShininess());
                        }
                    } else//en caso de que la proxima inetrseccion no este iluminada
                    {
                        if (firstIntersectionIsReflection) {
                            colorReflection[i] += 0;//negro porque es sombra
                        } else //sea llamado desde refraction
                        {
                            colorReflection[i] = (tempColor1[i] * (1 - closestIntersection.getObject().getLayers().getShininess()) + tempColor[i] * closestIntersection.getObject().getLayers().getShininess());
                        }
                    }
                }
                return colorReflection;
            }
            else
            {

                if(!firstIntersectionIsReflection)//el objeto del que viene el rayo sera un refraction
                {
                    return tempColor1;
                }
                else
                {
                    return refraction(reflectionRay, intersectionReflection, objects, clippingPlanes, true, false, colorReflection, lightPos);
                }
            }
        }
        else
        {
            if(!firstIntersectionIsReflection)
            {
                return tempColor1;
            }
            else {
                return tempColor;
            }

        }
    }

}
