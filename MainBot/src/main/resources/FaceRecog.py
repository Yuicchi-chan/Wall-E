import time
import cv2
import sys
import socket

cascPath = sys.argv[1]
faceCascade = cv2.CascadeClassifier(cascPath)

HOST = "192.168.1.17"
PORT = 25555

socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.connect((HOST, PORT))
socket.sendall(b'Face\n')

video_capture = cv2.VideoCapture(0)

while True:
    # Capture frame-by-frame
    ret, frame = video_capture.read()

    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    faces = faceCascade.detectMultiScale(
        gray,
        scaleFactor=1.1,
        minNeighbors=5,
        minSize=(30, 30),
        flags=cv2.CASCADE_SCALE_IMAGE
    )
    if faces == ():
        continue

    for(x,y,w,h) in faces:
        print(x, y)
        socket.sendall(bytes(f'{x}:{y}\n', encoding='utf-8'))