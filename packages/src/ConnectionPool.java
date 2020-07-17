import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

    Future<Integer> OpenConnection(String URL){
        return threadPool.submit(new Callable<Integer>(){
            @Override
            public Integer call() throws MalformedURLException, IOException {
                URL url = new URL(requireNonNull(URL));
                HttpsURLConnection connect;
                String protocol;
                protocol = url.getProtocol();

                if (!(protocol.equals("http") || protocol.equals("https")))
                    throw new IOException("connection protocol " + protocol + " not supported");
                connect = (HttpsURLConnection) url.openConnection();
                connect.connect();
                return connect.getResponseCode();
            }
        });
    }

    public void stopPool(){
        threadPool.shutdown();
    }
}
