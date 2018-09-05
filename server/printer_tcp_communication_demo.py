import time
import socket
import json
import base64
from tools import message_builder, recv, send

while 1:
    # 1 initialize tcp socket
    s = socket.socket()
    try:
        s.connect(('106.12.17.74', 8010))   
    except:
        time.sleep(3)
        continue
    # 2 send online notification to server
    online_msg = message_builder('printer323', 'server', 'Online', None)
    send(s, online_msg)

    # 3 start a permanent loop to send status to apps
    while 1:
        
        # package and send message
        image_binary = open('test.png',"rb").read()
        image_64 = base64.encodebytes(image_binary)
        image_str = image_64.decode('utf-8')
        status_dict = {'statusImg': image_str, 'statusText': 'Printing abnormally', 'statusCode': 1}
        tmp_msg =  message_builder('printer322223', 'app', 'PrinterStatus', status_dict)
        # tmp_msg =  message_builder('printer322223', 'server', 'Test', 'test')
        send(s, tmp_msg)

        # receive message
#        recved = recv(s)
#        print(recved)
#        if recved['messageType'] == 'Stop':
#            tmp_msg =  message_builder('printer322223', 'app', 'Stop', 'OK :( I\'m stopped...')
#            send(s, tmp_msg)

        time.sleep(5)

        image_binary = open('t1.jpg',"rb").read()
        image_64 = base64.encodebytes(image_binary)
        image_str = image_64.decode('utf-8')
        status_dict = {'statusImg': image_str, 'statusText': 'Printing normally', 'statusCode': 0}
        tmp_msg =  message_builder('printer322223', 'app', 'PrinterStatus', status_dict)
        # tmp_msg =  message_builder('printer322223', 'server', 'Test', 'test')
        send(s, tmp_msg)

        time.sleep(5)

    s.close()

