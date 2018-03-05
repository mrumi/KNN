package KNN;

public class Main {
   	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ktest kt = new ktest();
		kt.readTopics("topics.data");
		kt.readTrainData("training.data");
		kt.readTestData("test.data");
		kt.calculateDistance();
		kt.calIDF();
		kt.calTf_train(); 
		kt.calTf_test(); 		
    	kt.calTf_IDF();
		kt.clear();
	}
		
}
