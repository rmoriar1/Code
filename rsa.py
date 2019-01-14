import random
import sys

def main():
    #Alice generates her RSA Pub/Prv key pair
    Prv_A, Pub_A = gen_rsa()
    while Prv_A[1] == 0:
        Prv_A, Pub_A = gen_rsa()
    print("Alice's keys:", Prv_A, Pub_A)
    #Trent generates his RSA Pub/Prv key pair
    Prv_T, Pub_T = gen_rsa()
    while Prv_T[1] == 0:
        Prv_T, Pub_T = gen_rsa()
    print("Trent's keys:", Prv_T, Pub_T)
    #Trent issues Alice a digital certificate
    Certificate = gen_certificate(Prv_T, Pub_A)
    print("Alice's Certificate:", Certificate)
    #Alice authenticates herself to Bob
    Auth_success = authenticate(Prv_A, Pub_A)
    print("Authentication Successful:", Auth_success)

def gen_rsa():
    #generate 5 random bits and add it to 65 (1000001)
    p = 65 + generate_bits(5)
    #loop until prime is found
    while not miller_rabin(p):
        p = 65 + generate_bits(5)
    q = 65 + generate_bits(5)
    #loop until a different prime is found
    while not miller_rabin(q) or q == p:
        q = 65 + generate_bits(5)
    n = p * q
    e, d = find_e_d(p, q)
    #return private and public keys
    return (n, d), (n, e)

def generate_bits(num_of_bits):
    r = 0
    for i in range(num_of_bits, 0, -1): #generate randbits for b(num_of_bits) to b_1
        randint = random.randint(0, 2 ** 32 - 1)
        randbit = randint & 1
        if randbit: #if the bit is equal to one, set it for r
            r = set_bit(r, i)
    return r

def is_set(number, offset):
    mask = 1 << offset
    if (number & mask):
        return True
    return False

def set_bit(number, offset):
    mask = 1 << offset
    return(number | mask)

def miller_rabin(candidate_prime):
    #run primality testing for 20 random r values, if they all pass return (perhaps) true
    for i in range(20):
        a = random.randint(1, candidate_prime - 1)
        if not primality_testing(a, candidate_prime):
            return False
    return True

def primality_testing(a, n):
    x = n - 1
    y = 1
    for i in range((x).bit_length() - 1, -1, -1):
        #on each iteration square the number and check if it = 1 (and isn't 1 or n-1) if so return false
        z = y
        y = (y * y) % n
        if y == 1 and z != 1 and z != n - 1:
            return False
        #if the bit is set multiply by a
        if is_set(x, i):
            y = (y * a) % n
    # if the result isnt 1 return false, if n is prime (a^n-1) should equal 1 by fermat's theorem
    if y != 1:
        return False
    return True

def find_e_d(p, q):
    phi_n = (p-1) * (q-1)
    for e in range(3, phi_n - 1, 2):
        gcd, x, d = extended_euclid(phi_n, e)
        if gcd == 1:
            #return e and d (normalized)
            return e, (phi_n + d) % phi_n
    return 0, 0

def extended_euclid(a, b):
    if b == 0:
        return (a, 1, 0)
    else:
        d_, x_, y_ = extended_euclid(b, a % b)
        d, x, y = d_, y_, x_ - a//b * y_
        return d, x, y

def hash(r):
    result = 0
    #xor all bytes together
    for byte in r:
        result = result ^ byte
    return result

def sign(private_key, base):
    n = private_key[0]
    d = private_key[1]
    #message to the d mod n
    return fast_exponentiation(base, d, n)

def fast_exponentiation(a, x, n):
    y = 1
    k = x.bit_length()
    # k = bits of exponent (prv/pub key)
    for i in range(k-1, -1, -1):
        # square number on each iteration
        y = (y * y) % n
        # if bit is set multiply by a (message)
        if is_set(x, i):
            y = (y * a) % n
    return y

def gen_certificate(Issuer_Prv, Issuee_Pub):
    #bytes 1-6 Alice, 7-10 n, 11-14 e
    r = bytearray(' Alice'.encode()) + Issuee_Pub[0].to_bytes(4, byteorder='big') \
        + Issuee_Pub[1].to_bytes(4, byteorder='big')
    hash_r = hash(r)
    s = sign(Issuer_Prv, hash_r)
    return r, s

def authenticate(Prv_A, Pub_A):
    #k = length of n
    k = Pub_A[0].bit_length()
    #generate k-2 bits
    u = 2 ** (k - 1) + generate_bits(k - 2) + 1
    hash_u = (hash(u.to_bytes(4, byteorder='big')))
    v = sign(Prv_A, hash_u)
    #Bob encrypts with Alices pub key and ensures it equals the hash of the number they generated
    encrypt_v = sign(Pub_A, v)
    #verify signature
    return encrypt_v == hash_u

main()