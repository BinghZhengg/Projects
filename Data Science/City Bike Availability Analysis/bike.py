#Binghan Zheng
#Pandas DataFrame Manipulation
import argparse
import collections
import csv
import json
import glob
import math
import os
import pandas as pd
import re
import requests
import string
import sys
import time
import xml



class Bike():

    def __init__(self, baseURL, station_info, station_status):
        # initialize the instance
        self.baseURL = baseURL
        self.station_info = station_info
        self.station_status = station_status
        #pull station information (ID, name, address, coordinates, etc.)
        self.station_infoURL = baseURL+ station_info
        #pull stattion status (ID, bike available, docks available, etc.)
        self.station_statusURL = baseURL+station_status

        #read the pages
        self.df_info_out = pd.read_json(self.station_infoURL)
        self.df_status_out = pd.read_json(self.station_statusURL)

        #The data pulled is in the form of a list of dictionaries, each dictionary corresponds to the current state (set keys corresponds to attribute value) of all stations in Pittsburgh city.

    def total_bikes(self):
        # return the total number of bikes available

        #Transform the data into a dataframe
        df_status = self.df_status_out
        stations_list = df_status['data']
        df = pd.DataFrame.from_dict(stations_list['stations'], orient = 'columns')

        #return the sum the values in the column for available bikes
        return df["num_bikes_available"].sum()

    def total_docks(self):
        # return the total number of docks available

        #Transform the data into a dataframe 
        df_status = self.df_status_out
        stations_list = df_status['data']
        df = pd.DataFrame.from_dict(stations_list['stations'], orient = 'columns')

        #return the sum the values in the column for available docks
        return df["num_docks_available"].sum()

    def percent_avail(self, station_id):
        # return the percentage of available docks

        #Transform the data into a dataframe 
        df_status = self.df_status_out
        stations_list = df_status['data']
        df = pd.DataFrame.from_dict(stations_list['stations'], orient = 'columns')

        #set up variables to record available docks and occupied docks
        occupied=0
        available =0
        found = False

        #iterate through dataframe to access selected station's dock and bike availability status
        for i, j in df.iterrows():
            if j['station_id']== str(station_id):
                occupied = j["num_bikes_available"]
                available = j["num_docks_available"]
                found = True
            else:
                continue

        #return calculated dock percent availability if station exists
        if found:
            percent = math.floor(available/(available+occupied)*100)
            percent = str(percent) + "%"
        else:
            percent = ""

        
        return  percent

    def closest_stations(self, latitude, longitude):
        # return the stations closest to the given coordinates
    
        #Transform the data into a dataframe 
        df_info = self.df_info_out
        stations_list = df_info['data']
        df = pd.DataFrame.from_dict(stations_list['stations'], orient = 'columns')

        #Create reference dataframe
        new_data_frame = pd.DataFrame(columns = ["station_id", 'name', 'distances'])

        #Iterate through dataframe to calculate distances and store into reference df
        for i, j in df.iterrows():
            distances = self.distance(latitude, longitude, j['lat'], j['lon'])
            a_dict = {"station_id":j["station_id"],"name":j["name"],"distances":distances}
            new_data_frame.loc[len(new_data_frame.index)] = [j["station_id"], j["name"], distances]

        #Sort reference dataframe and store in sorted
        sorted = new_data_frame.sort_values(by=['distances'])

        #Pull 3 shortest distances
        subset_data_frame = sorted[0:3]

        #create empty dictionary to store conversion for result
        dict = {}

        #iterate to append station ID as key with name as value
        for i, j in subset_data_frame.iterrows():
            dict[j["station_id"]] = j["name"]

        #return results as a dictionary
        return dict

    def closest_bike(self, latitude, longitude):
        # return the station with available bikes closest to the given coordinates

        #Transform the data into a dataframe
        df_info = self.df_info_out
        stations_list = df_info['data']
        df = pd.DataFrame.from_dict(stations_list['stations'], orient = 'columns')

        #Transform the data into a dataframe
        df_status = self.df_status_out
        stations_list2 = df_status['data']
        dfs = pd.DataFrame.from_dict(stations_list2['stations'], orient = 'columns')

        #Create reference dataframe
        new_data_frame = pd.DataFrame(columns = ["station_id", 'name', 'distances'])

        #Iterate through dataframe to calculate distances and store into reference df if there are bikes at the station
        for i, j in df.iterrows():
            if dfs.loc[i]["num_bikes_available"] >0:
                distances = self.distance(latitude, longitude, j['lat'], j['lon'])
                a_dict = {"station_id":j["station_id"],"name":j["name"],"distances":distances}
                new_data_frame.loc[len(new_data_frame.index)] = [j["station_id"], j["name"], distances]
            else:
                continue
        
        #Sort reference dataframe by distance
        sorted = new_data_frame.sort_values(by=['distances'])

        #Pull top result for closest station with a bike
        subset_data_frame = sorted.iloc[0]

        #create empty dictionary for final result
        dict = {}
        #append 
        dict[subset_data_frame["station_id"]] = subset_data_frame["name"]

        return dict

    def station_bike_avail(self, latitude, longitude):
        # return the station with available bikes closest to the given coordinates

        #Transform the station data into a dataframe
        df_info = self.df_info_out
        stations_list = df_info['data']
        df = pd.DataFrame.from_dict(stations_list['stations'], orient = 'columns')

        #Transform the status data into a dataframe
        df_status = self.df_status_out
        stations_list2 = df_status['data']
        dfs = pd.DataFrame.from_dict(stations_list2['stations'], orient = 'columns')

        #Create reference dataframe for station and bikes available
        new_data_frame1 = pd.DataFrame(columns = ["station_id", 'num_bikes_available'])

        #Iterate to append station and bikes available for the station if there are
        for i, j in df.iterrows():
            available = dfs.loc[i]["num_bikes_available"]
            if available >0:
                if j['lat']==latitude and j['lon']==longitude :
                    a_dict = {"station_id":j["station_id"], "num_bikes_available":available}
                    new_data_frame1.loc[len(new_data_frame1.index)] = [j["station_id"], available]
            else:
                continue
        #Create dictionary to store final result
        dict = {}

        #Append result to dictionary
        if len(new_data_frame1)>0:
            subset_data_frame = new_data_frame1.iloc[0]
            dict[subset_data_frame["station_id"]] = subset_data_frame["num_bikes_available"]

        #Return results
        return dict

    def distance(self, lat1, lon1, lat2, lon2):
        #calculate euclidian distance given 2 coordinates
        p = 0.017453292519943295
        a = 0.5 - math.cos((lat2-lat1)*p)/2 + math.cos(lat1*p)*math.cos(lat2*p) * (1-math.cos((lon2-lon1)*p)) / 2

        return 12742 * math.asin(math.sqrt(a))


