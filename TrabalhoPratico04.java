/*******************************************************************************
 * Autor: Emerson da Silva Oliveira - CB303853-X
 * Disciplina: CBTLPR2 (Java) - IFSP Campus Cubatão
 * Trabalho Prático 04
 ******************************************************************************/

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrabalhoPratico04 extends JFrame {
    
    // Componentes da Interface Gráfica
    private JTextField txtPesquisaNome;
    private JButton btnPesquisar;
    private JTextField txtNome;
    private JTextField txtSalario;
    private JTextField txtCargo;
    private JButton btnAnterior;
    private JButton btnProximo;

    // Estrutura de dados para armazenar o resultado da pesquisa e navegação
    private List<FuncionarioDTO> listaFuncionarios = new ArrayList<>();
    private int indiceAtual = -1;

    // Configurações de Conexão JDBC (Ajuste usuário e senha conforme seu ambiente)
    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=aulajava;encrypt=true;trustServerCertificate=true;";
    private static final String USUARIO = "sa";
    private static final String SENHA = "meugatochuvisco";

    public TrabalhoPratico04() {
        setTitle("TRABALHO PRATICO 04");
        setSize(440, 310);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Componentes de Pesquisa
        JLabel lblPesquisa = new JLabel("Nome:");
        lblPesquisa.setBounds(30, 20, 50, 25);
        add(lblPesquisa);

        txtPesquisaNome = new JTextField();
        txtPesquisaNome.setBounds(80, 20, 230, 25);
        add(txtPesquisaNome);

        btnPesquisar = new JButton("Pesquisar");
        btnPesquisar.setBounds(140, 55, 110, 25);
        add(btnPesquisar);

        // Componentes de Exibição de Resultados
        JLabel lblNomeRes = new JLabel("Nome:");
        lblNomeRes.setBounds(30, 100, 70, 25);
        add(lblNomeRes);

        txtNome = new JTextField();
        txtNome.setBounds(100, 100, 280, 25);
        txtNome.setEditable(false);
        add(txtNome);

        JLabel lblSalarioRes = new JLabel("Salário:");
        lblSalarioRes.setBounds(30, 135, 70, 25);
        add(lblSalarioRes);

        txtSalario = new JTextField();
        txtSalario.setBounds(100, 135, 280, 25);
        txtSalario.setEditable(false);
        add(txtSalario);

        JLabel lblCargoRes = new JLabel("Cargo:");
        lblCargoRes.setBounds(30, 170, 70, 25);
        add(lblCargoRes);

        txtCargo = new JTextField();
        txtCargo.setBounds(100, 170, 280, 25);
        txtCargo.setEditable(false);
        add(txtCargo);

        // Botões de Navegação (Anterior e Próximo)
        btnAnterior = new JButton("Anterior");
        btnAnterior.setBounds(30, 215, 170, 30);
        add(btnAnterior);

        btnProximo = new JButton("Próximo");
        btnProximo.setBounds(210, 215, 170, 30);
        add(btnProximo);

        // Registro de Ações (Listeners)
        btnPesquisar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pesquisarFuncionarios();
            }
        });

        btnAnterior.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navegarAnterior();
            }
        });

        btnProximo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navegarProximo();
            }
        });

        atualizarBotoes();
    }

    // Método para realizar a consulta com PreparedStatement e LIKE (Requisito 5)
    private void pesquisarFuncionarios() {
        listaFuncionarios.clear();
        indiceAtual = -1;
        
        String termoPesquisa = "%" + txtPesquisaNome.getText().trim() + "%";
        String sql = "SELECT f.nome_func, f.sal_func, c.ds_cargo " +
                     "FROM tbfuncs f INNER JOIN tbcargos c ON f.cod_cargo = c.cd_cargo " +
                     "WHERE f.nome_func LIKE ?";

        try (Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, termoPesquisa);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome_func").trim();
                    double salario = rs.getDouble("sal_func");
                    String cargo = rs.getString("ds_cargo").trim();
                    listaFuncionarios.add(new FuncionarioDTO(nome, salario, cargo));
                }
            }

            if (!listaFuncionarios.isEmpty()) {
                indiceAtual = 0;
                exibirFuncionario(indiceAtual);
            } else {
                limparCampos();
                JOptionPane.showMessageDialog(this, "Nenhum registro encontrado.");
            }
            atualizarBotoes();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro de conexão ou SQL: " + ex.getMessage());
        }
    }

    private void exibirFuncionario(int indice) {
        if (indice >= 0 && indice < listaFuncionarios.size()) {
            FuncionarioDTO f = listaFuncionarios.get(indice);
            txtNome.setText(f.getNome());
            txtSalario.setText(String.format("%.2f", f.getSalario()));
            txtCargo.setText(f.getCargo());
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtSalario.setText("");
        txtCargo.setText("");
    }

    // Navegação anterior (Requisito 6)
    private void navegarAnterior() {
        if (indiceAtual > 0) {
            indiceAtual--;
            exibirFuncionario(indiceAtual);
            atualizarBotoes();
        }
    }

    // Navegação próximo (Requisito 6)
    private void navegarProximo() {
        if (indiceAtual < listaFuncionarios.size() - 1) {
            indiceAtual++;
            exibirFuncionario(indiceAtual);
            atualizarBotoes();
        }
    }

    // Controle de limites inicial e final para os botões (Requisito 6)
    private void atualizarBotoes() {
        btnAnterior.setEnabled(indiceAtual > 0);
        btnProximo.setEnabled(indiceAtual >= 0 && indiceAtual < listaFuncionarios.size() - 1);
    }

    // Classe auxiliar para estruturar os dados do registro
    private static class FuncionarioDTO {
        private String nome;
        private double salario;
        private String cargo;

        public FuncionarioDTO(String nome, double salario, String cargo) {
            this.nome = nome;
            this.salario = salario;
            this.cargo = cargo;
        }

        public String getNome() { return nome; }
        public double getSalario() { return salario; }
        public String getCargo() { return cargo; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TrabalhoPratico04().setVisible(true);
            }
        });
    }
}