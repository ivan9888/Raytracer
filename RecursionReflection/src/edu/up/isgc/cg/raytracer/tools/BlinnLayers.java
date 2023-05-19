package edu.up.isgc.cg.raytracer.tools;

public class BlinnLayers {

    private double ambient;
    private double shininess;
    private boolean reflection;
    private boolean refraction;

    public BlinnLayers(double ambient, double shininess,boolean reflection,boolean refraction) {
        setAmbient(ambient);
        setShininess(shininess);
        setReflection(reflection);
        setRefraction(refraction);
    }

    public boolean isReflection() {
        return reflection;
    }

    public void setReflection(boolean reflection) {
        this.reflection = reflection;
    }

    public boolean isRefraction() {
        return refraction;
    }

    public void setRefraction(boolean refraction) {
        this.refraction = refraction;
    }

    public double getAmbient() {
        return ambient;
    }

    public void setAmbient(double ambient) {
        this.ambient = ambient;
    }

    public double getShininess() {
        return shininess;
    }

    public void setShininess(double shininess) {
        this.shininess = shininess;
    }
}
