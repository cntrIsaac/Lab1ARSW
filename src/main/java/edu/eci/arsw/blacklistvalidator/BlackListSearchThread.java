package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final AtomicBoolean stopFlag;
    private final AtomicInteger checkedListsCount;

    private int localOccurrencesFound = 0;

    // Mantener el mismo umbral que el validador original (no modificar la fachada)
    private static final int BLACK_LIST_ALARM_COUNT = 5;

    public BlackListSearchThread(int startIndex, int endIndex, String host,
            AtomicInteger globalOccurrences,
            ConcurrentLinkedQueue<Integer> foundServers,
            AtomicBoolean stopFlag,
            AtomicInteger checkedListsCount) {
        super("BLSearch-" + startIndex + "-" + endIndex);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.host = host;
        this.globalOccurrences = globalOccurrences;
        this.foundServers = foundServers;
        this.stopFlag = stopFlag;
        this.checkedListsCount = checkedListsCount;
    }

    @Override
    public void run() {
        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();

        for (int i = startIndex; i <= endIndex && !stopFlag.get(); i++) {
            // Terminación cooperativa anticipada
            if (globalOccurrences.get() >= BLACK_LIST_ALARM_COUNT) {
                stopFlag.set(true);
                break;
            }

            boolean in = skds.isInBlackListServer(i, host);
            checkedListsCount.incrementAndGet();

            if (in) {
                foundServers.add(i);
                localOccurrencesFound++;
                int occ = globalOccurrences.incrementAndGet();
                if (occ >= BLACK_LIST_ALARM_COUNT) {
                    stopFlag.set(true);
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
