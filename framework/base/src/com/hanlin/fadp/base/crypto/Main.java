
package com.hanlin.fadp.base.crypto;

import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.crypto.AesCipherService;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args[0].equals("-crypt")) {
            System.out.println(HashCrypt.cryptUTF8(args[1], null, args[2]));
        } else if (args[0].equals("-digest")) {
            @SuppressWarnings("deprecation")
            String digest = HashCrypt.getDigestHash(args[1]);
            System.out.println(digest);
        } else if (args[0].equals("-kek")) {
            AesCipherService cs = new AesCipherService();
            System.out.println(Base64.encodeBase64String(cs.generateNewKey().getEncoded()));
        } else if (args[0].equals("-kek-old")) {
            System.out.println(Base64.encodeBase64String(DesCrypt.generateKey().getEncoded()));
        }
    }
}
