package com.pes.giraph;

import com.google.common.collect.Lists;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.edge.EdgeFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.NullWritable;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Simple text-based {@link org.apache.giraph.io.VertexInputFormat}
 * Inputs have text ids, no edge weights, and no vertex values.
 * (Vertex values are set to a Text)
 *
 * Each line consists of:
 * vertex neighbor1 neighbor2 ...
 *
 * Values can be separated by spaces or tabs.
 */
public class SimpleHitsInputFormat extends
    TextVertexInputFormat<Text, Text, Text> {
  /** Separator of the vertex and neighbors */
  private static final Pattern SEPARATOR = Pattern.compile("[\t ]");

  @Override
  public TextVertexReader createVertexReader(InputSplit split,
      TaskAttemptContext context)
    throws IOException {
    return new SimpleHitsVertexReader();
  }

  /**
   * Vertex reader associated with {@link SimplePageRankInputFormat}.
   */
  public class SimpleHitsVertexReader extends
    TextVertexReaderFromEachLineProcessed<String[]> {
    /**
     * Cached vertex id for the current line
     */
    private Text id;

    @Override
    protected String[] preprocessLine(Text line) throws IOException {
      String[] tokens = SEPARATOR.split(line.toString());
      id = new Text(tokens[0]);
      return tokens;
    }

    @Override
    protected Text getId(String[] tokens) throws IOException {
      return id;
    }

    @Override
    protected Text getValue(String[] tokens) throws IOException {
      return new Text();
    }

    @Override
    protected Iterable<Edge<Text, Text>> getEdges(
        String[] tokens) throws IOException {
      List<Edge<Text, Text>> edges =
          Lists.newArrayListWithCapacity(tokens.length - 1);
      for (int i = 1; i < tokens.length; i++) {
        edges.add(EdgeFactory.create(
            new Text(tokens[i]), new HitsScores().toText()));
      }
      return edges;
    }
  }
}
