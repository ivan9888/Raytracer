/**
 * [1968] - [2023] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.cg.raytracer.lights;

import edu.up.isgc.cg.raytracer.Intersection;
import edu.up.isgc.cg.raytracer.Vector3D;

import java.awt.*;

/**
 * @author Ivan Andrdade
 */
public class PointLight extends Light{

    public PointLight(Vector3D position, Color color, double intensity) {
        super(position, color, intensity);
    }
    @Override
    public double getNDotL(Intersection intersection) {
        return Math.max(Vector3D.dotProduct(intersection.getNormal(),Vector3D.substract(getPosition(), intersection.getPosition())), 0.0);
    }
}
