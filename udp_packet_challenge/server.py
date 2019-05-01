import socket
import time
import binascii
import zlib
import argparse
import re
import sys
from Queue import Queue
from collections import defaultdict
from Crypto.Hash import SHA256
from Crypto.PublicKey.RSA import construct
from Crypto.Signature import PKCS1_v1_5
from threading import Thread, Lock

def main():
    global ver_fail_list
    global crc_fail_list
    global streams
    global sock
    global mutex
    global packet_queue
    parser = argparse.ArgumentParser(description='args: keys dict, binaries dict, delay, port')
    parser.add_argument('--keys', help='a dictionary of {packet_id: key_file_path} mappings', type=str,
                        required=True)
    parser.add_argument('--binaries', help='a dictionary of {packet_id: binary_path} mappings', type=str,
                        required=True)
    parser.add_argument('-d', help='delay, (in seconds) for writing to log files', type=str, required=True)
    parser.add_argument('-p', help='port, to receive packets on', type=str, required=True)
    args = parser.parse_args()
    args.binaries = args.binaries.split(",")
    args.keys = args.keys.split(",")
    delay = int(args.d)
    port = int(args.p)
    streams = defaultdict(list)
    # add to dictionary of streams s.t. stream[packet_id] = [file][pub_key_verifier, e, n]][crc_list]
    for i in range(0,len(args.binaries)):
        packet_id, binary_path = re.sub("[\\\\{}:',\"]", "", args.binaries[i]).split()
        with open(binary_path, 'rb') as file:
            buffr = file.read()
        streams[packet_id].append(buffr)
    for i in range(0,len(args.keys)):
        packet_id, key_file_path = re.sub("[\\\\{}:',\"]", "", args.keys[i]).split()
        with open(key_file_path, 'r') as file:
            buffr = binascii.hexlify(file.read())
            n = long(buffr[0:128], 16)
            e = long(buffr[128:], 16)
            pub_key = construct((n, e))
            verifier = PKCS1_v1_5.new(pub_key)
        streams[packet_id].append([verifier, e, n])
        crc_list = []
        streams[packet_id].append(crc_list)
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind(('127.0.0.1', port))
    ver_fail_list = []
    crc_fail_list = []
    mutex = Lock()
    packet_queue = Queue()
    # init two threads one that writes to log and other that processes from packet queue
    worker1 = Thread(target=write_to_log, args=(delay,))
    worker1.daemon=True
    worker1.start()
    worker2 = Thread(target=get_payload)
    worker2.daemon = True
    worker2.start()
    try:
        while True:
            payload, address = sock.recvfrom(8192)
            packet_queue.put(payload)
    except KeyboardInterrupt:
        sock.close()
        sys.exit()

def write_to_log(delay):
    global ver_fail_list
    global crc_fail_list
    while True:
        f = open("verification_failures.log", "a")
        for i in ver_fail_list:
            f.write(i)
        f.close()
        del ver_fail_list[:]
        f = open("checksum_failures.log", "a")
        for i in crc_fail_list:
            f.write(i)
        f.close()
        del crc_fail_list[:]
        time.sleep(delay)

def get_payload():
    global packet_queue
    while True:
        process_packet(packet_queue.get())

def process_packet(payload):
    global ver_fail_list
    global crc_fail_list
    global streams
    payload_hex = binascii.hexlify(payload)
    packet_id = "0x" + payload_hex[0:8].lstrip("0")
    # check if packet_id is valid
    if packet_id not in streams:
        return
    seq_num = int(payload_hex[8:16], 16)
    xor_key = payload_hex[16:20]
    num_chksums = int(payload_hex[20:24], 16)
    # check if packet size is correct
    if len(payload) != num_chksums * 4 + 76:
        return
    rsa_sig = payload[-64:]
    # check if signature is valid
    msg_hash = SHA256.new()
    msg_hash.update(payload[:-64])
    if not streams[packet_id][1][0].verify(msg_hash, rsa_sig):
        ver_fail_list.append(packet_id + "\n" + str(seq_num) + "\n" +
            hex(pow(int(payload_hex[-128:], 16), streams[packet_id][1][1], streams[packet_id][1][2]))[-65:-1] + "\n" +
                             msg_hash.hexdigest() + "\n\n")
    # check if relevant checksums are already stored, if not generate them
    if len(streams[packet_id][2]) < seq_num + num_chksums:
        gen_crc32(packet_id, seq_num + num_chksums)
    # check if checksums are valid
    count = 0
    for i in range(24, len(payload_hex) - 128, 8):
        crc = payload_hex[i:i+8]
        if int(crc, 16) ^ int(xor_key + xor_key, 16) != streams[packet_id][2][seq_num + count]:
            crc_fail_list.append(packet_id + "\n" + str(seq_num) + "\n" + str(seq_num + count) + "\n" +
                    hex(int(crc, 16) ^ int(xor_key + xor_key, 16))[2:] + "\n" +
                    hex(streams[packet_id][2][seq_num + count])[2:] + "\n\n")
        count += 1

def gen_crc32(packet_id, seq_end):
    mutex.acquire()
    global steam
    file = streams[packet_id][0]
    seq_start = len(streams[packet_id][2])
    if seq_start == 0:
        crcvalue = 0
    else:
        crcvalue = streams[packet_id][2][seq_start-1]
    # generate 1000 beyond seq_end to reduce num of calls to gen_crc32
    for i in range(seq_start, seq_end + 1000):
        crcvalue = zlib.crc32(file, crcvalue)
        if crcvalue < 0:
            crcvalue += 2 ** 32
        streams[packet_id][2].append(crcvalue)
    mutex.release()

main()