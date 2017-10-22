package test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.util.*;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class knn {

    ArrayList<String> topicList = new ArrayList<String>();
	ArrayList<Document> trainDocList = new ArrayList<Document>();
	ArrayList<Document> testDocList = new ArrayList<Document>();
	ArrayList<Distance> hdistance=new ArrayList<Distance>();
	ArrayList<Distance> edistance=new ArrayList<Distance>();
	ArrayList<Distance> similarity=new ArrayList<Distance>();
	Hashtable trainwords=new Hashtable();
	String noiseList[]={"a","an","and","the","in","on","or","am","is",
        "are","for","but","it","this","has","have","will","shall","be",
        "been","would","should","that","to","of","off","at","however",
        "was","were","can","could","from","if","at","with","do","does",
        "did","done","who","where","how","what","when","which","then","there",
        "about","into","by","its",".",",","-",";","\"","(",")","\\",
        "out","up","with","through","about","above","below","before","after","reuter","reuters"};
   	ArrayList<String>noiseArrList=new ArrayList<String>(Arrays.asList(noiseList));
   	double accuracy=0;
    
    public static void main(String[] args) {
        knn ob = new knn();
    	ob.readTopics();
    	ob.readTrainData();
    	ob.readTestData();
    	ob.calIDF();
    	ob.calTf();    	
    	ob.testAccuracy();
        ob.clear();
    }

    public void testAccuracy()
    {
    	String write="";
    	int num=1;
    	for(int i=0;i<num;i++)        
    	{
    		double temp=calTf_IDF();
            System.out.println(temp+ " "+i);
            write+=temp+"\r\n";
            //accuracy+=temp;
    	}
		//accuracy=(accuracy*100)/num;
    	//System.out.println(accuracy);
    	writeResult(write);
    }

    public double calTf_IDF()
    {    	
    	int ct=0,cf=0;
    	for(int i=0;i<testDocList.size();i++)    	
    	{
    		Document ds=(Document)testDocList.get(i);    	
    		int sz=ds.getTab().size();
    		Enumeration es=ds.getTab().keys();
    		while(es.hasMoreElements())
    		{
    			String s=(String)es.nextElement();
    			int nw=(Integer)ds.getTab().get(s);
    			double tf=(double)nw/sz;
    			double idf=0;
    			if(trainwords.containsKey(s))
    				idf=(Double)trainwords.get(s);
    			double tf_idf=tf*idf;
    			ds.setTfSum(ds.getTfSum()+Math.pow(tf_idf,2));
    			ds.getWords().put(s,tf_idf);    			
    		}
    		ds.getTab().clear();
    		ds.setTfSum(Math.sqrt(ds.getTfSum()));
    		for(int j=0;j<trainDocList.size();j++)
    		{
    			Document dr=(Document)trainDocList.get(j);
    			Enumeration test=ds.getWords().keys();		    	
		    	double cosine=0;
		    	while(test.hasMoreElements())
		    	{
		    		String sr=(String)test.nextElement();
		    		if(dr.getWords().containsKey(sr))
		    		{
		    			cosine+=(Double)ds.getWords().get(sr)*(Double)dr.getWords().get(sr);
		    		}
		    	}
		    	cosine=cosine/(ds.getTfSum()*dr.getTfSum());
		    	Distance cs=new Distance(dr.getLabel(),cosine);
		    	similarity.add(cs);
    		}
    		Collections.sort(similarity);
    		Hashtable predict=new Hashtable();
    		String sp="";
    		predict.clear();            
            for(int k=0;k<5;k++)
            {
                Distance sm=(Distance)similarity.get(k);
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
            Enumeration p=predict.keys();
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
    	//System.out.println("TF_IDF accuracy "+temp_accuracy);
        return temp_accuracy;    	
    }
    public void calTf()
    {
    	for(int i=0;i<trainDocList.size();i++)
    	{
    		Document d=(Document)trainDocList.get(i);
    		int sz=d.getTab().size();
    		Enumeration e=d.getTab().keys();    		
    		while(e.hasMoreElements())
    		{
    			String s=(String)e.nextElement();
    			int nw=(Integer)d.getTab().get(s);
    			double tf=(double)nw/sz;
    			double idf=(Double)trainwords.get(s);
    			double tf_idf=tf*idf;
    			d.getWords().put(s,tf_idf);
    			d.setTfSum(d.getTfSum()+Math.pow(tf_idf,2));    			
    		}
    		d.getTab().clear();
    		d.setTfSum(Math.sqrt(d.getTfSum()));
    	}
    	System.out.println("tf done");
    }

    private void calIDF()
    {
    	int docnum=trainDocList.size();///D
        Enumeration e=trainwords.keys();        
        while(e.hasMoreElements())
        {
        	String str=(String)e.nextElement();
            int count=0;
            for(int k=0;k<trainDocList.size();k++)
            {
            	Document d=(Document)trainDocList.get(k);
            	if(d.getTab().containsKey(str))
            		count++;
            }
            double d_cw=(double)docnum/count;//D/C(w)
            double idf= Math.log(d_cw);
            trainwords.put(str,(Double)idf);
        }
        System.out.println("idf done");
    }

	private String removeNoise(String str)
    {
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
        String expression="^[-+]?[0-9]*\\.?[0-9]+$";
        Pattern pattern=Pattern.compile(expression);
        Matcher matcher=pattern.matcher(str);
        if(matcher.matches())
            return null;
        return str;
    }

    private void readTestData()
    {
    	//int c2=0;
    	Document doc=new Document();    	
    	try{
    		BufferedReader bin=new BufferedReader(new FileReader(new File("test.data")));
        	String input="";
        	while(true)
        	{
            	try
            	{
                	input=bin.readLine();
            	}
            	catch(Exception ex)
            	{
                	System.out.println("Exception in readline: "+ex);
            	}
            	if(input==null)
                	break;
            	if(input.equals(""))
                	continue;
            	StringTokenizer st=new StringTokenizer(input);
            	int x=st.countTokens();
            	if(x==1) // found a label
            	{
                	String str=st.nextToken();
                	str=removeNoise(str);
                	if(str==null)
                    	continue;
                	if(topicList.contains(str))
                	{
                		//c2++;
                		doc=new Document(str);
                		testDocList.add(doc);
                	}
            	}
	            while(st.hasMoreTokens())
	            {
	                String str=st.nextToken();
	                str=removeNoise(str);
	                if(str==null)
	                    continue;
	                if(!doc.getTab().containsKey(str))
	                	doc.getTab().put(str,new Integer(1));
	                else
	                {
	                	Integer count=(Integer)doc.getTab().get(str);	                	
	                	doc.getTab().put(str,new Integer(count+1));
	                }
	            }
        	}
	        try
	        {
	            bin.close();
	        }
	        catch(Exception ex)
	        {
	            System.out.println("Exception: "+ex);
	        }
    	}
    	catch(Exception rx)
    	{
    		System.out.println("Exception: "+rx);
    	}
    	System.out.println("doc number "+testDocList.size());    	
        noiseArrList.clear();
        topicList.clear();
    }

    private void readTrainData()
    {
    	//int c1=0;
    	Document doc=new Document();    	
    	try{
    		BufferedReader bin=new BufferedReader(new FileReader(new File("training.data")));
        	String input="";
        	while(true)
        	{
            	try
            	{
                	input=bin.readLine();
            	}
            	catch(Exception ex)
            	{
                	System.out.println("Exception in readline: "+ex);
            	}
            	if(input==null)
                	break;
            	if(input.equals(""))
                	continue;
            	StringTokenizer st=new StringTokenizer(input);
            	int x=st.countTokens();
            	if(x==1) // found a label
            	{
                	String str=st.nextToken();
                	str=removeNoise(str);
                	if(str==null)
                    	continue;
                	if(topicList.contains(str))
                	{
                		//c1++;
                		doc=new Document(str);
                		trainDocList.add(doc);
                	}
            	}
	            while(st.hasMoreTokens())
	            {
	                String str=st.nextToken();
	                str=removeNoise(str);
	                if(str==null)
	                    continue;
                    if(!doc.getTab().containsKey(str))
                    {
                    	doc.getTab().put(str,new Integer(1));
                    }
                    else
                    {
                    	Integer count=(Integer)doc.getTab().get(str);
                    	doc.getTab().put(str,new Integer(count+1));
                    }
	                if(!trainwords.containsKey(str))
	                	trainwords.put(str, new Integer(1));
	            }
        	}
	        try
	        {
	            bin.close();
	        }
	        catch(Exception ex)
	        {
	            System.out.println("Exception: "+ex);
	        }
    	}
    	catch(Exception rx)
    	{
    		System.out.println("Exception: "+rx);
    	}
    	System.out.println("doc number "+trainDocList.size());
    	System.out.println(trainwords.size());    	
    }

    private void readTopics()
    {    	
    	try{
    		BufferedReader bin=new BufferedReader(new FileReader(new File("topics.data")));
        	String input="";
        	while(true)
        	{
            	try
            	{
                	input=bin.readLine();                	
            	}
            	catch(Exception ex)
            	{
                	System.out.println("Exception in readline: "+ex);
            	}
            	if(input==null)
                	break;
            	if(input.equals(""))
                	continue;            	
            	StringTokenizer st = new StringTokenizer(input);
            	String str = st.nextToken();
            	str=removeNoise(str);
            	if(str==null)
                    continue;
            	topicList.add(str);
        	}
	        System.out.println("Topic: "+topicList.size());
	        try
	        {
	            bin.close();
	        }
	        catch(Exception ex)
	        {
	            System.out.println("Exception : "+ex);
	        }
        }
    	catch(Exception rx)
    	{
    		System.out.println("Exception : "+rx);
    	}
    }

    public void writeResult(String s)
    {
        char buffer[]=new char[s.length()];
        s.getChars(0, s.length(), buffer, 0);
        FileWriter fw = null;
        try
        {
            fw = new FileWriter("knn.data",false);
        }
        catch (IOException ex)
        {
            System.out.println("Exception in write file: "+ex);
        }
        try
        {
            fw.write(buffer);
        }
        catch (IOException ex)
        {
            System.out.println("Exception in write file: "+ex);
        }
        try
        {
            fw.close();
        }
        catch (IOException ex)
        {
            System.out.println("Exception: "+ex);
        }
    }

    public void clear()
    {    	
    	for(int i=0;i<trainDocList.size();i++)
    	{    		
    		trainDocList.get(i).getTab().clear();
    	}
    	for(int i=0;i<testDocList.size();i++)
    	{    	
    		testDocList.get(i).getWords().clear();
    	} 
    	trainDocList.clear();
    	testDocList.clear();
    	trainwords.clear();    	
    }    
}
