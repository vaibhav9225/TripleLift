package TripleLift.Server;

import java.util.LinkedList;

public class Solution {
	
	private static final long TIMEOUT = 500;
	
	public static void main(String[] args) {
		retrieveData(new int[]{123, 124, 125, 126});
	}

	private static void retrieveData(int[] IDs) {
		LinkedList<Thread> threads = new LinkedList<Thread>();
		LinkedList<TripleLiftServer> instances = new LinkedList<TripleLiftServer>();
		for(int ID : IDs){
			TripleLiftServer object = new TripleLiftServer(ID);
			instances.add(object);
			Thread instance = new Thread(object);
			threads.add(instance);
		}
		for(Thread thread : threads){
			thread.start();
		}
		try {
			Thread.sleep(TIMEOUT);
			for(int i=0; i<threads.size(); i++){
				if(threads.get(i).isAlive()){
					instances.get(i).exitThread();
				}
				threads.get(i).interrupt();
			}
			System.out.println(TripleLiftServer.output());
		} catch (InterruptedException e) {}
	}
}