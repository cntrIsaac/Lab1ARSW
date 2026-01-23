/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author hcadavid
 */
public class CountThread extends Thread {

	private final int a;
	private final int b;

	/**
	 * Crea un CountThread con el rango [a..b].
	 */
	public CountThread(int a, int b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Crea un CountThread con nombre explícito y rango [a..b].
	 */
	public CountThread(String name, int a, int b) {
		super(name);
		this.a = a;
		this.b = b;
	}

	/**
	 * Ciclo de vida del thread: cuando se inicia, imprime números desde a hasta b (incluyendo b).
	 */
	@Override
	public void run() {
		for (int i = a; i <= b; i++) {
			System.out.println(Thread.currentThread().getName() + ": " + i);
			try {
				Thread.sleep(10); 
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				System.out.println(getName() + " interrumpido");
				break;
			}
		}
	}

}
