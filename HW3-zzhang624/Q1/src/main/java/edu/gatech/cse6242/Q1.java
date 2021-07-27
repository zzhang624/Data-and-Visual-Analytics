package edu.gatech.cse6242;


import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.DataInput;
import java.io.DataOutput;


public class Q1 {
/* TODO: Update variable below with your gtid */
  final String gtid = "zzhang624";

  public static class MyMapper
       extends Mapper<Object, Text, Text, Text>{

    private final static IntWritable one = new IntWritable(1);

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
                String valueString = value.toString();
                String[] TaxiData = valueString.split(",");
		if ( TaxiData[0] != null && Float.parseFloat(TaxiData[2]) != 0 && Float.parseFloat(TaxiData[3]) > 0 ) {
                	context.write(new Text(TaxiData[0]), new Text("1," + TaxiData[3] ));
		}
//      	    while (itr.hasMoreTokens()) {
//        word.set(itr.nextToken());
//        context.write(word, one);
//      }
    }

//	public void map(LongWritable key, Text value, OutputCollector <Text, IntWritable> output, Reporter reporter) throws IOException {
//		String valueString = value.toString();
//		String[] SingleCountryData = valueString.split(",");
//		output.collect(new Text(SingleCountryData[7]), one);
//	}

  }

  public static class MyReducer
       extends Reducer<Text,Text,Text,Text> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {
      float sum = 0;
      float money = 0;

      for (Text val : values) {
//	System.out.print("1111");
        String valueString = val.toString();
        String[] newdata = valueString.split(",");
        sum = sum + Float.parseFloat(newdata[0]);
        money = money + new Float(newdata[1]);
      }
      String s_money = String.format("%,.2f", money);
//      System.out.print(s_money + "\n");
      String s_sum = String.format("%.0f", sum);
      context.write(key, new Text(s_sum + "," + s_money));
    }
  }


  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q1");

    /* TODO: Needs to be implemented */
    job.setJarByClass(Q1.class);
    job.setMapperClass(MyMapper.class);
//    job.setCombinerClass(MyReducer.class);
    job.setReducerClass(MyReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
