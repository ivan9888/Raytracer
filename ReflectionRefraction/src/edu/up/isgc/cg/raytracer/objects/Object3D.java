/**
 * [1968] - [2023] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.cg.raytracer.objects;

import edu.up.isgc.cg.raytracer.Intersection;
import edu.up.isgc.cg.raytracer.Ray;
import edu.up.isgc.cg.raytracer.Vector3D;
import edu.up.isgc.cg.raytracer.tools.BlinnLayers;

import java.awt.*;

/**
 * @author Jafet Rodríguez
 */
public abstract class Object3D implements IIntersectable{
    private Color color;
    private Vector3D position;
    private BlinnLayers layers;

    public Object3D(Vector3D position, Color color,BlinnLayers layers) {
        setPosition(position);
        setColor(color);
        setLayers(layers);
    }

    public BlinnLayers getLayers() {
        return layers;
    }

    public void setLayers(BlinnLayers layers) {
        this.layers = layers;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

}