# testing and debugging the Bike class

if __name__ == '__main__':
    instance = Bike('https://api.nextbike.net/maps/gbfs/v1/nextbike_pp/en', '/station_information.json', '/station_status.json')
    print('------------------total_bikes()-------------------')
    t_bikes = instance.total_bikes()
    print(type(t_bikes))
    print(t_bikes)
    print()

    print('------------------total_docks()-------------------')
    t_docks = instance.total_docks()
    print(type(t_docks))
    print(t_docks)
    print()

    print('-----------------percent_avail()------------------')
    p_avail = instance.percent_avail(342853) # replace with station ID
    print(type(p_avail))
    print(p_avail)
    print()

    print('----------------closest_stations()----------------')
    c_stations = instance.closest_stations(40.444618, -79.954707) # replace with latitude and longitude
    print(type(c_stations))
    print(c_stations)
    print()

    print('-----------------closest_bike()-------------------')
    c_bike = instance.closest_bike(40.444618, -79.954707) # replace with latitude and longitude
    print(type(c_bike))
    print(c_bike)
    print()

    print('---------------station_bike_avail()---------------')
    s_bike_avail = instance.station_bike_avail(40.440877, -80.00309) # replace with exact latitude and longitude of station
    print(type(s_bike_avail))
    print(s_bike_avail)


