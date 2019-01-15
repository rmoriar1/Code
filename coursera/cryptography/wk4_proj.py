import urllib.request
import urllib.error
import urllib.parse
import sys
TARGET = 'http://crypto-class.appspot.com/po?er='
class PaddingOracle(object):
    def query(self, q):
        target = TARGET + urllib.parse.quote(q)    # Create query URL
        try:
            req = urllib.request.urlopen(target)  # Send HTTP request to server
        except urllib.error.HTTPError as e:
            #print ("We got: %d" % e.code)       # Print response code
            if e.code == 404:
                return True # good padding
            return False # bad padding
tgt = "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4"
length = len(tgt)
ans = ""
po = PaddingOracle()
for i in range (length - 32, 0, -32):
    block = tgt[i-32: i]
    sum = 0
    for j in range (1, 17):
        pad = j * chr(j)
        for g in range (255, -1, -1):
            ct = hex((g * (256**(j-1))) ^ int.from_bytes(pad.encode(), sys.byteorder) ^ int(block,16) ^ sum)[2:] + tgt[i: i+32]
            if po.query(ct):
                sum += g * (256**(j-1))
                ans = chr(g) + ans
                break
print(ans)

