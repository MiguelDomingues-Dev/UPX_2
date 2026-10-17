package br.com.devnexus.reduza.dao;

import java.sql.SQLException;

/** Falha de banco com uma mensagem que o usuário entende. */
public class ErroBanco extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErroBanco(SQLException causa) {
        super(traduzir(causa), causa);
    }

    private static String traduzir(SQLException e) {
        String estado = e.getSQLState() == null ? "" : e.getSQLState();
        int codigo = e.getErrorCode();
        if (estado.startsWith("08") || codigo == 0 && estado.isEmpty()) {
            return "Não foi possível conectar ao banco de dados. Verifique se o MySQL está ligado "
                    + "e se o arquivo reduza.properties está correto.";
        }
        if (codigo == 1062) {
            return "Já existe um cadastro com esse nome ou e-mail.";
        }
        if (codigo == 1451) {
            return "Não é possível excluir: há registros ligados a este cadastro. Desative-o em vez de excluir.";
        }
        if (codigo == 3819 || codigo == 4025) {
            return "Algum valor está fora do permitido (por exemplo, peso zero ou datas invertidas).";
        }
        if (codigo == 1045) {
            return "Usuário ou senha do banco de dados incorretos (veja reduza.properties).";
        }
        if (codigo == 1049) {
            return "O banco reduza_db não existe. Execute o script database/reduza_db.sql no MySQL.";
        }
        return "Erro no banco de dados: " + e.getMessage();
    }
}
