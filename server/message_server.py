import sys
import socketserver
import socket
import threading
import time
from time import strftime


class TCPHandler(socketserver.StreamRequestHandler):
    """
    The request handler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """

    def handle(self):
        try:
            msg = _recv(self.request)
        except:
            pass
        
        if msg['msgType'] == 'online':
            name = msg['sender']
            self.server.add_client(name, self.request)
            response = message_builder('server', name, 'UpdatePrinterList', self.server.printers)
            self.server.send(response)
        
        while 1:
            try:
                msg = _recv(self.request)
            except:
                break
                    
            if msg['recver'] == 'server':
                pass

            else:
                self.server.send(msg)

        self.server.remove_client(name)


class Server(socketserver.ThreadingTCPServer):

    def __init__(self, *args, **kwargs):
        super(Server, self).__init__(*args, **kwargs)
        print("{0} - Server started".format(strftime('%c')))
        self.apps = {}
        self.printers = {}
        self.lock = threading.Lock()

    def print_clients(self):
        print("{0} - Connected apps -".format(strftime('%c')), list(self.apps))
        print("{0} - Connected printers -".format(strftime('%c')), list(self.printers))

    def add_client(self, name, client):
        with self.lock:
            if name.startswith('app'):
                self.apps[name] = client
            elif name.startswith('printer'):
                self.printers[name] = client
            
            print("\n{0} - Connected - {1} from {2}".format(
                strftime('%c'), name, client.getpeername()))
            self.print_clients()

    def remove_client(self, name):
        with self.lock:
            if name.startswith('app'):
                client =  self.apps[name]
                del self.apps[name]
            elif name.startswith('printer'):
                client = self.printers[name]
                del self.printers[name]

            print("\n{0} - Disconnected - {1} from {2}".format(
                strftime('%c'), name, client.getpeername()))
            self.print_clients()
    
    def send(self, msg):
        with self.lock:
            recver = msg['recver'] 
            if recver.startswith('app') and recver in self.apps:
                _send(self.apps[recver], msg)
            elif recver.startswith('printer') and recver in self.printers:
                _send(self.printers[recver], msg)


def message_builder(sender, recver, msg_type, msg_data):
    return {'sender': sender, 
            'recver': recver,
            'msgType': msg_type,
            'msgData': msg_data}


def _send(socket, data):
    try:
        serialized = json.dumps(data).encode()
    except (TypeError, ValueError) as e:
        raise Exception('You can only send JSON-serializable data')
    # send the length of the serialized data first
    socket.send('{0}\n'.format(len(serialized)).encode())
    # send the serialized data
    socket.sendall(serialized)


def _recv(socket):
    # read the length of the data, letter by letter until we reach EOL
    length_str = ''
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
        deserialized = json.loads(view.tobytes())
    except (TypeError, ValueError) as e:
        raise Exception('Data received was not in JSON format')
    return deserialized


def main():
    vcs_server = Server((socket.gethostname(), 8010), TCPHandler)
    vcs_server.serve_forever()


if __name__ == '__main__':
    main()
