import pandas as pd
import csv
from requests import get
import json
from datetime import datetime, timedelta, date
import numpy as np
from scipy.spatial.distance import euclidean, cityblock, cosine
from scipy.stats import pearsonr

import csv
import re
import pandas as pd
import argparse
import collections
import json
import glob
import math
import os
import requests
import string
import sys
import time
import xml
import random

class Recommender(object):

    def __init__(self, training_set, test_set):

        if isinstance(training_set, str):
            # the training set is a file name
            self.training_set = pd.read_csv(training_set)
        else:
            # the training set is a DataFrame
            self.training_set = training_set.copy()

        if isinstance(test_set, str):
            # the test set is a file name
            self.test_set = pd.read_csv(test_set)
        else:
            # the test set is a DataFrame
            self.test_set = test_set.copy()
    
    def train_user_euclidean(self, data_set, userId):

        user_w_dict = {}

        last_col = data_set.pop(userId)
        data_set.insert(len(data_set.columns), userId, last_col)

        for user in data_set.columns[1:-1]:
            subset = data_set[[userId,user]][data_set[userId].notnull() & data_set[user].notnull()]
            dist = euclidean(subset[userId], subset[user])
            user_w_dict[user] = 1.0 / (1.0 + dist)

        return user_w_dict # dictionary of weights mapped to users. e.g. {"0331949b45":1.0, "1030c5a8a9":2.5}
    
    def train_user_manhattan(self, data_set, userId):

        user_w_dict = {}

        last_col = data_set.pop(userId)
        data_set.insert(len(data_set.columns), userId, last_col)

        for user in data_set.columns[1:-1]:
            subset = data_set[[userId,user]][data_set[userId].notnull() & data_set[user].notnull()]
            dist = cityblock(subset[userId], subset[user])
            user_w_dict[user] = 1.0 / (1.0 + dist)

        return user_w_dict# dictionary of weights mapped to users. e.g. {"0331949b45":1.0, "1030c5a8a9":2.5}

    def train_user_cosine(self, data_set, userId):
        
        user_w_dict = {}

        for user in data_set.columns[1:]:
            if (user != userId):
                subset = data_set[[userId,user]][data_set[userId].notnull() & data_set[user].notnull()]
                if (subset.empty):
                    user_w_dict[user] = 0
                else:
                    user_w_dict[user] = cosine(subset[userId], subset[user])

        return user_w_dict# dictionary of weights mapped to users. e.g. {"0331949b45":1.0, "1030c5a8a9":2.5}
   
    def train_user_pearson(self, data_set, userId):

        user_w_dict = {}

        last_col = data_set.pop(userId)
        data_set.insert(len(data_set.columns), userId, last_col)

        for user in data_set.columns[1:-1]:
            subset = data_set[[userId,user]][data_set[userId].notnull() & data_set[user].notnull()]
            user_w_dict[user] = pearsonr(subset[userId], subset[user])[0]

        return user_w_dict # dictionary of weights mapped to users. e.g. {"0331949b45":1.0, "1030c5a8a9":2.5}

    def train_user(self, data_set, distance_function, userId):

        if distance_function == 'euclidean':
            return self.train_user_euclidean(data_set, userId)

        elif distance_function == 'manhattan':
            return self.train_user_manhattan(data_set, userId)

        elif distance_function == 'cosine':
            return self.train_user_cosine(data_set, userId)

        elif distance_function == 'pearson':
            return self.train_user_pearson(data_set, userId)

        else:
            return None

    def get_user_existing_ratings(self, data_set, userId):

        df = data_set[['movieId', userId]][data_set[userId].notnull()]

        result = [tuple(x) for x in df.to_numpy()]

        return result# list of tuples with movieId and rating. e.g. [(32, 4.0), (50, 4.0)]

    def predict_user_existing_ratings_top_k(self, data_set, sim_weights, userId, k):

        sim_top = sorted(sim_weights.items(), key=lambda x:x[1], reverse=True)[:k]
        sim_top = dict(sim_top)

        df = data_set
        last_col = df.pop(userId)
        df.insert(len(df.columns), userId, last_col)

        predictions = []
        for index, row in df.iterrows():
            if (not np.isnan(row[userId])):
                predicted_rating = 0
                weights_sum = 0.0
                ratings = df.iloc[index][1:-1]
                for user in df.columns[1:-1]:
                    if (not np.isnan(ratings[user]) and user in sim_top.keys()):
                        predicted_rating += ratings[user] * sim_top[user]
                        weights_sum += sim_top[user]
                if(not weights_sum == 0):
                    predicted_rating /= weights_sum
                    predictions.append((row['movieId'], predicted_rating))

        return predictions # list of tuples with movieId and rating. e.g. [(32, 4.0), (50, 4.0)]
    
    def evaluate(self, existing_ratings, predicted_ratings):

        rmse = 0
        ratio = 0
        num = 0
        count = 0

        er = dict(existing_ratings)
        pr = dict(predicted_ratings)

        fr = {k: v for k, v in er.items() if v is not None}
        er.clear()
        er.update(fr)   #gets rid of all the None values

        fr = {k: v for k, v in pr.items() if v is not None}
        pr.clear()
        pr.update(fr)

        for k, v in er.items():
            if k in pr and er[k] is not None:
                num += (pr[k]-er[k])**2
                count +=1

        if (not count == 0):
            rmse = math.sqrt(num/count)

        ratio = count/len(er)

        Dict = {}
        Dict['rmse'] = rmse
        Dict['ratio'] = ratio

        return Dict# dictionary with an rmse value and a ratio. e.g. {'rmse':1.2, 'ratio':0.5}
    
    def single_calculation(self, distance_function, userId, k_values):

        user_existing_ratings = self.get_user_existing_ratings(self.test_set, userId)

        print("User has {} existing and {} missing movie ratings".format(len(user_existing_ratings), len(self.test_set) - len(user_existing_ratings)), file=sys.stderr)

        print('Building weights')

        sim_weights = self.train_user(self.training_set[self.test_set.columns.values.tolist()], distance_function, userId)

        result = []

        for k in k_values:
            print('Calculating top-k user prediction with k={}'.format(k))
            top_k_existing_ratings_prediction = self.predict_user_existing_ratings_top_k(self.test_set, sim_weights, userId, k)
            result.append((k, self.evaluate(user_existing_ratings, top_k_existing_ratings_prediction)))

        return result # list of tuples, each of which has the k value and the result of the evaluation. e.g. [(1, {'rmse':1.2, 'ratio':0.5}), (2, {'rmse':1.0, 'ratio':0.9})]

    def aggregate_calculation(self, distance_functions, userId, k_values):

        result_per_k = {}

        for func in distance_functions:
            print("Calculating for {} distance metric".format(func))
            for calc in self.single_calculation(func, userId, k_values):
                if calc[0] not in result_per_k:
                    result_per_k[calc[0]] = {}
                result_per_k[calc[0]]['{}_rmse'.format(func)] = calc[1]['rmse']
                result_per_k[calc[0]]['{}_ratio'.format(func)] = calc[1]['ratio']

        result = []

        for k in k_values:
            row = {'k':k}
            row.update(result_per_k[k])
            result.append(row)

        columns = ['k']

        for func in distance_functions:
            columns.append('{}_rmse'.format(func))
            columns.append('{}_ratio'.format(func))

        result = pd.DataFrame(result, columns=columns)

        return result
        
if __name__ == "__main__":
    recommender = Recommender("data/train.csv", "data/small_test.csv")
    print("Training set has {} users and {} movies".format(len(recommender.training_set.columns[1:]), len(recommender.training_set)))
    print("Testing set has {} users and {} movies".format(len(recommender.test_set.columns[1:]), len(recommender.test_set)))

    result = recommender.aggregate_calculation(['euclidean', 'cosine', 'pearson', 'manhattan'], "0331949b45", [1, 2, 3, 4])
    print(result)