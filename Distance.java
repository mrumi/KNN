/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

public class Distance implements Comparable
{
    private String label;    
    private double sim;    

	public Distance(String l, double s){
        this.label=l;
        this.sim=s;
    }

    public String getLabel(){
        return this.label;
    }

   /* public int getDist(){
        return this.dist;
    }*/
    public double getSim(){
        return this.sim;
    }

    public int compareTo(Object ob){
        Distance temp=(Distance)ob;

        if(this.getSim()<temp.getSim())
        {
            return 1;
        }
        else if(this.getSim()>temp.getSim())
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}