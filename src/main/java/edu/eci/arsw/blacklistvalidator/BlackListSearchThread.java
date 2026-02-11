package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hilo de trabajo que busca en un segmento de servidores de listas negras
 * para un host dado.
 * Proporciona un método para consultar cuántas ocurrencias locales encontró
 * este hilo.
 */
public class BlackListSearchThread extends Thread {

    private final int startIndex;
    private final int endIndex;
    private final String host;
    private final AtomicInteger globalOccurrences;
    private final ConcurrentLinkedQueue<Integer> foundServers;
    private final AtomicInteger checkedListsCount;
    private final int alarmThreshold;

    private int localOccurrencesFound = 0;

    public BlackListSearchThread(int startIndex, int endIndex, String host,
            AtomicInteger globalOccurrences,
            ConcurrentLinkedQueue<Integer> foundServers,
            AtomicInteger checkedListsCount,
            int alarmThreshold) {
        super("BLSearch-" + startIndex + "-" + endIndex);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.host = host;
        this.globalOccurrences = globalOccurrences;
        this.foundServers = foundServers;
        this.checkedListsCount = checkedListsCount;
        this.alarmThreshold = alarmThreshold;
    }

    @Override
    public void run() {
        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();

        for (int i = startIndex; i <= endIndex; i++) {
            if (globalOccurrences.get() >= alarmThreshold) {
                break;
            }
            boolean in = skds.isInBlackListServer(i, host);
            checkedListsCount.incrementAndGet();

            if (in) {
                foundServers.add(i);
                localOccurrencesFound++;
                int occ = globalOccurrences.incrementAndGet();
                if (occ >= alarmThreshold) {
                    break;
                }
            }
        }
    }

    /**
     * Devuelve cuántas ocurrencias de la lista negra encontró este hilo
     * en su segmento.
     */
    public int getLocalOccurrencesFound() {
        return localOccurrencesFound;
    }

}
