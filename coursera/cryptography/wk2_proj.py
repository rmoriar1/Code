from Crypto.Cipher import AES
from Crypto import Random
from Crypto.Util import Counter
from binascii import unhexlify
from binascii import hexlify

bs = 16
pad = lambda s: s + (BS - len(s) % BS) * chr(BS - len(s) % BS)
def unpad(msg):
    return msg[:len(msg) - int(msg[-2:], 10) * 2]

class CBCCipher:
    def __init__( self, key ):
        self.key = unhexlify(key)

    def encrypt( self, pt):
        pt = pad(pt)
        cipher = AES.new(self.key)
        for start in range(bs, len(pt), bs):
            end = start + bs
            pt[start:end] = cipher.encrypt(pt[start - 16 : start] ^ pt[start : end])
        return unpad(pt)

    def decrypt( self, ct):
        ct = unhexlify(ct)
        iv = ct[:16]
        cipher = AES.new(self.key)
        msg = ""
        for start in range(bs, len(ct), bs):
            end = start + bs
            a = cipher.decrypt(ct[start:end])
            b = ct[start - 16: start]
            msg += (hex(int(a.hex(), 16) ^ int(b.hex(), 16)))[2:]
        return unpad(msg)

# CBC mode decryption
cbc_key = "140b41b22a29beb4061bda66b6747e14"
cbc_ciphers = ["4ca00ff4c898d61e1edbf1800618fb2828a226d160dad07883d04e008a7897ee" +
               "2e4b7465d5290d0c0e6c6822236e1daafb94ffe0c5da05d9476be028ad7c1d81",
               "5b68629feb8606f9a6667670b75b38a5b4832d0f26e1ab7da33249de7d4afc48" +
               "e713ac646ace36e872ad5fb8a512428a6e21364b0c374df45503473c5242a253"]

for c in cbc_ciphers:
    cipher = CBCCipher(cbc_key)
    print(bytearray.fromhex(cipher.decrypt(c)).decode())

class CTRCipher:
    def __init__( self, key ):
        self.key = unhexlify(key)

    def encrypt( self, pt):
        pt = unhexlify(pt)
        iv = Random.new().read(16)
        iv = iv.hex()
        print(iv)
        cipher = AES.new(self.key)
        ct = ""
        for start in range(0, len(pt), bs):
            end = start + bs
            a = cipher.encrypt(iv)
            b = pt[start: end]
            a = a[16:]
            print("CIPHER ENC: " + a.hex())
            #print("pt:" + b.hex())
            #print("ivenc:" + a.hex())
            ct += (hex(int(a.hex(), 16) ^ int(b.hex(), 16)))[2:]
            iv = hex(int(iv, 16) + 1)[2:]
        return (iv, ct)

    def decrypt( self, ct):
        print(len(ct))
        ct = unhexlify(ct)
        iv = ct[:bs].hex()
        iv = hex(int(iv, 16) - (len(ct)//16) + 1)[2:]
        print(iv)
        #print("iv:" + iv)
        cipher = AES.new(self.key)
        #print(cipher.encrypt(iv).hex())
        msg = ""
        for start in range(bs, len(ct), bs):
            end = start + bs
            a = cipher.encrypt(iv)
            b = ct[start: end]
            a = a[16:]
            print("CIPHER DEC: " + a.hex())
            #print("ct:" + b.hex())
            #print("ivenc:" + a.hex())
            msg += (hex(int(a.hex(), 16) ^ int(b.hex(), 16)))[2:]
            iv = hex(int(iv, 16) + 1)[2:]
        return msg


# Counter mode decryption
ctr_key = "36f18357be4dbd77f050515c73fcf9f2"
ctr_ciphers = ["69dda8455c7dd4254bf353b773304eec0ec7702330098ce7f7520d1cbbb20fc3" +
               "88d1b0adb5054dbd7370849dbf0b88d393f252e764f1f5f7ad97ef79d59ce29f5f51eeca32eabedd9afa9329",
               "770b80259ec33beb2561358a9f2dc617e46218c0a53cbeca695ae45faa8952aa" +
               "0e311bde9d4e01726d3184c34451"]

for c in ctr_ciphers:
    cipher = CTRCipher(cbc_key)
    print(cipher.decrypt(c))
