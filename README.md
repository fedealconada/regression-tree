# Regression trees project
Learning how regression trees work and implementing a tool that, given a dataset, builds a regression tree.


## Description
This projects aims to provide a tool that allows the generation of regression trees.

## Tool
This java-based tool consists of a UI where one can generate a regression tree based on training data specified from a .csv file.

Optionally, test data can be loaded either by specifying a test file whose data format matches the format of the training data, or by selecting the option “create the tests from the training values” in which case you a % of the training data will be used as evidence. 

If you have specified test data, once the tree is generated, each of the instances will be tested giving as a final result the average error of predictions made.

Note that, for both files, it must be specified if it has headers (in which case the first row of the .csv will be interpreted as the header) and which is the separator used in the file (comma, semicolon, tabulation).

Additionally, you have the option to enable the option to interpret the nominal attributes as ordinals. If you do so, the conversion of categorical attributes applies.

Finally, we must establish which are the cut-off limits for both the minimum number of test instances and the minimum percentage of the initial standard deviation.
