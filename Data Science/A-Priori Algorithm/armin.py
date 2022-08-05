#Binghan Zheng
#A-Priori Algorithm
from collections import defaultdict
from pandas import Series, DataFrame
import itertools as it
import pandas as pd
import math
import csv
import sys
import argparse
import collections
import glob
import os
import re
import requests
import string
import sys

class Armin():

    def apriori(self, input_filename, output_filename, min_support_percentage, min_confidence):
        """
        Implement the Apriori algorithm, and write the result to an output file

        PARAMS
        ------
        input_filename: String, the name of the input file
        output_filename: String, the name of the output file
        min_support_percentage: float, minimum support percentage for an itemset
        min_confidence: float, minimum confidence for an association rule to be significant

        """
        market_basket_dictionary = self.prepare_dictionary(input_filename)

        length = len(market_basket_dictionary)

        unique_items = self.find_unique(market_basket_dictionary)

        all_combinations = self.create_all_combinations(unique_items)

        reference = self.reference_sets(market_basket_dictionary, unique_items)

        CFI = self.return_dictionary(len(reference))
        VFI = self.return_dictionary(len(reference))

        pruned = self.return_dictionary(len(reference))
        rules = self.return_dictionary(len(reference))

        for i in range(1, len(reference) +1):
            if i == 1:
                temp = {}
                for j in all_combinations[1]:
                    temp.update(self.find_reference(j, market_basket_dictionary, reference))
                CFI[i] = temp

            else:
                temp_keys = self.prune_combinations(all_combinations, i, pruned[i-1])
                temp_CFI_keys = {}
                for j in temp_keys:
                    temp_CFI_keys.update(self.find_reference(j, market_basket_dictionary, reference))
                CFI[i] = temp_CFI_keys

            self.generate_VFI(length, min_support_percentage, VFI, CFI, pruned, i)

        rules = self.generate_rules(VFI, rules)
        rule_output = self.generate_valid_rules(rules, VFI, min_confidence)
        VFI_output = self.generate_VFI_output(VFI)

        self.generate_CSV(VFI_output, rule_output, min_support_percentage, min_confidence, output_filename)

    def generate_VFI(self, length, min_support_percentage, VFI, CFI, pruned, i ):

        temp_2 = {}
        temp_3 = []

        for key, value in CFI[i].items():
            score = self.calculate_support_percentage(value, length)
            if(score >= min_support_percentage):
                temp_2.update({key:score})
            else:
                temp_3.append(key)

        VFI[i] = temp_2
        pruned[i] = temp_3

    def prepare_dictionary(self, input_filename):

        market_basket_dictionary = {}

        with open(input_filename, 'r') as in_file:
            reader = csv.reader(in_file)
            for row in reader:
                market_basket_dictionary[row[0]] = row[1:]
                for key, value in market_basket_dictionary.items():
                    for item in value:
                        if (item == " "):
                            market_basket_dictionary[key].remove(item)

        return market_basket_dictionary

    def return_dictionary(self, length):

        results = {}

        for i in range(1, length+1):
            results[i] = ""

        return results

    def find_unique(self, market_basket_dictionary):

        unique_items=[]

        for key, value in market_basket_dictionary.items():
            for item in value:
                if item not in unique_items:
                    unique_items.append(item)
                    unique_items = sorted(unique_items)

        return unique_items

    def create_all_combinations(self, unique_items):

        all_combinations = {}

        for i in range(1, len(unique_items)+1):
            all_combinations[i] = list(it.combinations(unique_items, i))

        return all_combinations

    def prune_combinations(self, all_combinations, set_size, pruned_tuples):

        temp_combinations = all_combinations[set_size]

        return_combinations = []

        for i in temp_combinations:
            found = False
            for j in pruned_tuples:
                if(set(j).issubset(i)):
                    found = True
                    break
            if not found:
                return_combinations.append(i)

        return return_combinations

    def find_reference(self, candidates, market_basket_dictionary, references):

        return_dictionary = {}

        reference_list = list(references[x] for x in list(candidates))
        return_set = (reference_list[0].intersection(*reference_list))
        return_dictionary[candidates] = return_set

        return return_dictionary

    def reference_sets(self, market_basket_dictionary, unique_items):

        reference = {}

        for i in unique_items:
            temp = []
            for key, value in market_basket_dictionary.items():
                if i in value:
                    temp.append(key)
            reference[i] = set(temp)

        return reference

    def calculate_support_percentage(self, reference, length):

        return len(reference)/length

    def generate_rules(self, VFI, rules):

        for key, value in VFI.items():
            temp = {}
            for key_1, value_1 in value.items():
                for i in range(1, len(key_1)):
                    combinations = list(it.combinations(key_1, i))
                    for j in combinations:
                        test = set(key_1).difference(set(j))
                        if j in temp:
                            temp[j].append(tuple(test))
                        else:
                            temp.update({j:[tuple(test)]})         
            rules[key] = temp

        return rules

    def generate_valid_rules(self, rules, VFI, min_confidence):

        return_list = []

        for key, value in rules.items():
            for left_value, right_values in value.items():
                for single_right_value in right_values:
                    sub_list = ["R"]
                    support = VFI[key][tuple(sorted(left_value + single_right_value))]
                    confidence = support/VFI[len(left_value)][tuple(sorted(left_value))]
                    if confidence >= min_confidence:
                        sub_list.append("%.4f" % support)
                        sub_list.append("%.4f" % confidence)
                        sub_list = sub_list + list(left_value)
                        sub_list.append("\'=>\'")
                        sub_list = sub_list + list(single_right_value)
                        return_list.append(sub_list)

        return return_list

    def generate_VFI_output(self, VFI):

        return_list = []

        for key, value in VFI.items():
            for set, count in value.items():
                sub_list = ["S", "%.4f" % count]
                sub_list = sub_list + list(set)
                return_list.append(sub_list)

        return return_list

    def generate_CSV(self, VFI_Output, rule_output, min_support_percentage, min_confidence, output_filename):

        total_list = VFI_Output + rule_output

        file_name_num = str(min_support_percentage) + "_" + str(min_confidence) + ".csv"

        file_name_root = ["output_support_public_", "output_support_private_", "output_rule_public_", "output_rule_private_"]

        for name in file_name_root:
            input = name + file_name_num
            with open(input, 'w') as file:
                for row in total_list:
                    for i in range(len(row)):
                        if i == (len(row)-1):
                            file.write(str(row[i]))
                        else:
                            file.write(str(row[i]) + ',')
                    file.write('\n')



if __name__ == "__main__":
    armin = Armin()
    armin.apriori('input.csv', 'output.sup=0.5,conf=0.7.csv', 0.5, 0.7)
    armin.apriori('input.csv', 'output.sup=0.5,conf=0.8.csv', 0.5, 0.8)
    armin.apriori('input.csv', 'output.sup=0.6,conf=0.8.csv', 0.6, 0.8)
