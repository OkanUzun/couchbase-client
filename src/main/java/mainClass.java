import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;

public class mainClass {
    public static void main(String[] args) {
        // Initialize the Connection
        final Cluster cluster = CouchbaseCluster.create("localhost");
        cluster.authenticate("okan", "Okan2019!");
        final Bucket bucket = cluster.openBucket("okan-bucket");

        // Create a JSON Document
        final JsonObject arthur = JsonObject.create()
                .put("name", "Okan")
                .put("email", "okan.uzun@outlook.com")
                .put("interests", JsonArray.from("Plants", "Books"));

        // Store the Document
        bucket.upsert(JsonDocument.create("u:okan", arthur));

        // Load the Document and print it
        // Prints Content and Metadata of the stored Document
        System.out.println(bucket.get("u:okan"));

        // Create a N1QL Primary Index (but ignore if it exists)
        bucket.bucketManager().createN1qlPrimaryIndex(true, false);

        // Perform a N1QL Query
        final N1qlQueryResult result = bucket.query(
                N1qlQuery.parameterized("SELECT name, email FROM `okan-bucket` WHERE $1 IN interests",
                        JsonArray.from("Plants"))
        );

        // Print each found Row
        for (final N1qlQueryRow row : result) {
            // Prints {"name":"Okan", "email":"okan.uzun@outlook.com"}
            System.out.println(row);
        }
    }
}
