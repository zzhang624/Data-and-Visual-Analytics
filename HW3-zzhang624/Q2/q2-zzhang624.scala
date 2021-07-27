// Databricks notebook source
// STARTER CODE - DO NOT EDIT THIS CELL
import org.apache.spark.sql.functions.desc
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import spark.implicits._

// COMMAND ----------

// STARTER CODE - DO NOT EDIT THIS CELL
val customSchema = StructType(Array(StructField("lpep_pickup_datetime", StringType, true), StructField("lpep_dropoff_datetime", StringType, true), StructField("PULocationID", IntegerType, true), StructField("DOLocationID", IntegerType, true), StructField("passenger_count", IntegerType, true), StructField("trip_distance", FloatType, true), StructField("fare_amount", FloatType, true), StructField("payment_type", IntegerType, true)))

// COMMAND ----------

// STARTER CODE - YOU CAN LOAD ANY FILE WITH A SIMILAR SYNTAX. Edit the filepath on line 7 (.load(...)) to point to your uploaded file
val df = spark.read
   .format("com.databricks.spark.csv")
   .option("header", "true") // Use first line of all files as header
   .option("nullValue", "null")
   .schema(customSchema)
   .load("/FileStore/tables/nyc_tripdata-069c2.csv") // UPDATE this line with your filepath. Refer Databricks Setup Guide Step 3.
   .withColumn("pickup_datetime", from_unixtime(unix_timestamp(col("lpep_pickup_datetime"), "MM/dd/yyyy HH:mm")))
   .withColumn("dropoff_datetime", from_unixtime(unix_timestamp(col("lpep_dropoff_datetime"), "MM/dd/yyyy HH:mm")))
   .drop($"lpep_pickup_datetime")
   .drop($"lpep_dropoff_datetime")

// COMMAND ----------

// STARTER CODE - DO NOT EDIT THIS CELL
// Some commands that you can use to see your dataframes and results of the operations. You can alternatively uncomment the display() and show() functions to see the data differently. These two functions will be useful in reporting the results.

//display(df) //display in a tabular format for easy download

//df.show(5) // view the first 5 rows of the dataframe

// COMMAND ----------

// BEFORE YOU BEGIN: Replace gburdell3 with your GT username.
val gt_username = "zzhang624"
println(gt_username)

// COMMAND ----------

// PART 1: Filter the data to only keep the rows where "PULocationID" and the "DOLocationID" are different and the "trip_distance" is strictly greater than 2.0 (>2.0).
// Hint: Checkout the filter() function.

// VERY VERY IMPORTANT: ALL THE SUBSEQUENT OPERATIONS MUST BE PERFORMED ON THIS FILTERED DATA

// ENTER THE CODE BELOW
var df1 = df.filter($"PULocationID" =!= $"DOLocationID" and $"trip_distance" > 2.0)
df1.show()

// COMMAND ----------

// PART 2: The top-5 most popular drop locations - "DOLocationID", sorted in descending order - if there is a tie, then one with lower "DOLocationID" gets listed first
// Hint: Checkout the groupBy(), orderBy() and count() functions.

// ENTER THE CODE BELOW
var df2 = df1.groupBy($"DOLocationID").agg(count($"DOLocationID") as "DO_num").orderBy($"DO_num".desc, $"DOLocationID".asc).limit(5)
df2.show()

// COMMAND ----------

// PART 3: The top-5 most popular pickup locations - "PULocationID", sorted in descending order - if there is a tie, then one with lower "PULocationID" gets listed first 

// ENTER THE CODE BELOW
var df3 = df1.groupBy($"PULocationID").agg(count($"PULocationID") as "PU_num").orderBy($"PU_num".desc, $"PULocationID".asc).limit(5)
df3.show()

// COMMAND ----------

// PART 4: The top-5 most popular pickup-dropoff pairs - sorted in descending order - if there is a tie, then one with lower "PULocationID" gets listed first.

// ENTER THE CODE BELOW
var df4 = df1.groupBy($"PULocationID", $"DOLocationID").count().orderBy($"count".desc, $"PULocationID".asc).limit(5)
df4.show() 

// COMMAND ----------

// PART 5: Number of dropoffs over the period from January 1, 2019 (inclusive of January 1) to January 5, 2019 (inclusive of January 5). List the entries by day from January 1 to January 5.

// Reference: https://www.obstkel.com/blog/spark-sql-date-functions
// Read in the data and extract the month and year from the date column.
// Hint 1: Observe how we extracted the date from the timestamp in the thrid cell.
// Hint 2: Filter by month as well since there are a few dates for the month of February present in the dataset.

// ENTER THE CODE BELOW
var df5 = df1.withColumn("day", dayofmonth($"dropoff_datetime")).withColumn("month", month($"dropoff_datetime")).withColumn("year", year($"dropoff_datetime")).filter($"day" >= 1 and $"day" <= 5 and $"month" === 1 and $"year" === 2019).groupBy($"day").agg(count($"day") as "entries").orderBy($"day".asc)
df5.show()

// COMMAND ----------

// PART 6: List the top-3 locations with the maximum overall activity, i.e. sum of all pickups and all dropoffs at that LocationID. In case of a tie, the lower LocationID gets listed first.
// Hint: Checkout join() and na.drop() functions. You will need to perform a join operation between two dataframes which you created in earlier parts to get the result.

// ENTER THE CODE BELOW
var df6_1 = df1.groupBy($"PULocationID").agg(count($"PULocationID") as "PU_NUM").withColumnRenamed("PULocationID", "ID")
var df6_2 = df1.groupBy($"DOLocationID").agg(count($"DOLocationID") as "DO_NUM").withColumnRenamed("DOLocationID", "ID")
var df6_3 = df6_1.join(df6_2, Seq("ID"), "full").na.fill(0)
var df6 = df6_3.groupBy("ID").agg(sum($"PU_NUM" + $"DO_NUM") as "overall_activity").orderBy($"overall_activity".desc, $"ID".asc).limit(3)
df6.show()

// COMMAND ----------


