/*
 * Created by Koen Deschacht (koendeschacht@gmail.com) 2017-3-17. For license
 * information see the LICENSE file in the root folder of this repository.
 */

package be.bagwords.cache;

import be.bagofwords.cache.CachesManager;
import be.bagofwords.cache.ReadCache;
import be.bagofwords.memory.MemoryManager;
import be.bagofwords.minidepi.ApplicationContext;
import be.bagofwords.minidepi.annotations.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Random;

public class TestReadCache {

    @Inject
    private MemoryManager memoryManager;
    @Inject
    private CachesManager cachesManager;

    @Before
    public void setup() {
        ApplicationContext ctx = new ApplicationContext(new HashMap<>());
        ctx.registerBean(this);
    }

    @Test
    public void testRemoval() throws Exception {
        final int numOfValues = 1000;
        Random random = new Random();
        char[] randomString = createRandomString(random);
        ReadCache<String> firstReadCache = cachesManager.createNewCache("cache1", String.class);
        for (int i = 0; i < numOfValues; i++) {
            firstReadCache.put(random.nextLong(), new String(randomString));
        }
        firstReadCache.updateCachedObjects();
        Assert.assertTrue(firstReadCache.size() > 0);
        //Eventually all values should be removed
        ReadCache<String> otherReadCache = cachesManager.createNewCache("cache2", String.class);
        long maxTimeToTry = 5 * 60 * 1000; //usually this should take less then 5 minutes
        long start = System.currentTimeMillis();
        while (start + maxTimeToTry >= System.currentTimeMillis() && firstReadCache.size() > 0) {
            memoryManager.waitForSufficientMemory();
            otherReadCache.put(random.nextLong(), new String(randomString));
        }
        Assert.assertEquals(0, firstReadCache.size());
        cachesManager.createNewCache("unused_cache", Long.class); //just to make sure that the caches manager is not garbage collected before the end of the test
    }

    private char[] createRandomString(Random random) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextLong());
        }
        return sb.toString().toCharArray();
    }

}
