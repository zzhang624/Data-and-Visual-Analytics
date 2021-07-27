import numpy as np 
import ast
from util import entropy, information_gain, partition_classes, best_split

class DecisionTree(object):
    def __init__(self, max_depth):
        # Initializing the tree as an empty dictionary or list, as preferred
        self.tree = []
        self.max_depth = max_depth
        pass
    	
    def learn(self, X, y, par_node = {}, depth=0):
        # TODO: Train the decision tree (self.tree) using the the sample X and labels y
        # You will have to make use of the functions in utils.py to train the tree

        # Use the function best_split in util.py to get the best split and 
        # data corresponding to left and right child nodes
        
        # One possible way of implementing the tree:
        #    Each node in self.tree could be in the form of a dictionary:
        #       https://docs.python.org/2/library/stdtypes.html#mapping-types-dict
        #    For example, a non-leaf node with two children can have a 'left' key and  a 
        #    'right' key. You can add more keys which might help in classification
        #    (eg. split attribute and split value)
        ### Implement your code here
        #############################################
        self.tree = self.Build_tree(X, y)

    def Build_tree(self, X, y):
        if entropy(y) == 0.0:
          if y[0]  == 1:
            return 1
          else:
            return 0
        else:
          split_attribute, split_value, X_left, X_right, y_left, y_right = best_split(X, y)
          while len(y_left) == 0 or len(y_right) == 0:
            split_attribute, split_value, X_left, X_right, y_left, y_right = best_split(X, y)
          return [split_attribute, split_value, self.Build_tree(X_left, y_left), self.Build_tree(X_right, y_right)]
        #############################################


    def classify(self, record):
        # TODO: classify the record using self.tree and return the predicted label
        ### Implement your code here
        #############################################
        tree_temp = self.tree
        while isinstance(tree_temp, list):
          split_attribute = tree_temp[0]
          split_value = tree_temp[1]
          if record[split_attribute] <= split_value:
            tree_temp = tree_temp[2]
          else:
            tree_temp = tree_temp[3]
        
        return tree_temp
        #############################################
