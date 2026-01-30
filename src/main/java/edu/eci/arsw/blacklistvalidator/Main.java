/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]){
        // Parte 3 - Evaluacion de desempeño de desempeño
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Nucleos disponibles: " + cores);
        System.out.println("Iniciando pruebas de rendimiento...\n");
        
        String testIP = "202.24.34.55"; // esta IP esta dispersa en las listas
        
        // voy a probar con diferentes cantidades de hilos
        int[] numHilos = {1, cores, cores*2, 50, 100};
        
        System.out.println("Numero de hilos    |    Tiempo (ms)");
        System.out.println("----------------------------------------");
        
        for(int n : numHilos) {
            HostBlackListsValidator validator = new HostBlackListsValidator();
            
            long inicio = System.currentTimeMillis();
            List<Integer> resultado = validator.checkHost(testIP, n);
            long fin = System.currentTimeMillis();
            
            long tiempo = fin - inicio;
            System.out.println(n + "                   |    " + tiempo);
            
            // pausa para poder ver bien en jvisualvm
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("----------------------------------------");
        System.out.println("\nPruebas completadas!");
        
        // pruebas normales del lab
        System.out.println("\n--- Pruebas de validacion ---\n");
        HostBlackListsValidator hblv = new HostBlackListsValidator();

        String ip1 = "202.24.34.55";
        List<Integer> occ1 = hblv.checkHost(ip1);
        System.out.println("IP probada: " + ip1 + " -> Listas encontradas: " + occ1);
        System.out.println("-------------------------");

        String ip2 = "212.24.24.55";
        List<Integer> occ2 = hblv.checkHost(ip2);
        System.out.println("IP probada: " + ip2 + " -> Listas encontradas: " + occ2);
        System.out.println("-------------------------");

        String ip0 = "200.24.34.55";
        List<Integer> occ0 = hblv.checkHost(ip0);
        System.out.println("IP probada: " + ip0 + " -> Listas encontradas: " + occ0);
        System.out.println("-------------------------");
        
    }
    
}
