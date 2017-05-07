package br.com.rentcar.dao;

import br.com.rentcar.model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ClienteDAO {
    

    
    
    

    private Connection conexao;

    public ClienteDAO() {
        conexao = ConnectionFactory.getConnection();
    }

    public boolean inserirCliente(Cliente cliente) {

        boolean resultado = false;

        String sql = "insert into clientes (cpf, name) values (?,?)";

        try {
            conexao.setAutoCommit(false);
            
            PreparedStatement stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, cliente.getCpf());
            stmt.setString(2, cliente.getNome());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            rs.first();
            int codigo = rs.getInt(1);
            cliente.setCodigo(codigo);
            stmt.close();

            //chama o métdo para inserir o endereço do cliente
            boolean inseriu = inserirEndereco(cliente);    
            
            if(inseriu){
                conexao.commit();
                resultado = true;
            }
            
        } catch (SQLException ex) {
            try {
                conexao.rollback();
            } catch (SQLException ex1) {
               JOptionPane.showMessageDialog(null,"Erro ao reverter a operação - " + ex1.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(null,"Erro ao inserir o cliente - " + ex.getMessage());
        } finally {

            try {
                conexao.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,"Erro ao fechar conexão - " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }

        return resultado;
    }

    public boolean inserirEndereco(Cliente cliente) throws SQLException {
        //não coloquei try-catch para se der erro, 
        //o erro será tratado no método inserirCliente;
        boolean resultado = false;
        
        String sql = "insert into enderecos (rua, bairro, codigo_cliente) values (?,?,?)";

        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, cliente.getEndereco().getRua());
        stmt.setString(2, cliente.getEndereco().getBairro());
        stmt.setInt(3, cliente.getCodigo());

        stmt.executeUpdate();
        resultado = true;
        
        stmt.close();                
        return resultado;
    }

}
