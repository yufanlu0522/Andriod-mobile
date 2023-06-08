package ds;

/*
 * @author Yufan Lu, Yuting Long
 * AndrewID: yufanlu, yutinglo
 * 
 * This file is the Model component of the MVC, and it models the business
 * logic for the web application.  In this case, the business logic involves
 * making a request to Currencyscoop and return the conversion rate of two currencies.
 */

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.google.gson.Gson;

/**
 * This is the model for the application
 */
public class CurrencyscoopModel {

    // Call doGet with the from&to currency code and amount
    public String getCurrencyList(String from, String to, String amount) {
        Result r = doGet(from, to, amount);
        // Create a Gson object
        Gson gson = new Gson();
        // Serialize to JSON
        ResponseMessage responseMessage = gson.fromJson(r.getResponseText(), ResponseMessage.class);
        String responseMessageString = gson.toJson(responseMessage);
        return responseMessageString;
    }

    // Call get log function to get all data from the database
    public List<String> getLogList() {

        // Call get log function to get all data from the database
        FindIterable<Document> res = getLog();
        ArrayList<String> list = new ArrayList<String>();

        // create variable to store the data
        String all_entries = "";
        HashMap<String, Integer> map_from = new HashMap<>();
        HashMap<String, Integer> map_to = new HashMap<>();
        int total_latency = 0;

        Iterator it = res.iterator();
        System.out.println("All documents that currently stored in the database: ");

        // first get all the request/reply log, form&to country counts, average latency in the database
        int entry_count = 0;
        while (it.hasNext()) {
            entry_count += 1;

            Document cur = (Document)it.next();
            StringBuilder sb = new StringBuilder();
            sb.append(new Date(Long.parseLong(cur.get("request_timestamp").toString())));
            sb.append(",");
            sb.append(new Date(Long.parseLong(cur.get("response_timestamp").toString())));
            sb.append(",");
            sb.append(cur.get("currency_from"));
            sb.append(",");
            sb.append(cur.get("currency_to"));
            sb.append(",");
            sb.append(cur.get("currency_amount"));
            sb.append(",");
            sb.append(cur.get("status_code"));
            sb.append(",");
            sb.append(cur.get("reply_info"));
            sb.append("\n");

            all_entries += sb.toString();

            int count = map_from.getOrDefault(cur.get("currency_from").toString(), 0);
            count += 1;
            map_from.put(cur.get("currency_from").toString(), count);

            count = map_to.getOrDefault(cur.get("currency_to").toString(), 0);
            count += 1;
            map_to.put(cur.get("currency_to").toString(), count);

            total_latency += Long.parseLong(cur.get("response_timestamp").toString()) - Long.parseLong(cur.get("request_timestamp").toString());
        }

        // sorting the countries by their counts
        Map<String, Integer> map_from_sorted = sortByValue(map_from);
        Map<String, Integer> map_to_sorted = sortByValue(map_to);

        // getting the top 5 countries and store them in a string
        String top_5_from_countries = "";
        String top_5_to_countries = "";

        int count = 0;
        for (Map.Entry<String, Integer> en : map_from_sorted.entrySet()) {
            if(count == 5){
                break;
            }
            top_5_from_countries += en.getKey();
            top_5_from_countries += ",";
            top_5_from_countries += en.getValue();
            top_5_from_countries += ",";
            count += 1;
        }

        count = 0;
        for (Map.Entry<String, Integer> en : map_to_sorted.entrySet()) {
            if(count == 5){
                break;
            }
            top_5_to_countries += en.getKey();
            top_5_to_countries += ",";
            top_5_to_countries += en.getValue();
            top_5_to_countries += ",";
            count += 1;
        }


        // store all information in a list and return
        list.add(all_entries);
        list.add(top_5_from_countries);
        list.add(top_5_to_countries);
        double ave_latency = total_latency * 1.0 / entry_count;
        list.add(ave_latency+"");

        return list;
    }

