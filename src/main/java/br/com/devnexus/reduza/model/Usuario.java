package br.com.devnexus.reduza.model;

/** Pessoa que usa o sistema. A senha fica só no banco, em forma de hash. */
public record Usuario(int id, String nome, String email, Perfil perfil, boolean ativo) {

    public String iniciais() {
        String[] partes = nome.trim().split("\\s+");
        String a = partes[0].substring(0, 1);
        String b = partes.length > 1 ? partes[partes.length - 1].substring(0, 1) : "";
        return (a + b).toUpperCase();
    }

    @Override
    public String toString() {
        return nome;
    }
}
