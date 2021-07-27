var svg = d3.select("svg");
var margin = 100;
var width = svg.attr("width") - 2*margin;
var height = svg.attr("height") - 2*margin;

d3.select("svg")
    .append("text")
    .attr("x", 300)
    .attr("y", margin)
    .text("Rebrickable Lego Sets by Year")
    .style("font-size","200%")

d3.select("svg")
  .append("text")
    .attr("x", width - 50)
    .attr("y", height + 150)
    .text("zzhang624")

var g = svg.append("g")
           .attr("transform", "translate("+margin+","+margin+")");

var x = d3.scaleBand()
          .rangeRound([0, width])
	  .padding(0.2);

var y = d3.scaleLinear()
          .rangeRound([height, 0]);

d3.dsv(",", "q3.csv").then(function(data) {

  
    x.domain(data.map(function (d) {
	return d.year;
    }));

    y.domain([0, d3.max(data, function (d) {
	return Number(d.running_total);
    })]);
   
    g.append("g")
     .attr("transform", "translate(0," + height + ")")
     .call(d3.axisBottom(x)).selectAll(".tick").each(function(_,i){
      if(i%3 !== 1) d3.select(this).remove();
    });

    g.append("g")
     .call(d3.axisLeft(y)); 
    
    g.selectAll(".bar")
	.data(data)
	.enter().append("rect")
	.attr("x", function (d) {
		return x(d.year);
	})
	.attr("y", function (d) {
		return y(Number(d.running_total));
	})
	.attr("width", x.bandwidth())
	.attr("height", function (d) {
		return height - y(Number(d.running_total));
	})
        .attr("fill","blue");


});
