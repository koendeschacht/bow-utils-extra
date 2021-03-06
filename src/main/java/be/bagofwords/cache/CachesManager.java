/*
 * Created by Koen Deschacht (koendeschacht@gmail.com) 2017-3-17. For license
 * information see the LICENSE file in the root folder of this repository.
 */

package be.bagofwords.cache;

import be.bagofwords.application.status.StatusViewable;
import be.bagofwords.jobs.AsyncJobService;
import be.bagofwords.memory.MemoryGobbler;
import be.bagofwords.memory.MemoryManager;
import be.bagofwords.minidepi.ApplicationContext;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CachesManager implements MemoryGobbler, StatusViewable {

    private final List<WeakReference<ReadCache>> caches;

    public CachesManager(ApplicationContext applicationContext) {
        this.caches = new ArrayList<>();
        applicationContext.getBean(MemoryManager.class).registerMemoryGobbler(this);
        applicationContext.getBean(AsyncJobService.class).schedulePeriodicJob(this::updateCaches, 500);
    }

    private void updateCaches() {
        synchronized (caches) {
            for (WeakReference<ReadCache> reference : caches) {
                ReadCache readCache = reference.get();
                if (readCache != null) {
                    readCache.updateCachedObjects();
                }
            }
        }
    }

    @Override
    public synchronized long freeMemory() {
        synchronized (caches) {
            for (WeakReference<ReadCache> reference : caches) {
                ReadCache readCache = reference.get();
                if (readCache != null) {
                    readCache.moveCachedObjectsToOld();
                }
            }
            return 0; //TODO return actual size of freed memory
        }
    }

    @Override
    public long getMemoryUsage() {
        return sizeOfAllReadCaches();
    }

    private synchronized long sizeOfAllReadCaches() {
        long result = 0;
        synchronized (caches) {
            for (WeakReference<ReadCache> reference : caches) {
                ReadCache readCache = reference.get();
                if (readCache != null) {
                    result += readCache.completeSize();
                }
            }
        }
        return result;
    }


    public synchronized <T> ReadCache<T> createNewCache(String name, Class<? extends T> objectClass) {
        ReadCache<T> newReadCache = new ReadCache<>(name, objectClass);
        synchronized (caches) {
            caches.add(new WeakReference<>(newReadCache));
        }
        return newReadCache;
    }

    @Override
    public synchronized void printHtmlStatus(StringBuilder sb) {
        sb.append("<h1>Caches</h1>");
        List<ReadCache> sortedCaches = new ArrayList<>();
        synchronized (caches) {
            for (WeakReference<ReadCache> reference : caches) {
                ReadCache readCache = reference.get();
                if (readCache != null) {
                    sortedCaches.add(readCache);
                }
            }
        }
        sortedCaches.sort((o1, o2) -> -Double.compare(o1.size(), o2.size()));
        for (ReadCache readCache : sortedCaches) {
            double hitRatio = readCache.getNumberOfHits() == 0 ? 0 : readCache.getNumberOfHits() / (double) readCache.getNumberOfFetches();
            sb.append(readCache.getName()).append(" size=").append(readCache.size()).append(" fetches=").append(readCache.getNumberOfFetches()).append(" hits=").append(readCache.getNumberOfHits()).append(" hitRatio=").append(hitRatio);
            sb.append("<br>");
        }
    }


}

