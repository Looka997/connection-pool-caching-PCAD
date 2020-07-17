import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int i;
        ConnectionPool connectionPool = new ConnectionPool();
        String urls[] = {"https://unige.it/it/","https://2019.aulaweb.unige.it/my/aaaa"};
        Vector<Future<Integer>> futures = new Vector(urls.length);
        HashMap<String, Integer> responseCodes = new HashMap<String, Integer>();
        for (i=0; i<urls.length; ++i)
            futures.add(connectionPool.OpenConnection(urls[i]));
        for (i=0; i<urls.length; ++i)
            responseCodes.put(urls[i], futures.elementAt(i).get());
        for (i=0; i<futures.capacity(); ++i)
            System.out.println(urls[i] + " responded with " + responseCodes.get(urls[i]));
    }
}
