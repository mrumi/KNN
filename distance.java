package KNN;

public class distance implements Comparable<distance> {
	
	private String label;
    private double sim;    

	public distance(String l, double s){
        this.label=l;
        this.sim=s;
    }

    public String getLabel(){
        return this.label;
    }
   
    public double getSim(){
        return this.sim;
    }

    public int compareTo(distance dist){        

        if(this.getSim() < dist.getSim())
        {
            return 1;
        }
        else if(this.getSim() > dist.getSim())
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

}
