package edu.gatech.cse6242;
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


public class Q4b {

  public static class MyMapper
       extends Mapper<Object, Text, Text, Text>{

    private final static IntWritable one = new IntWritable(1);

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
                String valueString = value.toString();
                String[] TaxiData = valueString.split("\t");
                context.write(new Text(TaxiData[2]), new Text(TaxiData[3]));
    }
  }

  public static class MyReducer
       extends Reducer<Text,Text,Text,Text> {

    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {

      float sum = 0;
      float count = 0;

      for (Text val : values) {
        String valueString = val.toString();
        sum = sum + Float.parseFloat(valueString);
        count = count + 1;
      }
      float ave = sum / count;
      String s_ave = String.format("%.2f", ave);
      context.write(key, new Text(s_ave));
    }
  }

  public static void main(String[] args) throws Exception {
    /* TODO: Update variable below with your gtid */
    final String gtid = "zzhang624";

    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q4b");
    job.setJarByClass(Q4b.class);
    job.setMapperClass(MyMapper.class);
    job.setReducerClass(MyReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
