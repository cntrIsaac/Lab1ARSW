/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    private static final int DEFAULT_NUM_THREADS = Runtime.getRuntime().availableProcessors();
    
    /**
     * Versión por compatibilidad: delega a la nueva sobrecarga usando
     * el número de hilos por defecto.
     * @param ipaddress dirección IP sospechosa del host.
     * @return números de las listas negras donde se encontró la dirección IP.
     */
    public List<Integer> checkHost(String ipaddress){
        return checkHost(ipaddress, DEFAULT_NUM_THREADS);
    }

    /**
     * Comprueba la dirección IP del host en paralelo usando hasta N hilos.
     * Divide el conjunto de servidores en N segmentos lo más equitativamente posible
     * y lanza un hilo `BlackListSearchThread` por segmento. Si N es mayor que
     * el número de servidores registrados, se limitará a ese número.
     * La búsqueda termina tempranamente cuando el número total de ocurrencias
     * alcanza `BLACK_LIST_ALARM_COUNT`.
     * @param ipaddress dirección IP sospechosa del host.
     * @param N número máximo de hilos a usar (debe ser >= 1).
     * @return números de las listas negras donde se encontró la dirección IP.
     * @throws IllegalArgumentException si N < 1
     */
    public List<Integer> checkHost(String ipaddress, int N){

        if (N < 1) {
            throw new IllegalArgumentException("N debe ser al menos 1");
        }

        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();

        int registeredServers = skds.getRegisteredServersCount();

        // Si hay menos servidores que hilos solicitados, limitar N
        int numThreads = Math.min(N, Math.max(1, registeredServers));

        ConcurrentLinkedQueue<Integer> foundServers = new ConcurrentLinkedQueue<>();
        AtomicInteger occurrences = new AtomicInteger(0);
        AtomicInteger checkedListsCount = new AtomicInteger(0);
        AtomicBoolean stopFlag = new AtomicBoolean(false);

        Thread[] workers = new Thread[numThreads];

        int chunk = registeredServers / numThreads;
        int remainder = registeredServers % numThreads;
        int start = 0;

        for (int t = 0; t < numThreads; t++){
            int extra = (t < remainder) ? 1 : 0;
            int end = start + chunk + extra - 1;
            if (end >= registeredServers) {
                end = registeredServers - 1;
            }
            if (start <= end){
                workers[t] = new BlackListSearchThread(start, end, ipaddress, occurrences, foundServers, stopFlag, checkedListsCount);
                workers[t].start();
            } else {
                workers[t] = null;
            }
            start = end + 1;
        }

        for (Thread wt : workers){
            if (wt != null){
                try{
                    wt.join();
                } catch (InterruptedException ex){
                    Thread.currentThread().interrupt();
                    LOG.log(Level.WARNING, "Main thread interrupted while waiting for workers", ex);
                }
            }
        }

        if (occurrences.get()>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount.get(), registeredServers});

        return new LinkedList<>(foundServers);
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
