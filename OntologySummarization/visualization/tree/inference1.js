//var m = [20, 120, 20, 120],
var m = [2*40, 2*240, 2*40, 2*240],
    w = 2*1280 - m[1] - m[3],
    h = 1000 - m[0] - m[2],
    i = 0,
    root;

var tree = d3.layout.tree()
    .size([h, w]);

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.y, d.x]; });

var vis = d3.select("#chart").append("svg:svg")
    .attr("width", w + m[1] + m[3])
    .attr("height", h + m[0] + m[2])
  .append("svg:g")
    .attr("transform", "translate(" + m[3] + "," + m[0] + ")");
    //.selectAll('g.x.axis g text').each(insertLinebreaks);
//svg = body.append('svg');
//svg.selectAll('g.x.axis g text').each(insertLinebreaks);
d3.json("inference1.json", function(json) {
//d3.json("../data/flare-wrong.json", function(json) {
  root = json;
  root.x0 = h / 2;
  root.y0 = 0;

  function toggleAll(d) {
    if (d.children) {
      d.children.forEach(toggleAll);
      toggle(d);
      d._expanded = false;
    }
  }

  // Initialize the display to show a few nodes.
  root.children.forEach(toggleAll);
  //toggle(root.children[1]);
  //toggle(root.children[1].children[2]);
  //toggle(root.children[9]);
  //toggle(root.children[9].children[0]);
  //toggle(root.children[2]);
  //toggle(root.children[3]);
  //toggle(root.children[3].children[0]);
  //toggle(root.children[3].children[9]);
  //toggle(root.children[10].children[18].children[22]);
  root._expanded = true;
  update(root);
  //console.log(root.name);
 
  threshold = 0.57;

  expandChildren(root);

 
});

function expandChildren(d) {
	//console.log(d.name);
  if(d.children) {
    //toggle(root.children[2]);
    for (var i=0;i<d.children.length;i++) {
      if(d.children[i].subtree>0 && d.children[i].nScore>threshold && d.children[i]._expanded == false) {
        toggle(d.children[i]);
	d.children[i]._expanded = true;
        update(d.children[i]);
	expandChildren(d.children[i]);
      }
    }
  }
}

function update(source) {
	//console.log("triple");
  var duration = d3.event && d3.event.altKey ? 5000 : 500;

  // Compute the new tree layout.
  var nodes = tree.nodes(root).reverse();

  // Normalize for fixed-depth.
  nodes.forEach(function(d) { d.y = d.depth * 180; });

  // Update the nodes…
  var node = vis.selectAll("g.node")
      .data(nodes, function(d) { return d.id || (d.id = ++i); });
 
  var nodeEnter = node.enter().append("svg:g")
      .attr("class", "node")
      .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
      .on("click", function(d) {toggle(d); update(d); expandChildren(d) });

  nodeEnter.append("svg:circle")
      .attr("r", 1e-6)
      .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });
  
  
  nodeEnter.append('svg:foreignObject')
      .attr("x", function(d) {
    	  return d==root? -150:-10 ; })
    	  //return d==root? -150: d.children || d._children ? -10 : -5; })
//      .attr("text-anchor", function(d) {
//    	  return d!=root? "middle":"end";
//    	  //return d.children || d._children ? "middle" : "middle"; 
//    	  })    	  
  .attr('width', 300)
  .attr('height', 80)
  .append("xhtml:body")
  //.html("<span style='color:red'>hi there<br>viva viva</span> <span style='color:blue'>world</span>");
  .html(function(d) { return "<span>"+d.subject+" <br> "+d.predicate + " <br>"+d.object; }
		  );

//  nodeEnter.append("svg:text")
//      .attr("x", function(d) { 
//    	  //return !d.children && !d._
//    	  return d.children || d._children ? -10 : 10; })
//      //.attr("y", function(d) { return 30; })
//      .attr("dy", ".35em")
//      .attr("text-anchor", function(d) {
//    	  return d!=root? "middle":"end";
//    	  //return d.children || d._children ? "middle" : "middle"; 
//    	  })
//      .text(function(d) { return "["+d.name+"] "+d.nScore; })
//      //.text(function(d) { return d.name; })
//      .style("fill-opacity", 1e-6);

  // Transition nodes to their new position.
  var nodeUpdate = node.transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

  nodeUpdate.select("circle")
      .attr("r", 4.5)
      .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

  nodeUpdate.select("text")
      .style("fill-opacity", 1);

  // Transition exiting nodes to the parent's new position.
  var nodeExit = node.exit().transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
      .remove();

  nodeExit.select("circle")
      .attr("r", 1e-6);

  nodeExit.select("text")
      .style("fill-opacity", 1e-6);

  // Update the links…
  var link = vis.selectAll("path.link")
      .data(tree.links(nodes), function(d) { return d.target.id; });

  // Enter any new links at the parent's previous position.
  link.enter().insert("svg:path", "g")
      .attr("class", "link")
      .attr("d", function(d) {
        var o = {x: source.x0, y: source.y0};
        return diagonal({source: o, target: o});
      })
    .transition()
      .duration(duration)
      .attr("d", diagonal);

  // Transition links to their new position.
  link.transition()
      .duration(duration)
      .attr("d", diagonal);

  // Transition exiting nodes to the parent's new position.
  link.exit().transition()
      .duration(duration)
      .attr("d", function(d) {
        var o = {x: source.x, y: source.y};
        return diagonal({source: o, target: o});
      })
      .remove();

  // Stash the old positions for transition.
  nodes.forEach(function(d) {
    d.x0 = d.x;
    d.y0 = d.y;
  });

}

// Toggle children.
function toggle(d) {
  if (d.children) {
    d._children = d.children;
    d.children = null;
  } else {
    d.children = d._children;
    d._children = null;
  }
}
