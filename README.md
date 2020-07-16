# connection-pool-caching-PCAD
Labo 4: Connection Pool/Caching

ConnectionPool
Create una classe Java di nome ConnectionPool per gestire connessioni HTTP attraverso un thread pool.
ConnectionPool deve  fornire i seguenti metodi:

Future<Integer> OpenConnection(String URL)
Crea un callable per effettuare la connessione all'indirizzo URI (metodo GET) restituendo il codice di risposta
Per costruire una connessione usare la classe URLconnection ed i metodi openConnection e getResponseCode
Sottomette il callable al pool.
Il metodo deve sollevare eccezioni in caso di errori di connessione e restituire attraverso un Future il codice di ritorno della richiesta  di connessione

void StopPool
Esegue shutdown del pool

Implementare quindi un programma Java multithreaded che usa un'istanza dl ConnectionPool per effettuare attraverso dei thread richieste  di connessione
ad indirizzi URL (scegliere indirizzi accessibili dal laboratorio come aulaweb vedi anche http://www.simplesoft.it/connessioni-http-in-java.html per gestire Proxy).

Estensione con Caching Locale
Estendere quindi la classe in maniera tale da implementare un meccanismo di caching locale delle connessioni per evitare di effettuare nuovamente connessioni già effettuate. 
Per assicurare thread-safeness usate  una struttura dati concorrenti es ConcurrentHashMap per associare connessioni ad URI.

ConcurrentHashmap: https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ConcurrentHashMap.html

Fare attenzione alla sincronizzazione tra le richieste multiple quando si legge/scrive nella cache in particolare per l'inserimento di nuove coppie.
In particolare ricordate che contains e put singolarmente sono operazioni atomiche ma le due operazioni in cascata non rappresentano un blocco atomico.
Usate invece putIfAbsent.