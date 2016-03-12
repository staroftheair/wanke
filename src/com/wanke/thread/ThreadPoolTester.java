package com.wanke.thread;

public class ThreadPoolTester {

	public static void main(String[] args) {

		int numTasks = 200; // 任务数量
		int poolSize = 1; // 线程池里线程的数量

		ThreadPool threadPool = new ThreadPool(poolSize); // 创建线程池

		/*
		 * 运行任务
		 */
		for (int i = 0; i < numTasks; i++) {
			threadPool.execute(createTask(i));
		}

		threadPool.join(); // 等待工作线程完成所有的任务
		//
		threadPool.close();
	}

	/*
	 * 定义了一个简单的任务(打印ID)
	 */
	private static Runnable createTask(final int taskID) {
		return new Runnable() {
			public void run() {
				System.out.println("Task:" + taskID + ":start");
				try {
					Thread.sleep(1000); // 增加执行一个任务的时间
				} catch (InterruptedException e) {
					System.out.println("Task " + taskID + ":end");
				}
			}
		};
	}
}