    //Source: https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/
    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    // Make an HTTP GET request
    private Result doGet(String from, String to, String amount) {

        HttpURLConnection conn;
        int status = 0;
        Result result = new Result();
        try {
            String API_KEY = "b83a9e71e00630d1d29bfdb3b9099ce4";
            // GET wants us to pass the name on the URL line
            URL url = new URL("https://api.currencyscoop.com/v1/convert?from=" + from + "&to=" + to + "&amount=" + amount + "&api_key=" + API_KEY);
            String req_t = System.currentTimeMillis()+"";

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // we are sending plain text
            conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "text/plain");

            // wait for response
            status = conn.getResponseCode();

            // set http response code
            result.setResponseCode(status);
            // set http response message - this is just a status message
            // and not the body returned by GET
            String response = conn.getResponseMessage();
            result.setResponseText(response);

            if (status == 200) {
                String responseBody = getResponseBody(conn);
                result.setResponseText(responseBody);
                Gson gson = new Gson();
                ResponseMessage responseMessage = gson.fromJson(responseBody, ResponseMessage.class);
                System.out.println("responseMessage");
                System.out.println(responseMessage.response.value);
                makeLog(req_t, System.currentTimeMillis()+"", from, to, amount, status+"", responseMessage.response.value+"");
            }else{
                makeLog(req_t, System.currentTimeMillis()+"", from, to, amount, status+"", "");
            }
            conn.disconnect();

        }
        // handle exceptions
        catch (MalformedURLException e) {
            System.out.println("URL Exception thrown" + e);
        } catch (IOException e) {
            System.out.println("IO Exception thrown" + e);
        } catch (Exception e) {
            System.out.println("IO Exception thrown" + e);
        }
        return result;
    }


    /**
     * this method handle logging entries into the Mongodb
     */
    private void makeLog(String a0, String a, String b, String c, String d, String e, String f) {
        //Source: https://docs.mongodb.com/drivers/java/sync/v4.3/quick-start/
        ConnectionString connectionString = new ConnectionString("mongodb://yufanlu1999:brobdingnagian@ac-qkzl6uw-shard-00-00.xapckgu.mongodb.net:27017,ac-qkzl6uw-shard-00-01.xapckgu.mongodb.net:27017,ac-qkzl6uw-shard-00-02.xapckgu.mongodb.net:27017/test?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("myDatabase");

        //Source: https://www.tutorialspoint.com/how-to-insert-a-document-into-a-mongodb-collection-using-java
        //Creating a collection
        String testCollectionName = "CurrencyscoopLogs";
        boolean collectionExists = database.listCollectionNames()
                .into(new ArrayList()).contains(testCollectionName);
        if(!collectionExists){
            database.createCollection("CurrencyscoopLogs");
        }

        //Preparing a document
        Document document = new Document();
        document.append("request_timestamp", a0);
        document.append("response_timestamp", a);
        document.append("currency_from", b);
        document.append("currency_to", c);
        document.append("currency_amount", d);
        document.append("status_code", e);
        document.append("reply_info", f);
        //Inserting the document into the collection
        database.getCollection("CurrencyscoopLogs").insertOne(document);
    }


    private FindIterable<Document> getLog(){
        ConnectionString connectionString = new ConnectionString("mongodb://yufanlu1999:brobdingnagian@ac-qkzl6uw-shard-00-00.xapckgu.mongodb.net:27017,ac-qkzl6uw-shard-00-01.xapckgu.mongodb.net:27017,ac-qkzl6uw-shard-00-02.xapckgu.mongodb.net:27017/test?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("myDatabase");
        //Print all the documents
        //Creating a collection object
        //Source: https://www.tutorialspoint.com/how-to-retrieve-all-the-documents-from-a-mongodb-collection-using-java
        MongoCollection<Document> collection = database.getCollection("CurrencyscoopLogs");
        //Retrieving the documents
        FindIterable<Document> iterDoc = collection.find();

        return iterDoc;
    }

    // Gather up a response body from the connection
    // and close the connection.
    private String getResponseBody(HttpURLConnection conn) {
        String responseText = "";
        try {
            String output = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                responseText += output;
            }
            conn.disconnect();
        } catch (IOException e) {
            System.out.println("Exception caught " + e);
        }
        return responseText;
    }
}

// A simple class to wrap an RPC result.
class Result {
    private int responseCode;
    private String responseText;

    public int getResponseCode() { return responseCode; }
    public void setResponseCode(int code) { responseCode = code; }
    public String getResponseText() { return responseText; }
    public void setResponseText(String msg) { responseText = msg; }

    public String toString() { return responseCode + ":" + responseText; }
}
