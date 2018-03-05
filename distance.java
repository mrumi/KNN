package KNN;

public class distance implements Comparable {
	
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

    public int compareTo(Object ob){
        distance temp=(distance)ob;

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
