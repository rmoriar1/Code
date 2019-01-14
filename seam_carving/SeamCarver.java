import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.AcyclicSP;
import java.awt.Color;

public class SeamCarver {
    private Picture current;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
    	   throw new java.lang.IllegalArgumentException();
        current = new Picture(picture.width(), picture.height());
        for (int i = 0; i < picture.width(); i++)
        {
            for (int j = 0; j < picture.height(); j++)
            {
                current.set(i, j, picture.get(i, j));
            }
        }
    }
    // current picture
    public Picture picture() {
        Picture copy = new Picture(current.width(), current.height());
        for (int i = 0; i < current.width(); i++)
        {
            for (int j = 0; j < current.height(); j++)
            {
                copy.set(i, j, current.get(i, j));
            }
        }
        return copy;
    }   
    // width of current picture
    public int width() {
        return current.width();
    }
    // height of current picture
    public int height() {
        return current.height();
    }
    // energy of pixel at column x and row y                        
    public double energy(int x, int y) {
    	if (x < 0 || x >= this.width() || y < 0 || y >= this.height())
    		throw new java.lang.IllegalArgumentException();
        if (x == 0 || y == 0 || x == this.width() - 1 || y == this.height() -1)
            return 1000.00;
        Color xMax = current.get(x+1, y);
        Color xMin = current.get(x-1, y);
        Color yMax = current.get(x, y+1);
        Color yMin = current.get(x, y-1);
        double rx = xMax.getRed() - xMin.getRed();
        double gx = xMax.getGreen() - xMin.getGreen();
        double bx = xMax.getBlue() - xMin.getBlue();
        double ry = yMax.getRed() - yMin.getRed();
        double gy = yMax.getGreen() - yMin.getGreen();
        double by = yMax.getBlue() - yMin.getBlue();
        double xGrad = rx * rx + gx * gx + bx * bx;
        double yGrad = ry * ry + gy * gy + by * by;
        return Math.sqrt(xGrad + yGrad);
    }
    // sequence of indices for horizontal seam             
    public int[] findHorizontalSeam() {
        Picture transpose = new Picture(current.height(), current.width());
        for (int i = 0; i < this.height(); i++)
        {
            for (int j = 0; j < this.width(); j++)
            {
                transpose.set(i, j, current.get(j, i));
            }
        }
        Picture temp = current;
        current = transpose;
        int [] hs = findVerticalSeam();
        current = temp;
        return hs;
    }   
    // sequence of indices for vertical seam         
    public int[] findVerticalSeam() {
        int [] vs = new int [this.height()];
        double [][] energyMatrix = new double[this.height()][this.width()];
        for (int i = 0; i < this.width(); i++)
        {
            for (int j = 0; j < this.height(); j++)
            {
                energyMatrix[j][i] = energy(i, j);
            }
        }
        EdgeWeightedDigraph g = new EdgeWeightedDigraph(current.width() * current.height() + 2);
        for (int i = 0; i < current.width(); i++)
        {
            g.addEdge(new DirectedEdge(0, i + 1, energyMatrix[0][i]));
            g.addEdge(new DirectedEdge(current.width() * current.height() - i, 
                current.width() * current.height() + 1, 1));
            for (int j = 0; j < current.height() - 1; j++)
            {
                int vertex = j * current.width() + i + 1;
                int vertexBelow = vertex + current.width();
                if (i != 0)
                    g.addEdge(new DirectedEdge(vertex, vertexBelow - 1, energyMatrix[j + 1] [i - 1]));
                if (i != current.width() - 1)
                    g.addEdge(new DirectedEdge(vertex, vertexBelow + 1, energyMatrix[j + 1] [i + 1]));
                g.addEdge(new DirectedEdge(vertex, vertexBelow, energyMatrix[j + 1][i]));
            }
        }
        AcyclicSP sp = new AcyclicSP(g, 0);
        int index = 0;
        for (DirectedEdge v : sp.pathTo(current.width() * current.height() + 1))
        {   
            if (index < vs.length)
                vs[index] = (v.to() - 1) % current.width();
            index++;
        }
        return vs;
    }      
    // remove horizontal seam from current picture         
    public void removeHorizontalSeam(int[] seam) {
    	if (seam == null || this.height() <= 1)
    		throw new java.lang.IllegalArgumentException();
    	if (seam.length != this.width())
    		throw new java.lang.IllegalArgumentException();
        for (int i = 1; i < seam.length; i++)
        {
            if (Math.abs(seam[i] - seam[i-1]) > 1)
                throw new java.lang.IllegalArgumentException();
        }
        Picture transpose = new Picture(current.height(), current.width());
        for (int i = 0; i < this.height(); i++)
        {
            for (int j = 0; j < this.width(); j++)
            {
                transpose.set(i, j, current.get(j, i));
            }
        }
        current = transpose;
        removeVerticalSeam(seam);
        Picture transpose2 = new Picture(current.height(), current.width());
        for (int i = 0; i < this.height(); i++)
        {
            for (int j = 0; j < this.width(); j++)
            {
                transpose2.set(i, j, current.get(j, i));
            }
        }
        current = transpose2;
    }
    // remove vertical seam from current picture  
    public void removeVerticalSeam(int[] seam) {
    	if (seam == null || this.width() <= 1)
    		throw new java.lang.IllegalArgumentException();
    	if (seam.length != this.height())
    		throw new java.lang.IllegalArgumentException();
        for (int i = 1; i < seam.length; i++)
        {
            if (Math.abs(seam[i] - seam[i-1]) > 1)
                throw new java.lang.IllegalArgumentException();
        }
        Picture newCur = new Picture(this.width() - 1, this.height());
        for (int i = 0; i < this.height(); i++)
        {
            for (int j = 0; j < this.width(); j++)
            {
                if (j < seam[i])
                    newCur.set(j, i, current.get(j, i));
                if (j > seam[i])
                    newCur.set(j - 1, i, current.get(j, i));
            }
        }
        current = newCur;
    } 
}