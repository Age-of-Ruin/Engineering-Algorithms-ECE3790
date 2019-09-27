# RSA Encryption
Specifically RSA utilizes a one way hash with a modulus value formed by the multiplication of two large prime numbers (p and q, where p * q=n). The one way nature is due to the fact that it is easy to multiply two big integers but it is difficult to factor a large integer. The steps involved are as follows:

1) Generate 2 large prime numbers (p & q)
2) Generate modulus n (`p*q = n`)
3) Generate public exponent e such that `gcd(e, (p-1)(q-1)) = gcd (e, phi) 1` (ie relatively prime)
4) Generate private exponent d such that `d * e * mod(phi) = 1`

The public key containing the public exponent (e) and the modulus (n) can be sent to communication end points.

Where M=message and C=cyperText:

ENCRYPTION
`C = (M^e) * mod(n)`

DECRYPTION
`M = (C^d) * mod(n)`

Note: The message is first turned into array of bytes and broken into blocks (size = KEY_SIZE/8) - these blocks are iteratively encrypted until the entire message is encrypted. This allows for variable length messages.

### Example:
Say p = 3 and q = 5

n = p * q = 3 * 5 = 15

phi = (p-1) * (q-1) = 2 * 4 = 8

gcd(e, 8) = 1 therefore say e = 3

d = 1 / (e * mod(phi)) = 1 / (3 mod 8) = 3

Now say M = 7...

C = (M^e) * mod(n) = (7^3) mod 15 = 13

M = (C^d) * mod(n) = (13^3) mod 15 = 7