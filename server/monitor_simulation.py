import time
import socket
from tools import s_addr
s_addr = ('106.12.17.74', 8010)


def perform_cmds(cmds):
    print('\tPerforming cmds:', cmds)
    print('\tFinished cmds:', cmds)
    return 'result:finished\n'

while 1:
    s = socket.socket()
    try:
        s.connect(s_addr)
    except:
        time.sleep(1)
        continue

    s.sendall('monitor'.encode())
    print('Connected to: ', s_addr)

    while 1:
        s.sendall('Progressing properly...'.encode())
        s.sendall('Problem detected, please choose an action'.encode())
        recv = s.recv(1024).strip()
        print(type(recv), recv)
        s.sendall('Action received, executing'.encode())

    s.sendall('exit\n'.encode())
    s.close()
