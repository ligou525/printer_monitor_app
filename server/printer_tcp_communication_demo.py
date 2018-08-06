import time
import socket
import json

# 0 functions (The following three functions cannot be modified)
def message_builder(sender, recver, msg_type, msg_data):
    '''package message
    
    sender:
        name of sender, can be any string prefixed with "app", "server" or "printer"

    recver:
        receiver of the message, can be any string prefixed with "app", "server" or "printer"
        
    messageType: can be the following options
        Status: update printer's status, from  printer to app
        Alert: notify app if the printing goes wrong, from printer to app
        Online: register sender on the server, from printer/app to server
        Off: shutdown the printer, from app to printer
        Stop: stop printing (not shutdown), from app to printer
        Continue: continue printing, from app to printer

    messageContent:
        JSON-serializable data: string, file, image, etc.
    '''
    
    return {'sender': sender, 
            'recver': recver,
            'messageType': msg_type,
            'messageContent': msg_data}


def send(socket, data):
    try:
        serialized = json.dumps(data).encode()
    except (TypeError, ValueError) as e:
        raise Exception('You can only send JSON-serializable data')
    # send the length of the serialized data first
    socket.send('{0}\n'.format(len(serialized)).encode())
    # send the serialized data
    socket.sendall(serialized)


def recv(socket):
    # read the length of the data, letter by letter until we reach EOL
    length_str = b''
    char = socket.recv(1)
    while char != b'\n':
        length_str += char
        char = socket.recv(1)
    total = int(length_str)
    # use a memoryview to receive the data chunk by chunk efficiently
    view = memoryview(bytearray(total)) 
    next_offset = 0
    while total - next_offset > 0:
        recv_size = socket.recv_into(view[next_offset:], total - next_offset)
        next_offset += recv_size
    try:
        deserialized = json.loads(view.tobytes().decode())
    except (TypeError, ValueError) as e:
        raise Exception('Data received was not in JSON format')
    return deserialized



# 1 initialize tcp socket
s = socket.socket()
s.connect(('106.12.17.74', 8010))   

# 2 send online notification to server
online_msg = message_builder('printer323', 'server', 'Online', None)
send(s, online_msg)

# 3 start a permanent loop to send status to apps
while 1:
    
    # package and send message
    #tmp_msg =  message_builder('printer0', 'apps', 'Status', 'any object here: image, string, video, etc. ')
    tmp_msg =  message_builder('printer323', 'server', 'Test', 'test ')
    send(s, tmp_msg)

    # receive message
    recved = recv(s)
    print(recved)
        

    time.sleep(30)


s.close()

