package concordia.bankproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

public class Bank implements Runnable {
	
	private LinkedBlockingQueue<String> sharedMemory;
	private int totalBalance;
	private String name;
	private money master;
	
	public Bank(String name,String balance, LinkedBlockingQueue<String> sharedMemory, money master) {
		this.sharedMemory = sharedMemory;
		this.totalBalance = Integer.parseInt(balance);
		this.name = name;
		this.master = master;
	}

	@Override
	public void run() {
		try {
			String customerName;
			int amountRequested;
			String[] request;
			String receivedData;
			ArrayList<String> responseArray = new ArrayList<>();
			Thread.currentThread().sleep(100);
			while(!sharedMemory.isEmpty()) {
				Iterator<String > iterator = sharedMemory.iterator();
				while(iterator.hasNext()) {
					receivedData = iterator.next();
					request =receivedData.split(" ");
					if(request[0].equals(Thread.currentThread().getName())) {
						customerName = request[1];
						amountRequested = Integer.parseInt(request[2]);
						StringBuffer response = new StringBuffer();
						response.append(customerName);
						response.append(" ");
						int amountSent;
						if(amountRequested<=totalBalance) {
							amountSent = amountRequested;
							totalBalance = totalBalance - amountRequested;
							response.append(Thread.currentThread().getName());
							response.append(" ");
							response.append(amountSent);
							master.display(Thread.currentThread().getName()+"  approves a loan of  "+amountSent+" dollar(s) from "+customerName);
						}else {
							amountSent = 0;
							response.append(Thread.currentThread().getName());
							response.append(" ");
							response.append(amountSent);
							master.display(Thread.currentThread().getName()+"  denies a loan of  "+amountRequested+" dollar(s) from "+customerName);
						}
						sharedMemory.add(response.toString());
						iterator.remove();
					}
				}
			}
			Thread.currentThread().sleep(500);
			master.display(Thread.currentThread().getName()+" has "+totalBalance+" dollar(s) remaining");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
