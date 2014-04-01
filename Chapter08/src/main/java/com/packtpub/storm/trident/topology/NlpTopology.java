package com.packtpub.storm.trident.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.packtpub.storm.trident.operator.PersistenceFunction;
import com.packtpub.storm.trident.operator.TweetSplitterFunction;
import com.packtpub.storm.trident.operator.WordFrequencyFunction;
import com.packtpub.storm.trident.state.DruidStateFactory;
import com.packtpub.storm.trident.state.DruidStateUpdater;
import com.packtpub.twitter.TwitterSpout;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.Stream;
import storm.trident.TridentTopology;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;

public class NlpTopology {
    private static final Logger LOG = LoggerFactory.getLogger(NlpTopology.class);

    public static StormTopology buildTopology() {
        LOG.info("Building topology.");
        TridentTopology topology = new TridentTopology();
        TwitterSpout spout = new TwitterSpout();
        Stream inputStream = topology.newStream("nlp", spout);
        try {
            inputStream.each(new Fields("tweet"), new TweetSplitterFunction(), new Fields("word"))
                    .each(new Fields("searchphrase", "tweet", "word"), new WordFrequencyFunction(), new Fields("baseline"))
                    .each(new Fields("searchphrase", "tweet", "word", "baseline"), new PersistenceFunction(), new Fields("none"))
                    .partitionPersist(new DruidStateFactory(), new Fields("searchphrase", "tweet", "word", "baseline"), new DruidStateUpdater());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return topology.build();
    }

    public static void main(String[] args) throws Exception {
        //https://api.hmsonline.com/v1/search/masterfile?timestamp=1381505612735&key=cQUroC22o0OFvyHVt2cTYg&signature=0r7Zm++/ve+/vTIk77+9eXkh77+9al/vv70h77+9
        String time = "1381505612735";
        String BaseUrl = "https://api.hmsonline.com";
//        String Signature= ExecutionUtil.getDynamicProcessProperty("Signature");    
        String str_Key = "F8EnkY4tINSfinyXgioFV1bpq9HIHZ4XhjmkXkYSqoA=";

        System.out.println("Secret_Decoded" + str_Key);
        String contentTosign = "/v1/search/masterfile?timestamp=" + time + "&key=cQUroC22o0OFvyHVt2cTYg";

        System.out.println("" + Base64.isArrayByteBase64(str_Key.getBytes()));

        byte[] key = Base64.decodeBase64(str_Key);
//             System.out.println("Secret_Decoded" +key );
        SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");
//              System.out.println("SecretKeySpec" +sha1Key );
        Mac mac = Mac.getInstance("HmacSHA1");

//              System.out.println("mac");   
        mac.init(sha1Key);
        byte[] bytes = mac.doFinal(contentTosign.getBytes("UTF-8"));

        System.out.println("SystemMiili = https://api.hmsonline.com/" + contentTosign + "&signature=" + new String(bytes, "UTF-8"));
        System.out.println("BaseUrl = " + contentTosign + "&signature=");
        System.out.println("Signature = " + new String(Base64.encodeBase64String(bytes)));

        final Config conf = new Config();
        final LocalCluster cluster = new LocalCluster();

        LOG.info("Submitting topology.");

        cluster.submitTopology("nlp", conf, buildTopology());
        LOG.info("Topology submitted.");
        Thread.sleep(600000);
    }
}
