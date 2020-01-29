package concordia.bankproject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Customer implements Runnable {

	private LinkedBlockingQueue<String> sharedMemory;
	private int totalAmount;
	private int amountRequired;
	private String name;
	private List<String> bankNames = new ArrayList<>();
	money master;
	
	public Customer(String name,String amount, LinkedBlockingQueue<String> sharedMemory, ArrayList<String> bankNames,money master) {
		this.sharedMemory = sharedMemory;
		this.amountRequired = Integer.parseInt(amount);
		totalAmount = Integer.parseInt(amount);
		this.name = name;
		for(int i=0;i<bankNames.size();i++) {
			this.bankNames.add(bankNames.get(i));
		}
		
		this.master = master;
	}
	
	@Override
	public synchronized void  run() {
		
		try {
			String[] response;
			String bankName;
			int amountReceived;
			makeRequest();
			Random random = new Random();
			while(amountRequired>0 && !sharedMemory.isEmpty() && this.bankNames.size()>0) {

				Iterator<String > iterator = sharedMemory.iterator();
				while(iterator.hasNext() && amountRequired>0 ) {
					
					response = iterator.next().split(" ");
					if(response[0].equals(Thread.currentThread().getName())) {
						bankName = response[1];
						amountReceived = Integer.parseInt(response[2]);
						if(amountReceived == 0) {
							synchronized (bankNames) {
								this.bankNames.remove(bankName);
							}
							//master.display(response[1] +" denied a request from " +response[0]);
						}else {
							amountRequired = amountRequired - amountReceived;
							//master.display(response[0]+" received from " +response[1]+" an amount of "+response[2]);
						}
						makeRequest();
						iterator.remove();
					}
				}
			}
			if(amountRequired == 0) {
				master.display(Thread.currentThread().getName()+" has reached the objective of "+totalAmount + " dollar(s). Woo Hoo!");
			}else {
				master.display(Thread.currentThread().getName()+" was only able to borrow "+(totalAmount-amountRequired) + " dollar(s). Boo Hoo!");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	 private void makeRequest() throws InterruptedException {
		if(!(amountRequired==0) && this.bankNames.size()>0) {
			Random random = new Random();
			int requestAmount;
			int bankIndex = random.nextInt(bankNames.size());
			StringBuffer request = new StringBuffer();
			request.append(bankNames.get(bankIndex));
			request.append(" ");
			if(amountRequired>50) {
				requestAmount = random.nextInt(50)+1;
				request.append(Thread.currentThread().getName());
				request.append(" ");
				request.append(requestAmount);
			}else {
				requestAmount = random.nextInt(amountRequired)+1;
				request.append(Thread.currentThread().getName());
				request.append(" ");
				request.append(requestAmount);
			}
			master.display(Thread.currentThread().getName()+" requests a loan of  "+requestAmount+" dollar(s) from "+bankNames.get(bankIndex));
			sharedMemory.add(request.toString());
			Thread.currentThread().sleep(random.nextInt(100)+10);
		}
	}

}
