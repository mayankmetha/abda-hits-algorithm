package com.pes.giraph;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.edge.MutableEdge;
import org.apache.giraph.GiraphRunner;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;

import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.giraph.GiraphRunner;
import org.apache.hadoop.util.ToolRunner;

import java.util.List;
import java.util.ArrayList;

public class App extends
BasicComputation<Text, Text, Text, Text> {

    @Override
    public void compute( Vertex<Text, Text, Text> vertex, Iterable<Text> messages) {

        //System.out.println("Starting compute " + counter++ + " for vertex " + vertex.getId());
        HitsScores cur = new HitsScores();

        long n = getTotalNumVertices(); 
        if ( getSuperstep() == 0 ) {
            // System.out.println("Initializing to 1.0 for vertex: " + vertex.getId());
            cur = new HitsScores(1.0, 1.0);
            vertex.setValue(new HitsScores().toText());
        } else {
            cur = new HitsScores(vertex.getValue());
        }
        // System.out.println("Current score for vertex " + vertex.getId() + " is " + cur);

        // Calculate auth score for all incoming edges i.e. messages
        // Message without prefix - reply from outgoing edges
        // Message with prefix - incoming edges
        List<Text> replies = new ArrayList<>();
        for ( Text m : messages ) {
            // System.out.println("Vertex " + vertex.getId() + " processing message: " + m);
            if (m.toString().startsWith("M") ) {
                String[] s = m.toString().split(" ");
                HitsScores v = new HitsScores(m);
                replies.add(new Text(s[1]));
                cur.addToAuthScore(v.getHubScore());
            } else {
                HitsScores v = new HitsScores(m);
                cur.addToHubScore(v.getAuthScore());
            }
        }
        aggregate(MasterComputer.HubId, new DoubleWritable(Math.pow(cur.getHubScore(),2)));
        aggregate(MasterComputer.AuthId, new DoubleWritable(Math.pow(cur.getAuthScore(), 2)));

        /*
        for (String vertexId : replies.keySet())
                HitsScores v = replies.get(vertexId);
                System.out.println("In vertex " + vertex.getId() + ": " + v);
                cur.addToHubScore(v.getAuthScore());
        }*/

        // System.out.println("For vertex " + vertex.getId() + ", scores are: " + cur);
        double authAgg = ((DoubleWritable)getAggregatedValue(MasterComputer.AuthId)).get();
        double hubAgg = ((DoubleWritable)getAggregatedValue(MasterComputer.HubId)).get();

        // System.out.println("AuthAggregate = " + authAgg + ", HubAgg = " + hubAgg);
        if ( authAgg > 0 ) {
            cur.setAuthScore(cur.getAuthScore() / (Math.sqrt(authAgg)));
            // Limit score to 1
            if ( cur.getAuthScore() > 1 ) {
                cur.setAuthScore(1.0);
            }
        }
        if ( hubAgg > 0 ) {
            cur.setHubScore(cur.getHubScore() / (Math.sqrt(hubAgg)));
            if ( cur.getHubScore() > 1 ) {
                cur.setHubScore(1.0);
            }
        }

        System.out.println("For vertex " + vertex.getId() + ", normalized scores are: " + cur);

        HitsScores prev = new HitsScores(vertex.getValue());
        vertex.setValue(cur.toText());
        if (getSuperstep() > 0 && getSuperstep() < 500
                && ((Math.abs(cur.getHubScore() - prev.getHubScore()) <= 0.01)
                    && (Math.abs(cur.getAuthScore() - prev.getAuthScore()) <= 0.01)) ) {
            System.out.println("Vertex " + vertex.getId() + " Voting to halt!");
            vertex.voteToHalt();  // signaling the end of the current BSP computation for the current vertex 
        } else {
            sendMessageToAllEdges(vertex, cur.toText("M", vertex.getId().toString()));
            sendMessageToMultipleEdges(replies.iterator(), cur.toText());
        }
    }  

/*
  public static void main(String[] args) throws Exception {   // main() method is not strictly speaking required,
    GiraphRunner runner = new GiraphRunner();
    runner.setConf(new YarnConfiguration());
    System.exit(ToolRunner.run(runner, args));    // but it could be useful when using Hadoop’s
  }
  */
}
