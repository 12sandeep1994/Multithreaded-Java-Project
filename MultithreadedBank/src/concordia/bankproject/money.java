package concordia.bankproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

public class money {

	private  static LinkedBlockingQueue<String> sharedMemory = new LinkedBlockingQueue<String>();
	private static  Map<String,String> bankdata = new HashMap<String, String>();
	private static  Map<String,String> customerdata = new HashMap<String, String>();
	private static ArrayList<String> bankNames = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		money master = new money();
		System.out.println("** Customers and loan objectives **");
		customerdata = master.readData("customers.txt");
		System.out.println("** Banks and financial resources **");
		bankdata = master.readData("banks.txt");
		master.startBankThreads(bankdata);
		master.startCustomerThreads(customerdata);
	}
	
	public void startBankThreads(Map<String,String> bankdata) {
	
	for(Entry<String, String> entry : bankdata.entrySet()) {
		Bank bank = new  Bank(entry.getKey(), entry.getValue(), sharedMemory,this);
		Thread bankThread = new Thread(bank, entry.getKey());
		bankThread.start();
		bankNames.add(entry.getKey());
	}
	}
	
	public void startCustomerThreads(Map<String,String> customerdata) {
	for(Entry<String, String> entry : customerdata.entrySet()) {
		Customer customer = new  Customer(entry.getKey(), entry.getValue(), sharedMemory,bankNames,this);
		Thread customerThread = new Thread(customer, entry.getKey());
		customerThread.start();
	}
	}
	public  void display(String message) {
		System.out.println(message);
	}
	
	
	public Map<String, String> readData (String fileName) throws Exception
	{
		Map<String,String> data = new HashMap<String, String>();
		      BufferedReader in = new BufferedReader( new InputStreamReader(this.getClass().getResourceAsStream(fileName)));
				String content = "";
				while ((content = in.readLine()) != null) {
					String lineData[] = content.substring(content.indexOf("{") + 1, content.indexOf("}")).split(",");
					data.put(lineData[0],
							lineData[1]);
				}
				in.close();
				
			for (String key : data.keySet()) {
			System.out.println(key + ": " + data.get(key));
			}
			System.out.println();
	return data;	
	}
}
