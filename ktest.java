package KNN;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ktest {
	
	private ArrayList<String> topicList;
	private ArrayList<document> trainDocList;
	private ArrayList<document> testDocList;
	private ArrayList<distance> hdistance;
	private ArrayList<distance> edistance;
	private ArrayList<distance> similarity;
	private Hashtable<String, Double> trainwords;	
   	
   	public ktest(){
		topicList = new ArrayList<String>();
		trainDocList = new ArrayList<document>();
		testDocList = new ArrayList<document>();
		hdistance = new ArrayList<distance>();
		edistance = new ArrayList<distance>();
		similarity = new ArrayList<distance>();
		trainwords = new Hashtable<String,Double>();
	}  
   	
   	private String removeNoise(String str) {
		String noiseList[] = {"a","an","and","the","in","on","or","am","is",
		        "are","for","but","it","this","has","have","will","shall","be",
		        "been","would","should","that","to","of","off","at","however",
		        "was","were","can","could","from","if","at","with","do","does",
		        "did","done","who","where","how","what","when","which","then","there",
		        "about","into","by","its",".",",","-",";","\"","(",")","\\",
		        "out","up","with","through","about","above","below","before","after","reuter","reuters"};
		   	ArrayList<String>noiseArrList=new ArrayList<String>(Arrays.asList(noiseList));   
        str=str.toLowerCase();
        if(noiseArrList.contains(str))
            return null;
        str=str.replaceAll(",","");
        str=str.replaceAll("\\.","");
        str=str.replaceAll(";","");
        str=str.replaceAll("-","");
        str=str.replaceAll("\"","");
        str=str.replaceAll("\\(","");
        str=str.replaceAll("\\)","");
        str=str.replaceAll("'","");
        str=str.replaceAll(":","");
        if(str.isEmpty())
            return null;
        String expression = "^[-+]?[0-9]*\\.?[0-9]+$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(str);
        if(matcher.matches())
            return null;
        return str;
    }
   	
   	public void readTopics(String filename) {    	
    	try {
    		BufferedReader bin = new BufferedReader(new FileReader(new File(filename)));
        	String input = "";
        	while(true) {        	
            	try {            	
                	input = bin.readLine();                	
            	}
            	catch(Exception ex) {            	
                	System.out.println("Exception in readline: "+ex);
            	}
            	if(input == null)
                	break;
            	if(input.equals(""))
                	continue;            	
            	StringTokenizer st = new StringTokenizer(input);
            	String str = st.nextToken();
            	str = removeNoise(str);
            	if(str == null)
                    continue;
            	topicList.add(str);
        	}
	        System.out.println("Topic: "+topicList.size());
	        try {	        
	            bin.close();
	        }
	        catch(Exception ex) {	        
	            System.out.println("Exception : "+ex);
	        }
        }
    	catch(Exception rx) {    	
    		System.out.println("Exception : "+rx);
    	}
    }
   	
   	public void readTrainData(String filename) {    	
    	document doc = new document();    	
    	try {
    		BufferedReader bin=new BufferedReader(new FileReader(new File(filename)));
        	String input = "";
        	while(true) {
            	try {
                	input = bin.readLine();
            	}
            	catch(Exception ex) {
                	System.out.println("Exception in readline: "+ex);
            	}
            	if(input == null)
                	break;
            	if(input.equals(""))
                	continue;
            	StringTokenizer st=new StringTokenizer(input);
            	int x = st.countTokens();
            	if(x == 1) { // found a label
            	
                	String str = st.nextToken();
                	str = removeNoise(str);
                	if(str == null)
                    	continue;
                	if(topicList.contains(str)) {                		
                		doc = new document(str);
                		trainDocList.add(doc);
                	}
            	}
	            while(st.hasMoreTokens()) {
	                String str = st.nextToken();
	                str = removeNoise(str);
	                if(str == null)
	                    continue;	                
                    doc.addWord(str);
	                if(!trainwords.containsKey(str))
	                	trainwords.put(str, new Double(1));
	            }
        	}
	        try {	        
	            bin.close();
	        }
	        catch(Exception ex) {	        
	            System.out.println("Exception: "+ex);
	        }
    	}
    	catch(Exception rx) {    	
    		System.out.println("Exception: "+rx);
    	}
    	System.out.println("doc number "+trainDocList.size());
    	System.out.println(trainwords.size());    	
    }
   	   	
    public void readTestData(String filename) {
    	document doc = new document();    	
    	try{
    		BufferedReader bin = new BufferedReader(new FileReader(new File(filename)));
        	String input = "";
        	while(true) {        	
            	try {            	
                	input = bin.readLine();
            	}
            	catch(Exception ex) {            	
                	System.out.println("Exception in readline: "+ex);
            	}
            	if(input == null)
                	break;
            	if(input.equals(""))
                	continue;
            	StringTokenizer st = new StringTokenizer(input);
            	int x = st.countTokens();
            	if(x == 1) {// found a label
            	
                	String str = st.nextToken();
                	str = removeNoise(str);
                	if(str == null)
                    	continue;
                	if(topicList.contains(str)) {
                		//c2++;
                		doc = new document(str);
                		testDocList.add(doc);
                	}
            	}
	            while(st.hasMoreTokens()) {
	                String str = st.nextToken();
	                str = removeNoise(str);
	                if(str == null)
	                    continue;
	                doc.addWord(str);	                
	            }
        	}
	        try {
	            bin.close();
	        }
	        catch(Exception ex) {
	            System.out.println("Exception: "+ex);
	        }
    	}
    	catch(Exception rx) {
    		System.out.println("Exception: "+rx);
    	}
    	System.out.println("doc number "+testDocList.size());    	        
        topicList.clear();
    }
    
    public void calculateDistance() {
    	int ht = 0, hf = 0, ht3 = 0, hf3 = 0, ht5 = 0, hf5 = 0;
    	int et = 0, ef = 0, et3 = 0, ef3 = 0, et5 = 0, ef5 = 0;
    	for(int i = 0; i < testDocList.size(); i++) {
    		document ds = (document)testDocList.get(i);    		
    		for(int j = 0; j < trainDocList.size(); j++) {
    			document dr = (document)trainDocList.get(j);
    			Enumeration<String> test = ds.getWords();
		    	Enumeration<String> train = dr.getWords();
		    	int hamDist = 0;
		    	int euclidDist = 0;
		    	int b = 0;
		    	int a = 0;
		    	while(test.hasMoreElements()) {		    	
		    		String sr = (String)test.nextElement();
		    		a = (Integer)ds.getCount(sr);
		    		if(dr.check(sr)) {		    		
		    			b = (Integer)dr.getCount(sr);
		    			euclidDist+= (a-b)*(a-b);
		    		}
		    		else {		    		
		    			euclidDist+= a*a;
		    			hamDist+= 1;
		    		}
		    	}
		    	while(train.hasMoreElements()) {		    	
		    		String sr = (String)train.nextElement();
		    		if(!ds.check(sr)) {		    		
		    			b = (Integer)dr.getCount(sr);
		    			euclidDist+= b*b;
		    			hamDist+= 1;
		    		}
		    	}
		    	int dist = (int)Math.sqrt((double)euclidDist);

		    	distance hd = new distance(dr.getLabel(),hamDist);
		    	hdistance.add(hd);
		    	distance ed = new distance(dr.getLabel(),dist);		    	
		    	edistance.add(ed);
    		}
    		//System.out.println(ds.getLabel()+" "+i);
    		Collections.sort(hdistance);
    		Hashtable<String,Integer> hpredict = new Hashtable<String, Integer>();
    		String hp = "";    		
    		for(int q = 0; q < 3; q++)
            {
                hpredict.clear();
                for(int p = 0; p < (2*q+1); p++) {                
                	distance hdis=(distance)hdistance.get(p);
                    String hlabel=hdis.getLabel(); //docTopic=distance.get(p).getName();
                    if(hpredict.containsKey(hlabel)) {                    
                    	Integer hcount = (Integer)hpredict.get(hlabel);
                    	hpredict.put(hlabel, new Integer(hcount+1));
                    }
                    else
                    	hpredict.put(hlabel, new Integer(1));
                }
                int max=0;
            	Enumeration<String> he=hpredict.keys();
            	while(he.hasMoreElements()) {            	
            		String hs=(String)he.nextElement();
            		int k=(Integer)hpredict.get(hs);
            		if(k>max) {            		
            			max=k;
            			hp=hs;
            		}
            	}
            	if(hp.matches(ds.getLabel())) {            	
            		if(q == 0)
            			ht++;
            		else if(q == 1)
            			ht3++;
            		else
            			ht5++;
            	}
    			else {    			
    				if(q == 0)
            			hf++;
            		else if(q == 1)
            			hf3++;
            		else
            			hf5++;
    			}
            }
    		hdistance.clear();
    		Collections.sort(edistance);
    		Hashtable<String, Integer> epredict = new Hashtable<String, Integer>();
    		String ep = "";
    		for(int q = 0; q < 3; q++) {            
                epredict.clear();
                for(int p = 0; p < (2*q+1); p++) {                
                	distance edis = (distance)edistance.get(p);
                    String elabel = edis.getLabel(); //docTopic=distance.get(p).getName();

                    if(epredict.containsKey(elabel)) {                    
                    	Integer ecount = (Integer)epredict.get(elabel);
                    	epredict.put(elabel, new Integer(ecount+1));
                    }
                    else
                    	epredict.put(elabel, new Integer(1));
                }
                int max = 0;
            	Enumeration<String> eu = epredict.keys();
            	while(eu.hasMoreElements()) {            	
            		String es = (String)eu.nextElement();
            		int k = (Integer)epredict.get(es);
            		if(k > max) {
            			max = k;
            			ep = es;
            		}
            	}
            	if(ep.matches(ds.getLabel())) {            	
            		if(q==0)
            			et++;
            		else if(q==1)
            			et3++;
            		else
            			et5++;
            	}
    			else {    			
    				if(q == 0)
            			ef++;
            		else if(q == 1)
            			ef3++;
            		else
            			ef5++;
    			}
            }
			edistance.clear();			
    	}
    	double haccur1 = (ht*100.0)/(ht+hf);
    	double haccur3 = (ht3*100.0)/(ht3+hf3);
    	double haccur5 = (ht5*100.0)/(ht5+hf5);
    	System.out.println("hamming accuracy for K=1 "+haccur1);
    	System.out.println("hamming accuracy for K=3 "+haccur3);
    	System.out.println("hamming accuracy for K=5 "+haccur5);

    	double eaccur1 = (et*100.0)/(et+ef);
    	double eaccur3 = (et3*100.0)/(et3+ef3);
    	double eaccur5 = (et5*100.0)/(et5+ef5);
    	System.out.println("euclid accuracy for K=1 "+eaccur1);
    	System.out.println("euclid accuracy for K=3 "+eaccur3);
    	System.out.println("euclid accuracy for K=5 "+eaccur5);
    }
    
    public void calIDF()
    {
    	int docnum=trainDocList.size();///D
        Enumeration<String> e=trainwords.keys();        
        while(e.hasMoreElements())
        {
        	String str=(String)e.nextElement();
            int count=0;
            for(int k=0;k<trainDocList.size();k++)
            {
            	document d=(document)trainDocList.get(k);
            	if(d.check(str))
            		count++;
            }
            double d_cw=(double)docnum/count;//D/C(w)
            double idf= Math.log(d_cw);
            trainwords.put(str,(Double)idf);
        }
        System.out.println("idf done");
    }
       	
   	public void calTf_train() {
    	for(int i = 0; i < trainDocList.size(); i++)
    	{
    		document d = (document)trainDocList.get(i);
    		double tfsum = 0;
    		int sz = d.getSize();
    		Enumeration<String> e = d.getWords();    		
    		while(e.hasMoreElements())
    		{
    			String s = (String)e.nextElement();
    			int nw = d.getCount(s);
    			double tf = (double)nw/(sz*1.0);
    			double idf = (Double)trainwords.get(s);
    			double tf_idf = tf*idf;
    			d.change(s, tf_idf);    			
    			tfsum+= Math.pow(tf_idf,2);
    		}    		
    		d.setTfSum(Math.sqrt(tfsum));
    		d.wClear();
    	}    	
    	System.out.println("tf done");
    }
   	
   	public void calTf_test() {   		
   		for(int i = 0; i < testDocList.size(); i++) {
    		document ds = (document)testDocList.get(i);    	
    		int sz = ds.getSize();    		
    		Enumeration<String> es = ds.getWords();
    		double tfsum = 0;
    		while(es.hasMoreElements()) {    		
    			String s = (String)es.nextElement();
    			int nw = ds.getSize();
    			double tf = (double)nw/(sz*1.0);
    			double idf=0;
    			if(trainwords.containsKey(s))
    				idf=(Double)trainwords.get(s);
    			double tf_idf=tf*idf;
    			tfsum+= Math.pow(tf_idf,2);   
    			ds.change(s, tf_idf);    			    			
    		}
    		ds.setTfSum(Math.sqrt(tfsum)); 
    		ds.wClear();
   		}
    	System.out.println("tf test done");
    }

    public double calTf_IDF()
    {    	
    	int ct=0,cf=0;  
    	for(int i=0;i<testDocList.size();i++) {
    		document ds=(document)testDocList.get(i);        	
    		    		    		
    		for(int j=0;j<trainDocList.size();j++){
    		
    			document dr=(document)trainDocList.get(j);
    			Enumeration<String> test=ds.getWords();		    	
		    	double cosine=0;
		    	while(test.hasMoreElements())
		    	{
		    		String sr=(String)test.nextElement();
		    		if(dr.check(sr))
		    		{
		    			cosine+=(Double)ds.getTf(sr)*(Double)dr.getTf(sr);
		    		}
		    	}
		    	cosine=cosine/(ds.getTfSum()*dr.getTfSum());
		    	distance cs=new distance(dr.getLabel(),cosine);
		    	similarity.add(cs);
    		}
    		Collections.sort(similarity);
    		Hashtable<String, Integer> predict=new Hashtable<String, Integer>();
    		String sp="";
    		            
            for(int k=0;k<5;k++)
            {
                distance sm=(distance)similarity.get(k);
                String label=sm.getLabel();
                if(predict.containsKey(label))
                {
                    Integer count=(Integer)predict.get(label);
                    predict.put(label,new Integer(count+1));
                }
                else
                    predict.put(label,new Integer(1));
            }
            int max=0;
            Enumeration<String> p=predict.keys();
            while(p.hasMoreElements())
            {
                String s=(String)p.nextElement();
            	int m=(Integer)predict.get(s);
            	if(m>max)
            	{
                    max=m;
            		sp=s;
            	}
            }
            if(sp.matches(ds.getLabel()))           
                ct++;            
    		else    		
    			cf++;    		             		
    		similarity.clear();
    	}
    	double temp_accuracy=(double)ct/(ct+cf);  
    	System.out.println(temp_accuracy*100);
        return temp_accuracy;    	
    }        
	        
    public void clear() {    	
    	for(int i=0;i<trainDocList.size();i++) {    		
    		trainDocList.get(i).tClear();;
    	}
    	for(int i=0;i<testDocList.size();i++) {    	
    		testDocList.get(i).tClear();
    	} 
    	trainDocList.clear();
    	testDocList.clear();
    	trainwords.clear();    	
    }        
}
