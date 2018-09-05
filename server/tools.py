import json


server_addr = ('106.12.17.74', 8010)



def message_builder(sender, recver, msg_type, msg_data):
    '''package message
    
    sender:
        name of sender, can be any string prefixed with "app", "server" or "printer"

    recver:
        receiver of the message, can be any string prefixed with "app", "server" or "printer"
        
    messageType: can be the following options
        PrinterStatus: update printer's status, from printer to app
        Online: register sender on the server, from printer/app to server
        Shutdown: shutdown the printer, from app to printer
        Stop: stop printing (not shutdown), from app to printer
        Continue: continue printing, from app to printer
        UpdateList: update printer list, from server to app
        UpdateInterval: change the interval to update printing status

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
    socket.send('{0}\n'.format(len(serialized)).encode('utf-8'))
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
        deserialized = json.loads(view.tobytes().decode('utf-8'))
    except (TypeError, ValueError) as e:
        raise Exception('Data received was not in JSON format')
    return deserialized
