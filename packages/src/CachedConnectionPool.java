import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.requireNonNull;

public class CachedConnectionPool extends ConnectionPool {
    private ConcurrentHashMap<URL, URLConnection> cachedConnections;
    private HashSet<URL> askedFor;
    private final Lock askedForlock = new ReentrantLock();
    private final Condition isBeingAsked = askedForlock.newCondition();

    public CachedConnectionPool(){
        super();
        cachedConnections = new ConcurrentHashMap<>();
        askedFor = new HashSet<>();
    }

    @Override
    Future<Integer> OpenConnection(String URL) {
        return threadPool.submit(new Callable<Integer>(){

            @Override
            public Integer call() throws IOException, InterruptedException {
                URL url = new URL(requireNonNull(URL));
                askedForlock.lock();
                if (!askedFor.isEmpty())            // if somebody is processing a connection
                    while (askedFor.contains(url)) {    // is this url being processed?
                        isBeingAsked.await();   // wait until it's not; then check again.
                    }
                askedFor.add(url);  // new url starts being processed; whether it was previously cached or not
                askedForlock.unlock();

                synchronized (cachedConnections){   // even though cachedConnections is concurrent, we have to make multiple checks
                    if (cachedConnections.containsKey(url)) {     // if url is cached
                        System.out.println("cache HIT for " + url);
                        askedForlock.lock();
                        askedFor.remove(url);   // url no longer being processed
                        isBeingAsked.signal();  // signaling to other threads who may be waiting on the same url
                        askedForlock.unlock();
                        return ((HttpURLConnection) cachedConnections.get(url)).getResponseCode();    // return cached response code
                    }
                }

                String protocol;
                protocol = url.getProtocol();
                URLConnection connect;

                if (!(protocol.equals("http") || protocol.equals("https")))
                    throw new IOException("connection protocol " + protocol + " not supported");
                connect = url.openConnection();

                connect.connect();
                cachedConnections.putIfAbsent(url, connect);    // new connection now cached
                askedForlock.lock();
                askedFor.remove(url);
                isBeingAsked.signal();
                askedForlock.unlock();
                return ((HttpURLConnection) connect).getResponseCode();
            }
        });
    }
}
