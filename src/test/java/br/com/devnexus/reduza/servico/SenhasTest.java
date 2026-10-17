package br.com.devnexus.reduza.servico;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SenhasTest {

    @Test
    void hashConfereSoComASenhaCerta() {
        String h = Senhas.gerarHash("reduza123".toCharArray());
        assertTrue(h.startsWith("pbkdf2_sha256$"));
        assertTrue(Senhas.confere("reduza123".toCharArray(), h));
        assertFalse(Senhas.confere("reduza124".toCharArray(), h));
    }

    @Test
    void mesmaSenhaGeraHashesDiferentes() {
        assertNotEquals(Senhas.gerarHash("abc12345".toCharArray()), Senhas.gerarHash("abc12345".toCharArray()));
    }

    @Test
    void hashDoScriptSqlConfere() {
        String doScript = "pbkdf2_sha256$120000$KkrYQjpH8O+spzcbbgDGxg==$WWEPlaZAIv3PxO9412Pl4m4XzWhj5LzUXObA8AmnUDk=";
        assertTrue(Senhas.confere("reduza123".toCharArray(), doScript));
    }

    @Test
    void formatoInvalidoNaoConfere() {
        assertFalse(Senhas.confere("x".toCharArray(), "texto-qualquer"));
        assertFalse(Senhas.confere("x".toCharArray(), null));
    }

    @Test
    void regraMinimaDeSenha() {
        assertNotNull(Senhas.problemaNaSenha("abc1".toCharArray()));
        assertNotNull(Senhas.problemaNaSenha("abcdefgh".toCharArray()));
        assertNull(Senhas.problemaNaSenha("reduza2026".toCharArray()));
    }
}
