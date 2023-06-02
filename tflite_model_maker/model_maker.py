import tensorflow as tf
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.layers import Dense
from tensorflow.keras.models import Model
from tensorflow.keras.optimizers import Adam

# Set up the parameters
image_height = 224
image_width = 224
num_classes = 2
batch_size = 1

TRAIN_DIR = 'D:/homeguard_thesis/tflite_model_maker/image_dir_train'

# Load and preprocess the training data
train_data_generator = tf.keras.preprocessing.image.ImageDataGenerator(rescale=1./255)
train_generator = train_data_generator.flow_from_directory(
    TRAIN_DIR,
    target_size=(image_height, image_width),
    batch_size=batch_size,
    class_mode='categorical',
    shuffle=True
)

# Load the MobileNetV2 model without the top layer
base_model = MobileNetV2(include_top=False, weights='imagenet', input_shape=(image_height, image_width, 3))

# Add a custom top layer for object detection
x = base_model.output
x = tf.keras.layers.GlobalAveragePooling2D()(x)
x = Dense(128, activation='relu')(x)
predictions = Dense(num_classes, activation='softmax')(x)

# Create the model
model = Model(inputs=base_model.input, outputs=predictions)

# Compile the model
model.compile(optimizer=Adam(learning_rate=0.0001), loss='categorical_crossentropy', metrics=['accuracy'])

# Train the model
model.fit(train_generator, epochs=10)

# Convert the model to TensorFlow Lite format
converter = tf.lite.TFLiteConverter.from_keras_model(model)
converter.optimizations = [tf.lite.Optimize.DEFAULT]
converter.target_spec.supported_types = [tf.float32]
converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS]
converter.experimental_new_converter = True
tflite_model = converter.convert()

# Save the TensorFlow Lite model to a file
with open('modeltest8.tflite', 'wb') as f:
    f.write(tflite_model)