package br.com.devnexus.reduza.servico;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash de senha com PBKDF2-HMAC-SHA256, disponível na biblioteca padrão do Java.
 * Formato gravado no banco: pbkdf2_sha256$iteracoes$sal$hash (sal e hash em Base64).
 */
public final class Senhas {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACOES = 120_000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private Senhas() {
    }

    public static String gerarHash(char[] senha) {
        byte[] sal = new byte[BYTES_SAL];
        ALEATORIO.nextBytes(sal);
        byte[] hash = derivar(senha, sal, ITERACOES);
        Base64.Encoder b64 = Base64.getEncoder();
        return "pbkdf2_sha256$" + ITERACOES + "$" + b64.encodeToString(sal) + "$" + b64.encodeToString(hash);
    }

    public static boolean confere(char[] senha, String armazenado) {
        if (armazenado == null) {
            return false;
        }
        String[] p = armazenado.split("\\$");
        if (p.length != 4 || !p[0].equals("pbkdf2_sha256")) {
            return false;
        }
        try {
            int iteracoes = Integer.parseInt(p[1]);
            byte[] sal = Base64.getDecoder().decode(p[2]);
            byte[] esperado = Base64.getDecoder().decode(p[3]);
            byte[] obtido = derivar(senha, sal, iteracoes);
            return MessageDigest.isEqual(esperado, obtido); // comparação em tempo constante
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /** Regra mínima: 8 caracteres, com letra e número. */
    public static String problemaNaSenha(char[] senha) {
        if (senha.length < 8) {
            return "A senha precisa ter pelo menos 8 caracteres.";
        }
        boolean letra = false;
        boolean numero = false;
        for (char c : senha) {
            letra |= Character.isLetter(c);
            numero |= Character.isDigit(c);
        }
        return letra && numero ? null : "A senha precisa ter letras e números.";
    }

    private static byte[] derivar(char[] senha, byte[] sal, int iteracoes) {
        PBEKeySpec spec = new PBEKeySpec(senha, sal, iteracoes, BITS_HASH);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("PBKDF2 indisponível nesta versão do Java", e);
        } finally {
            spec.clearPassword();
        }
    }

    /** Utilitário de linha de comando: imprime o hash de uma senha (útil para o script SQL). */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java br.com.devnexus.reduza.servico.Senhas <senha>");
            return;
        }
        char[] s = args[0].toCharArray();
        System.out.println(gerarHash(s));
        Arrays.fill(s, '\0');
    }
}
