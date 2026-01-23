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
public class CountThreadsMain {
    
    public static void main(String a[]){
        CountThread t1 = new CountThread("T1", 0, 99);
        CountThread t2 = new CountThread("T2", 99, 199);
        CountThread t3 = new CountThread("T3", 200, 299);

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Hilo principal interrumpido");
        }

        System.out.println("El conteo de hilos ha terminado.");
    }
    
}
