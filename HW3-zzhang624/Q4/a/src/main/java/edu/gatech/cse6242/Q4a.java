package edu.gatech.cse6242;
import java.util.StringTokenizer;
import java.lang.Object;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;


public class Q4a {
  public static class DegreeMapper extends Mapper<Object, Text, IntWritable, IntWritable> {

    private final static IntWritable one = new IntWritable(1);
    private final static IntWritable negone = new IntWritable(-1);
    private IntWritable node1 = new IntWritable();
    private IntWritable node2 = new IntWritable();

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      String valueString = value.toString();
      String[] TaxiData = valueString.split("\t");
      node1.set(Integer.parseInt(TaxiData[0]));
      node2.set(Integer.parseInt(TaxiData[1]));
      context.write(node1, one);
      context.write(node2, negone);
    }
  }

    public static class DegreeReducer extends Reducer<IntWritable,IntWritable,IntWritable,IntWritable> {

    private IntWritable result = new IntWritable();

    public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
      int d = 0;
      for (IntWritable val: values) {
        d += val.get();
      }
//      public void map(LongWritable key, Text value, OutputCollector <Text, IntWritable> output, Reporter reporter) throws IOException {
//              String valueString = value.toString();
//              String[] SingleCountryData = valueString.split(",");
//              output.collect(new Text(SingleCountryData[7]), one);
//      }

      result.set(d);
      context.write(key, result);
    }
  }

  public static class CountMapper extends Mapper<Object, Text, IntWritable, IntWritable> {

    private final static IntWritable one = new IntWritable(1);
    private IntWritable d = new IntWritable();
//      System.out.print("1111");
//
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString(), "\n");
      while (itr.hasMoreTokens()) {
        String line = itr.nextToken();
        String tokens[] = line.split("\t");
        d.set(Integer.parseInt(tokens[1]));
        context.write(d, one);
      }
    }
  }

  public static class CountReducer extends Reducer<IntWritable,IntWritable,IntWritable,IntWritable> {

    private IntWritable result = new IntWritable();

    public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
      int s = 0;
      for (IntWritable val : values) {
        s += val.get();
      }
      //      System.out.print(s_money + "\n");

      result.set(s);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {

    /* TODO: Update variable below with your gtid */
    final String gtid = "zzhang624";
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "job1");
    job.setJarByClass(Q4a.class);
    job.setMapperClass(DegreeMapper.class);
    job.setCombinerClass(DegreeReducer.class);
    job.setReducerClass(DegreeReducer.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path("FJobOutput"));
    job.waitForCompletion(true);
//    job.setCombinerClass(MyReducer.class);

    Job job2 = Job.getInstance(conf, "job2");
    job2.setJarByClass(Q4a.class);
    job2.setMapperClass(CountMapper.class);
    job2.setCombinerClass(CountReducer.class);
    job2.setReducerClass(CountReducer.class);
    job2.setOutputKeyClass(IntWritable.class);
    job2.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job2, new Path("FJobOutput"));
    FileOutputFormat.setOutputPath(job2, new Path(args[1]));

    System.exit(job2.waitForCompletion(true) ? 0 : 1);
  }
}
