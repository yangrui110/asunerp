/**
 * Created by cl on 2017/1/19.
 */
(function($){
    $.md5 = function(o) {
        if(null === o) {
            return 'null';
        }
        if(typeof o != "string") {
            return 'null';
        }
        //计算填充的长度
        var fill_data_len = 0;
        var data_len = o.length;
        var d_l_mod = data_len % MD5_BASE_LEN;
        if(0 != d_l_mod)
            fill_data_len = MD5_BASE_LEN - d_l_mod;
        if(fill_data_len < 8)
            fill_data_len += MD5_BASE_LEN;
        var buf = new Array(data_len + fill_data_len);
        for(var i = 0;i < data_len;i++)
            buf[i] = o.charCodeAt(i);
        var msg_bit_len = data_len * BYTE_BIT_LEN
        buf[data_len] = FIRST_FILL_BYTE;
        for(var i = 0;i < 4;i++) {
            buf[data_len + fill_data_len - 8 + i] =
                ((msg_bit_len & (0x000000ff << (i * 8)))
                >> (i * 8));
        }
        var md5_c = [MD5_A,MD5_B,MD5_C,MD5_D];
        var md5_code = [MD5_A,MD5_B,MD5_C,MD5_D];
        for(var m = 0;m < data_len + fill_data_len;m += 64) {
            var buf_p = new Array(16);
            for(var i = 0;i < 16;i++) {
                buf_p[i] = 0;
                for(var j = 0;j < 4;j++) {
                    buf_p[i] <<= 8;
                    buf_p[i] |= buf[m + j + i * 4];
                }
            }
            for(var k = 0;k < 16;k += 4) {
                md5_c[0] = FF(md5_c[0],md5_c[1],md5_c[2],
                    md5_c[3],buf_p[k],7,md5_ti[k]);
                md5_c[3] = FF(md5_c[3],md5_c[0],md5_c[1],
                    md5_c[2],buf_p[k + 1],12,
                    md5_ti[k + 1]);
                md5_c[2] = FF(md5_c[2],md5_c[3],md5_c[0],
                    md5_c[1],buf_p[k + 2],17,
                    md5_ti[k + 2]);
                md5_c[1] = FF(md5_c[1],md5_c[2],md5_c[3],
                    md5_c[0],buf_p[k + 3],22,
                    md5_ti[k + 3]);
            }
            md5_c[0] = GG(md5_c[0],md5_c[1],md5_c[2],md5_c[3],
                buf_p[1],5,md5_ti[16]);
            md5_c[3] = GG(md5_c[3],md5_c[0],md5_c[1],md5_c[2],
                buf_p[6],9,md5_ti[17]);
            md5_c[2] = GG(md5_c[2],md5_c[3],md5_c[0],md5_c[1],
                buf_p[11],14,md5_ti[18]);
            md5_c[1] = GG(md5_c[1],md5_c[2],md5_c[3],md5_c[0],
                buf_p[0],20,md5_ti[19]);

            md5_c[0] = GG(md5_c[0],md5_c[1],md5_c[2],md5_c[3],
                buf_p[5],5,md5_ti[20]);
            md5_c[3] = GG(md5_c[3],md5_c[0],md5_c[1],md5_c[2],
                buf_p[10],9,md5_ti[21]);
            md5_c[2] = GG(md5_c[2],md5_c[3],md5_c[0],md5_c[1],
                buf_p[15],14,md5_ti[22]);
            md5_c[1] = GG(md5_c[1],md5_c[2],md5_c[3],md5_c[0],
                buf_p[4],20,md5_ti[23]);

            md5_c[0] = GG(md5_c[0],md5_c[1],md5_c[2],md5_c[3],
                buf_p[9],5,md5_ti[24]);
            md5_c[3] = GG(md5_c[3],md5_c[0],md5_c[1],md5_c[2],
                buf_p[14],9,md5_ti[25]);
            md5_c[2] = GG(md5_c[2],md5_c[3],md5_c[0],md5_c[1],
                buf_p[3],14,md5_ti[26]);
            md5_c[1] = GG(md5_c[1],md5_c[2],md5_c[3],md5_c[0],
                buf_p[8],20,md5_ti[27]);

            md5_c[0] = GG(md5_c[0],md5_c[1],md5_c[2],md5_c[3],
                buf_p[13],5,md5_ti[28]);
            md5_c[3] = GG(md5_c[3],md5_c[0],md5_c[1],md5_c[2],
                buf_p[2],9,md5_ti[29]);
            md5_c[2] = GG(md5_c[2],md5_c[3],md5_c[0],md5_c[1],
                buf_p[7],14,md5_ti[30]);
            md5_c[1] = GG(md5_c[1],md5_c[2],md5_c[3],md5_c[0],
                buf_p[12],20,md5_ti[31]);

            md5_c[0] = HH(md5_c[0],md5_c[1],md5_c[2],md5_c[3],
                buf_p[5],4,md5_ti[32]);
            md5_c[3] = HH(md5_c[3],md5_c[0],md5_c[1],md5_c[2],
                buf_p[8],11,md5_ti[33]);
            md5_c[2] = HH(md5_c[2],md5_c[3],md5_c[0],md5_c[1],
                buf_p[11],16,md5_ti[34]);
            md5_c[1] = HH(md5_c[1],md5_c[2],md5_c[3],md5_c[0],
                buf_p[14],23,md5_ti[35]);

            md5_c[0] = HH(md5_c[0],md5_c[1],md5_c[2],md5_c[3],
                buf_p[1],4,md5_ti[36]);
            md5_c[3] = HH(md5_c[3],md5_c[0],md5_c[1],md5_c[2],
                buf_p[4],11,md5_ti[37]);
            md5_c[2] = HH(md5_c[2],md5_c[3],md5_c[0],md5_c[1],
                buf_p[7],16,md5_ti[38]);
            md5_c[1] = HH(md5_c[1],md5_c[2],md5_c[3],md5_c[0],
                buf_p[10],23,md5_ti[39]);

            md5_c[0] = HH(md5_c[0],md5_c[1],md5_c[2],md5_c[3],
                buf_p[13],4,md5_ti[40]);
            md5_c[3] = HH(md5_c[3],md5_c[0],md5_c[1],md5_c[2],
                buf_p[0],11,md5_ti[41]);
            md5_c[2] = HH(md5_c[2],md5_c[3],md5_c[0],md5_c[1],
                buf_p[3],16,md5_ti[42]);
            md5_c[1] = HH(md5_c[1],md5_c[2],md5_c[3],md5_c[0],
                buf_p[6],23,md5_ti[43]);

            md5_c[0] = HH(md5_c[0],md5_c[1],md5_c[2],md5_c[3],
                buf_p[9],4,md5_ti[44]);
            md5_c[3] = HH(md5_c[3],md5_c[0],md5_c[1],md5_c[2],
                buf_p[12],11,md5_ti[45]);
            md5_c[2] = HH(md5_c[2],md5_c[3],md5_c[0],md5_c[1],
                buf_p[15],16,md5_ti[46]);
            md5_c[1] = HH(md5_c[1],md5_c[2],md5_c[3],md5_c[0],
                buf_p[2],23,md5_ti[47]);

            md5_c[0] = II(md5_c[0],md5_c[1],md5_c[2],md5_c[3],
                buf_p[0],6,md5_ti[48]);
            md5_c[3] = II(md5_c[3],md5_c[0],md5_c[1],md5_c[2],
                buf_p[7],10,md5_ti[49]);
            md5_c[2] = II(md5_c[2],md5_c[3],md5_c[0],md5_c[1],
                buf_p[14],15,md5_ti[50]);
            md5_c[1] = II(md5_c[1],md5_c[2],md5_c[3],md5_c[0],
                buf_p[5],21,md5_ti[51]);

            md5_c[0] = II(md5_c[0],md5_c[1],md5_c[2],md5_c[3],
                buf_p[12],6,md5_ti[52]);
            md5_c[3] = II(md5_c[3],md5_c[0],md5_c[1],md5_c[2],
                buf_p[3],10,md5_ti[53]);
            md5_c[2] = II(md5_c[2],md5_c[3],md5_c[0],md5_c[1],
                buf_p[10],15,md5_ti[54]);
            md5_c[1] = II(md5_c[1],md5_c[2],md5_c[3],md5_c[0],
                buf_p[1],21,md5_ti[55]);

            md5_c[0] = II(md5_c[0],md5_c[1],md5_c[2],md5_c[3],
                buf_p[8],6,md5_ti[56]);
            md5_c[3] = II(md5_c[3],md5_c[0],md5_c[1],md5_c[2],
                buf_p[15],10,md5_ti[57]);
            md5_c[2] = II(md5_c[2],md5_c[3],md5_c[0],md5_c[1],
                buf_p[6],15,md5_ti[58]);
            md5_c[1] = II(md5_c[1],md5_c[2],md5_c[3],md5_c[0],
                buf_p[13],21,md5_ti[59]);

            md5_c[0] = II(md5_c[0],md5_c[1],md5_c[2],md5_c[3],
                buf_p[4],6,md5_ti[60]);
            md5_c[3] = II(md5_c[3],md5_c[0],md5_c[1],md5_c[2],
                buf_p[11],10,md5_ti[61]);
            md5_c[2] = II(md5_c[2],md5_c[3],md5_c[0],md5_c[1],
                buf_p[2],15,md5_ti[62]);
            md5_c[1] = II(md5_c[1],md5_c[2],md5_c[3],md5_c[0],
                buf_p[9],21,md5_ti[63]);

            for(var i = 0;i < 4;i++) {
                md5_code[i] += md5_c[i];
            }
            for(var i = 0;i < 4;i++) {
                md5_c[i] = md5_code[i];
            }
        }
        var md5_code_str =  "" + md5_code[0];
        for(var i = 1;i < 4;i++)
            md5_code_str += md5_code[i];
        return md5_code_str;
    };
}(jQuery));

