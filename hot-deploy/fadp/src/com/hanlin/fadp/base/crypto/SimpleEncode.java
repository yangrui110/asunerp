package com.hanlin.fadp.base.crypto;

import java.nio.charset.Charset;

import com.hanlin.fadp.base.util.Base64;

public class SimpleEncode {
    private static final Charset charset = Charset.forName("UTF-8");  
      
    public static String encode(String enc,byte[] keyBytes){  
        byte[] b = enc.getBytes(charset);  
        for(int i=0,size=b.length;i<size;i++){  
            for(byte keyBytes0:keyBytes){  
                b[i] = (byte) (b[i]^keyBytes0);  
            }  
        } 
        
        return new String(Base64.base64Encode(b));  
    }  
      
    public static String decode(String dec,byte[] keyBytes){  
        byte[] e = Base64.base64Decode(dec.getBytes(charset));  
        byte[] dee = e;  
        for(int i=0,size=e.length;i<size;i++){  
            for(byte keyBytes0:keyBytes){  
                e[i] = (byte) (dee[i]^keyBytes0);  
            }  
        }  
        return new String(e);  
    }  

    public static void main(String[] args) {  
        String s="root";  
        String key="1qaz2wsx,.";
        String enc = encode(s,key.getBytes());  
        String dec = decode(enc,key.getBytes());  
        System.out.println(enc);  
        System.out.println(dec);  
        key="admin";
         enc = encode(s,key.getBytes());  
         dec = decode(enc,key.getBytes());  
        System.out.println(enc);  
        System.out.println(dec);  
        
        System.out.println(encode("13141cx","1qaz2wsx,.".getBytes()));
        System.out.println(encode("hello","ofbiz".getBytes()));
    }
}
