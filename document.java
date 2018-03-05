package KNN;

import java.util.*;

public class document {

    private String label; 
    private Hashtable<String, Integer> words;
    private Hashtable<String, Double> tab;    
    private double tfsum;    
    
    public document(){
    	
    }
    public document(String l)
    {    	
    	label = l;
    	words = new Hashtable<String, Integer>();
    	tab = new Hashtable<String, Double>();
    	tfsum = 0;
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
    
    public void addWord(String word){
    	if(!words.containsKey(word))
        {
        	words.put(word,new Integer(1));
        }
        else
        {
        	Integer count=(Integer)words.get(word);
        	words.put(word,new Integer(count+1));
        }
    }
    
    public int getSize(){
    	return words.size();
    }
    
    public Enumeration<String> getWords(){
    	return words.keys();
    }
    
    public int getCount(String w){
    	return words.get(w);
    }
    
    public void change(String word, double val){
    	tab.put(word, val);
    }
    
    public double getTf(String w){
    	return tab.get(w);
    }
    
    public boolean check(String w){
    	if(words.contains(w))
    		return true;
    	return false;
    }
            
    public void wClear(){
    	words.clear();    	
    }  
    
    public void tClear(){
    	tab.clear();    	
    }
    
}

