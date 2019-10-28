#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Oct 25 20:57:42 2019

@author: PALASH
"""
from keras.models import Model, Sequential
from keras.layers import Conv2D, MaxPooling2D, BatchNormalization, GlobalAveragePooling2D
from keras.layers import Activation, Dropout, Flatten, Dense
from keras.optimizers import RMSprop, SGD
from keras.applications import ResNet50
from keras.preprocessing.image import img_to_array
from keras.applications import imagenet_utils
from PIL import Image
import numpy as np
import flask
import io
from keras import backend as K
import tensorflow as tf
import keras

 

app = flask.Flask(__name__)
 

def load_label():
    global labels
    labels = ['ben_afflek',  'elton_john',  'jerry_seinfeld',  'madonna',  'mindy_kaling']
 

def resnet50tl(input_shape, outclass, sigma='sigmoid'):
    resnet50weight = '/Users/PALASH/Desktop/dogs/resnet50/resnet50_weights_tf_dim_ordering_tf_kernels_notop.h5'
    base_model = None
    base_model = keras.applications.resnet50.ResNet50(weights=None, include_top=False, input_shape=input_shape)
    base_model.load_weights(resnet50weight)
    print(input_shape)
    print(base_model.output_shape)

    
    top_model = Sequential()
    top_model.add(Flatten(input_shape=base_model.output_shape[1:]))
    for i in range(2):
        top_model.add(Dense(4096, activation='relu'))
        top_model.add(Dropout(0.5))
    top_model.add(Dense(outclass, activation=sigma))

    model = None
    model = Model(inputs=base_model.input, outputs=top_model(base_model.output))
    
    return model


def load_model(): 
    
    
    
    numclasses = 5
    keras.backend.clear_session()
    img_width, img_height = 200, 200
    if K.image_data_format() == 'channels_first':
        input_shape = (3, img_width, img_height)
    else:
        input_shape = (img_width, img_height, 3)
    
    global model_res
    model_res = resnet50tl(input_shape, numclasses, 'softmax')
    model_res.load_weights('/Users/PALASH/Desktop/dogs/Human/model_weights.h5')
    model_res._make_predict_function()

    print("Model loaded succefully")
      
 
@app.route("/predict_name",methods = ["POST"])   
def predict():   
    data  = {"success":False}
 
    
    if flask.request.method == "POST":
        if flask.request.files.get("image"):
            
            image = flask.request.files["image"].read()
            image = Image.open(io.BytesIO(image))
            
            image = resize_image(image,target = (200,200))
            
            preds = model_res.predict(image)
            #results = imagenet_utils.decode_predictions(preds)
            result = np.squeeze(preds)
            result_indices = np.argmax(result)
            data["predictions"] = []
            
            data["predictions"].append("{}, {:.2f}%".format(labels[result_indices], result[result_indices]*100))
            
            #for (imagenetID , label , prob) in  results[0]:
             #   r = {"label" : label , probability : float(prob)}
              #  data[predictions].append(r)
                
            
            data["success"] = True
        
        else:
               print("No Image Found {}".format(file))    

             
             
    return flask.jsonify(data)    
    
    
    
 
    
#Resizing recieved image to target size 
#RGB (Red, Green, Blue) are 8 bit each.
#The range for each individual colour is 0-255 (as 2^8 = 256 possibilities).
#The combination range is 256*256*256.
#By dividing by 255, the 0-255 range can be described 
#with a 0.0-1.0 range where 0.0 means 0 (0x00) and 1.0 means 255 (0xFF).
    
def resize_image(image,target):
     
     if image.mode != "RGB":
         image = image.convert("RGB")
         
     image = image.resize(target)
     image = img_to_array(image)
     image = np.expand_dims(image , axis = 0)
     image /= 255.
    
     
     return image
     
        
    
    
if __name__ == '__main__':
    load_label()
    print("Loading keras model")
    load_model()
    app.run(host= '0.0.0.0')
    
