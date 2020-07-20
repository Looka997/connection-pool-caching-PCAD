import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.*;

public class Main {

    private static class Client implements Runnable {

        private ConnectionPool connectionPool;
        private Vector<Future<Integer>> futures;
        private String URL;

        public Client (ConnectionPool connectionPool,Vector<Future<Integer>> futures, String URL){
            this.connectionPool = connectionPool;
            this.futures = futures;
            this.URL = URL;
        }
        @Override
        public void run() {
            synchronized (futures){
                futures.add(connectionPool.OpenConnection(URL));
            }
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int i;
        ConnectionPool connectionPool = new CachedConnectionPool();
        String urls[] = {"https://unige.it/it/","https://2019.aulaweb.unige.it/my/shouldnotwork","https://unige.it/it/", "https://youtube.com", "https://youtube.com", "http://info.cern.ch/"}; //
        Vector<Future<Integer>> futures = new Vector(urls.length);
        ConcurrentLinkedQueue<Pair<String,Integer>> responseCodes = new ConcurrentLinkedQueue();

        for (String url : urls){
            new Thread(new Client(connectionPool, futures, url)).start();
        }

        for (i=0; i<futures.size(); ++i)
            responseCodes.add( new Pair<String, Integer>(urls[i], futures.elementAt(i).get()));

        System.out.println(responseCodes);
        connectionPool.stopPool();
    }
}
