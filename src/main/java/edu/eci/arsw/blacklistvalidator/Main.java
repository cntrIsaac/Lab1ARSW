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
        HostBlackListsValidator hblv = new HostBlackListsValidator();

        // Prueba 1: IP que suele aparecer dispersa en varias listas
        String ip1 = "202.24.34.55";
        List<Integer> occ1 = hblv.checkHost(ip1);
        System.out.println("IP probada: " + ip1 + " -> Listas encontradas: " + occ1);
        System.out.println("-------------------------");

        // Prueba 2: IP que no debería aparecer en ninguna lista
        String ip2 = "212.24.24.55";
        List<Integer> occ2 = hblv.checkHost(ip2);
        System.out.println("IP probada: " + ip2 + " -> Listas encontradas: " + occ2);
        System.out.println("-------------------------");

        // (Opcional) prueba original para comparar
        String ip0 = "200.24.34.55";
        List<Integer> occ0 = hblv.checkHost(ip0);
        System.out.println("IP probada: " + ip0 + " -> Listas encontradas: " + occ0);
        System.out.println("-------------------------");
        
    }
    
}
