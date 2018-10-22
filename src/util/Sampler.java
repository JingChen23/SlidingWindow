package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class Sampler {
	public static void main(String[] args) {
		try {
			FileReader reader = new FileReader("D://Jworkspace/4-5_train5.csv");
		    BufferedReader br = new BufferedReader(reader);
		    FileWriter writer = new FileWriter("D://Jworkspace/5_train5.csv");
		    BufferedWriter bw = new BufferedWriter(writer);
		    String str = null;
		    int row = 0;
		    while((str = br.readLine()) != null) {
		    	if (row < 80000) {
		    		row++;
		    		continue;
		    	}
		    	bw.write(str);
	    		bw.newLine();
	    		row++ ;
		    }
		    br.close();
		    bw.close();
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