function F(x,y,z)
{
    return (((x) & (y)) | ((~(x)) & (z)));
}

function G(x,y,z)
{
    return (((x) & (z)) | ((y) & (~(z))));
}

function H(x,y,z)
{
    return ((x) ^ (y) ^ (z));
}

function I(x,y,z)
{
    return ((y) ^ ((x) | (~(z))));
}

function bit_rol(d,n)
{
    return (((d) <<  (n)) | ((d) >> (32 - (n))));
}

function FF(a,b,c,d,m,s,t)
{
    return b + bit_rol((a + F(b,c,d) + m + t),s);
}

function GG(a,b,c,d,m,s,t)
{
    return b + bit_rol((a + G(b,c,d) + m + t),s);
}

function HH(a,b,c,d,m,s,t)
{
    return b + bit_rol((a + H(b,c,d) + m + t),s);
}

function II(a,b,c,d,m,s,t)
{
    return b + bit_rol((a + I(b,c,d) + m + t),s);
}

var md5_ti = [
    0xd76aa478,0xe8c7b756,0x242070db,0xc1bdceee,
    0xf57c0faf,0x4787c62a,0xa8304613,0xfd469501,
    0x698098d8,0x8b44f7af,0xffff5bb1,0x895cd7be,
    0x6b901122,0xfd987193,0xa679438e,0x49b40821,
    0xf61e2562,0xc040b340,0x265e5a51,0xe9b6c7aa,
    0xd62f105d,0x02441453,0xd8a1e681,0xe7d3fbc8,
    0x21e1cde6,0xc33707d6,0xf4d50d87,0x455a14ed,
    0xa9e3e905,0xfcefa3f8,0x676f02d9,0x8d2a4c8a,
    0xfffa3942,0x8771f681,0x6d9d6122,0xfde5380c,
    0xa4beea44,0x4bdecfa9,0xf6bb4b60,0xbebfbc70,
    0x289b7ec6,0xeaa127fa,0xd4ef3085,0x04881d05,
    0xd9d4d039,0xe6db99e5,0x1fa27cf8,0xc4ac5665,
    0xf4292244,0x432aff97,0xab9423a7,0xfc93a039,
    0x655b59c3,0x8f0ccc92,0xffeff47d,0x85845dd1,
    0x6fa87e4f,0xfe2ce6e0,0xa3014314,0x4e0811a1,
    0xf7537e82,0xbd3af235,0x2ad7d2bb,0xeb86d391
];

var BYTE_BIT_LEN = 8;
var MD5_BASE_LEN = 64;
var MD5_A = 0x67452301;
var MD5_B = 0xefcdab89;
var MD5_C = 0x98badcfe;
var MD5_D = 0x10325476;
var FIRST_FILL_BYTE = 0x80;