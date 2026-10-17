package br.com.devnexus.reduza.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Abre conexões com o banco MySQL.
 *
 * As configurações são lidas, nesta ordem:
 *   1. arquivo reduza.properties na pasta onde o programa é executado;
 *   2. variáveis de ambiente REDUZA_DB_URL, REDUZA_DB_USUARIO e REDUZA_DB_SENHA;
 *   3. valores padrão (MySQL local, banco reduza_db, usuário root sem senha).
 */
public final class ConexaoBanco {

    private static final String URL_PADRAO =
            "jdbc:mysql://localhost:3306/reduza_db?useSSL=false&allowPublicKeyRetrieval=true"
            + "&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8";

    private static String url;
    private static String usuario;
    private static String senha;

    private ConexaoBanco() {
    }

    /** Carrega a configuração uma única vez. */
    public static synchronized void carregar() {
        if (url != null) {
            return;
        }
        Properties p = new Properties();
        File arquivo = new File("reduza.properties");
        if (arquivo.isFile()) {
            try (InputStream in = new FileInputStream(arquivo)) {
                p.load(in);
            } catch (IOException e) {
                System.err.println("Não foi possível ler reduza.properties: " + e.getMessage());
            }
        }
        url = escolher(p.getProperty("db.url"), System.getenv("REDUZA_DB_URL"), URL_PADRAO);
        usuario = escolher(p.getProperty("db.usuario"), System.getenv("REDUZA_DB_USUARIO"), "root");
        senha = escolher(p.getProperty("db.senha"), System.getenv("REDUZA_DB_SENHA"), "");
    }

    private static String escolher(String... opcoes) {
        for (String o : opcoes) {
            if (o != null) {
                return o;
            }
        }
        return null;
    }

    /** Abre uma nova conexão. Quem chama deve fechá-la (try-with-resources). */
    public static Connection abrir() throws SQLException {
        carregar();
        return DriverManager.getConnection(url, usuario, senha);
    }

    /** Usado na tela de erro para mostrar onde o sistema tentou conectar. */
    public static String descricao() {
        carregar();
        String semParametros = url.contains("?") ? url.substring(0, url.indexOf('?')) : url;
        return semParametros + " (usuário " + usuario + ")";
    }
}
