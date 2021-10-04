package xyz.pwmw.mynlife.util;

import lombok.Getter;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

@Getter
public class KeyCreator {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final String publicKeyModulus;
    private final String publicKeyExponent;

    public KeyCreator() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.genKeyPair();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
        this.publicKeyModulus = publicSpec.getModulus().toString(16);
        this.publicKeyExponent = publicSpec.getPublicExponent().toString(16);
    }

}
