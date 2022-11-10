from vosk import Model, KaldiRecognizer
import pyaudio
import json
import socket



HOST = "localhost"
PORT = 25555

socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.connect((HOST, PORT))
socket.sendall(b'Voice\n')

model = Model(r'/home/pi/Wall-E/en')
recognizer = KaldiRecognizer(model, 16000)

cap = pyaudio.PyAudio()
stream = cap.open(format= pyaudio.paInt16, channels=1, rate=16000, input=True, input_device_index=1, frames_per_buffer=8192)
stream.start_stream()

while True:
    data = stream.read(4096)
    
    if recognizer.AcceptWaveform(data):
        jsonobject = json.loads(recognizer.Result())
        socket.sendall(bytes(jsonobject["text"],encoding='utf-8'))

