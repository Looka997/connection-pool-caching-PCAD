import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Objects.requireNonNull;

public class ConnectionPool {
    ExecutorService threadPool;

    public ConnectionPool(){
        threadPool = Executors.newCachedThreadPool();
    }

    Future<Integer> OpenConnection(String URL) {
        return threadPool.submit(new Callable<Integer>(){
            @Override
            public Integer call() throws IOException {
                URL url = new URL(requireNonNull(URL));
                String protocol;
                protocol = url.getProtocol();
                URLConnection connect;
                if (!(protocol.equals("http") || protocol.equals("https")))
                    throw new IOException("connection protocol " + protocol + " not supported");
                connect = url.openConnection();
                connect.connect();
                return ((HttpURLConnection) connect).getResponseCode();
            }
        });
    }

    synchronized public void stopPool(){
        threadPool.shutdown();
    }
}
