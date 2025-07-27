package br.edu.ifsc.fln.utils;

import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import java.sql.Connection;

public class Conexao {

    private static final Database database = DatabaseFactory.getDatabase("mysql");
    private static final Connection connection = database.conectar();

    public static Connection getConnection() {
        return connection;
    }
}
