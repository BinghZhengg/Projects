import pandas as pd
import numpy as np

import string
from string import punctuation
import nltk
from nltk.corpus import stopwords
nltk.download('stopwords')

import tensorflow as tf

from tensorflow.keras.layers import Input, Dense, Dropout
from tensorflow.keras.models import Sequential 

import sklearn
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import CountVectorizer, TfidfTransformer, TfidfVectorizer

import joblib

import matplotlib.pyplot as plts

def reformat(text):
  stopwords = stopwords.words('english')
  no_punctuation = [char for char in text if char not in string.punctuation]
  no_punctuation = ''.join(no_punctuation)
  return ' '.join([word for word in no_punctuation.split() if word.lower not in stopwords])


def vectorize(text, vectorizer):
  text = vect.fit_transform(text)
  text = text.toarray()
  return text

def graph_output(prediction_array, index):
    labels = ["negative", "neutral", "positive"]
    predictions = np.argmax(prediction_array[index])
    title = "Prediction: " + labels[predictions]
    graph = plt.subplot()
    graph.bar(range(3), prediction_array[index])
    graph.set_xticks(range(3))
    graph.set_xticklabels(labels)

    graph.set_title(title)

    plt.show()


data = pd.read_csv('Reviews.csv')

data = data.drop(['UserId', 'Id', 'Time' ], axis=1)
data.dropna(inplace=True)

data['Polarity_Rating'] = data['Score'].apply(lambda x: 'Positive' if x > 3 else('Neutral' if x==3 else 'Negative'))

data_positive = data[data['Polarity_Rating'] == 'Positive']

data_negative = data[data['Polarity_Rating'] == 'Negative']

data_neutral = data[data['Polarity_Rating'] == 'Neutral']

data_positive = data_positive.sample(8000)
data_negative = data_negative.sample(8000)
data_neutral = data_neutral.sample(8000)

data = pd.concat([data_positive, data_negative, data_neutral])

data['reviews'] = data['Text'].apply(reformat)

data = data[["reviews", "Polarity_Rating"]]

one_hot = pd.get_dummies(data["Polarity_Rating"])

data = pd.concat([data, one_hot], axis=1)
data.drop(['Polarity_Rating'], axis=1, inplace=True)

x_rev = data["reviews"].values
y_pol = data.drop("reviews", axis =1)

x_rev_train, x_rev_test, y_pol_train, y_pol_test = train_test_split(x_rev, y_pol, test_size=0.30, shuffle=True)

vect = CountVectorizer()

vect.max_features = 15000

vect.fit(x_rev)

vocab = vect.vocabulary_

joblib.dump(vocab, "vocab.pkl")

x_rev_train_v = vect.transform(x_rev_train)

x_rev_test_v = vect.transform(x_rev_test)

x_rev_train_v = x_rev_train_v.toarray()

x_rev_test_v = x_rev_test_v.toarray()

model = Sequential()

model.add(Dense(units=15000, activation = "relu"))

model.add(Dropout(0.5))

model.add(Dense(units=2000, activation = "relu"))

model.add(Dropout(0.5))

model.add(Dense(units=500, activation = "relu"))

model.add(Dropout(0.5))

model.add(Dense(units=250, activation = "relu"))

model.add(Dropout(0.5))

model.add(Dense(units=3, activation = "softmax"))

opt = tf.keras.optimizers.Adam(learning_rate= 0.001)
model.compile(loss="categorical_crossentropy", optimizer=opt, metrics=["accuracy"])

model.fit(
    x=x_rev_train_v,
    y=y_pol_train,
    batch_size= 256,
    epochs= 10,
    validation_data=(x_rev_test_v, y_pol_test))

scores = model.evaluate(x_rev_test_v, y_pol_test, verbose=0)

model.save('sentiments.h5')


#webscrape

import requests
from bs4 import BeautifulSoup
url = ""
page = requests.get(url)
soup = BeautifulSoup(page.content,'html.parser')
reviews = soup.find_all(id = "content")

vect = CountVectorizer()
vect.vocabulary=vocab

clean_reviews = []

for review in reviews: 
  text = review.get_text().strip() 
  clean_reviews.append(text)

x = vectorize(clean_reviews, vect)
# Create a predictions array
pred_array = model.predict(x)


#run it through the network
#generate plot
for i in range(len(pred_array)):
  print(clean_reviews[i])
  graph_output(pred_array, i)

