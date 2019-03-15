package com.pes.giraph;

import org.apache.hadoop.io.Text;

public class HitsScores
{
    private double hubScore;
    private double authScore;

    public HitsScores() {
        hubScore = 0.0;
        authScore = 0.0;
    }

    public HitsScores(double hubScore, double authScore) {
        this.hubScore = hubScore;
        this.authScore = authScore;
    }

    public HitsScores(Text t) {
        // Text is of the form: (Hub,Score) = (x,y)
        // First, get only the second part of the string
        // On the second part, remove the brackets
        // Then, parse the string to get the values
        String[] parts1 = t.toString().split(" = ");
        parts1[1] = parts1[1].substring(1, parts1[1].length() - 1);

        String[] parts = parts1[1].split(",");
        this.hubScore = Double.parseDouble(parts[0]);
        this.authScore = Double.parseDouble(parts[1]);
    }

    public Text toText() {
        return new Text(toString());
    }

    public Text toText(String prefix, String node) {
        return new Text(prefix + " " + node + " " + toString()); 
    }

    public double getHubScore() {
        return hubScore;
    }

    public double getAuthScore() {
        return authScore;
    }

    public void addToHubScore(double v) {
        this.hubScore += v;
    }

    public void addToAuthScore(double v) {
        this.authScore += v;
    }

    public void setHubScore(double v) {
        this.hubScore = v;
    }

    public void setAuthScore(double v) {
        this.authScore = v;
    }

    public void normalize(long n) {
        if ( n <= 0 )
            return;
        this.authScore = authScore / n;
        this.hubScore = hubScore / n;
    }

    @Override
    public String toString() {
        return "(Hub,Auth) = (" + String.format("%.4f", hubScore) + "," + String.format("%.4f", authScore) + ")";
    }
}
