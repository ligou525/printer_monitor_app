import sys
import socketserver
import socket
import threading
import time
from time import strftime
from tools import send, recv, message_builder, server_addr

class TCPHandler(socketserver.StreamRequestHandler):
    """
    The request handler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """

    def handle(self):
        try:
            msg = recv(self.request)
        except:
            return 

        if msg['messageType'] == 'Online':
            name = msg['sender']
            self.server.add_client(name, self.request)
            if name.startswith('app'):
                response = message_builder('server', name, 'UpdatePrinterList', self.server.printers)
                self.server.send_mgs(response)
        
        while 1:
            try:
                msg = recv(self.request)
                print("{0} - recevied message".format(strftime('%c')))
            except:
                break
                    
            if msg['recver'] == 'server':
                if msg['messageType'] == 'Test':
                    response = message_builder('server', name, 'Test', 'test - {0}'.format(strftime('%c')))
                    self.server.send_msg(response)

            else:
                self.server.send_msg(msg)

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
    
    def send_msg(self, msg):
        with self.lock:
            recver = msg['recver'] 
            if recver.startswith('app') and recver in self.apps:
                send(self.apps[recver], msg)
            elif recver.startswith('printer') and recver in self.printers:
                send(self.printers[recver], msg)



def main():
    vcs_server = Server((socket.gethostname(), server_addr[1]), TCPHandler)
    vcs_server.serve_forever()


if __name__ == '__main__':
    main()
