package test;

import java.util.*;

public class Document {
    private String label;
    private Hashtable tab;
    private Hashtable words;
    private double tfsum;
    //int wordcount;
    
    public Document()    	
    {
    	
    }
    public Document(String l)    	
    {
    	tab=new Hashtable();
    	label=l;
    	words=new Hashtable();
    	tfsum=0;
    }
    
    public String getLabel(){
    	return this.label;
    }
    
    public void setTfSum(double sum){
    	this.tfsum = sum;
    }
    
    public double getTfSum(){
    	return this.tfsum;
    }
    
    public Hashtable getTab(){
    	return this.tab;
    }
    
    public Hashtable getWords(){
    	return this.words;
    }

    
}
