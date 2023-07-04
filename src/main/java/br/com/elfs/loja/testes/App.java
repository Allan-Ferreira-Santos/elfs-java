package br.com.elfs.loja.testes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.mindrot.jbcrypt.BCrypt;

import br.com.elfs.loja.dao.MarcaDAO;
import br.com.elfs.loja.dao.PagamentoDAO;
import br.com.elfs.loja.dao.TenisDAO;
import br.com.elfs.loja.dao.TenisEsportivoDAO;
import br.com.elfs.loja.dao.UsuarioDAO;
import br.com.elfs.loja.modelo.Marca;
import br.com.elfs.loja.modelo.Pagamento;
import br.com.elfs.loja.modelo.Tenis;
import br.com.elfs.loja.modelo.TenisEsportivo;
import br.com.elfs.loja.modelo.Usuario;
import br.com.elfs.loja.util.JPAUtil;

public class App {
    private static Scanner scanner = new Scanner(System.in);
    private static boolean logadoOk = false;

    // cria CRUD para usuario

    public void cadastrarUsuario() {
        System.out.print("Digite o nome de usuário: ");
        String nomeUsuario = scanner.nextLine();

        System.out.print("Digite a senha: ");
        String senha = scanner.nextLine();

        String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());

        EntityManager em = JPAUtil.getEntityManager();
        UsuarioDAO usuarioDAO = new UsuarioDAO(em);

        try {
            em.getTransaction().begin();

            Usuario usuarioExistente = usuarioDAO.buscarPorNomeUsuario(nomeUsuario);
            if (usuarioExistente != null) {
                System.out.println("\nUsuário já existe. Não é possível cadastrar novamente.");
                return;
            }

            Usuario novoUsuario = new Usuario(nomeUsuario, senhaCriptografada);
            usuarioDAO.cadastrar(novoUsuario);

            em.getTransaction().commit();
            System.out.println("\nUsuário cadastrado com sucesso!");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("\nErro ao cadastrar usuário: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public Usuario logarUsuario() {
        System.out.print("Digite o nome de usuário: ");
        String nomeUsuario = scanner.nextLine();

        System.out.print("Digite a senha: ");
        String senha = scanner.nextLine();

        EntityManager em = JPAUtil.getEntityManager();
        UsuarioDAO usuarioDAO = new UsuarioDAO(em);

        Usuario usuario = usuarioDAO.buscarPorNomeUsuario(nomeUsuario);
        if (usuario == null) {
            System.out.println("\nUsuário não encontrado.");
            em.close();
            return null;
        }

        if (!BCrypt.checkpw(senha, usuario.getSenha())) {
            System.out.println("\nSenha incorreta.");
            em.close();
            return null;
        }

        usuario.setLogado(true);
        usuarioDAO.atualizar(usuario);
        logadoOk = true;

        em.close();

        System.out.println("\nLogin realizado com sucesso!");
        return usuario;
    }

    public Usuario deslogarUsuario() {
        System.out.print("Digite o nome de usuário: ");
        String nomeUsuario = scanner.nextLine();

        System.out.print("Digite a senha: ");
        String senha = scanner.nextLine();

        EntityManager em = JPAUtil.getEntityManager();
        UsuarioDAO usuarioDAO = new UsuarioDAO(em);

        Usuario usuario = usuarioDAO.buscarPorNomeUsuario(nomeUsuario);
        if (usuario == null || !BCrypt.checkpw(senha, usuario.getSenha())) {
            System.out.println("Nome de usuário ou senha inválidos. Tente novamente.");
            em.close();
            return null;
        }
        usuario.setLogado(false);
        usuarioDAO.atualizar(usuario);
        logadoOk = false;
        em.close();
        System.out.println("Usuário deslogado com sucesso!");

        return usuario;
    }

    public void listarUsuarios() {
        EntityManager em = JPAUtil.getEntityManager();
        UsuarioDAO usuarioDAO = new UsuarioDAO(em);

        List<Usuario> usuarios = usuarioDAO.buscarTodos();
        System.out.println("Lista de Usuários:");
        for (Usuario usuario : usuarios) {
            System.out.println(usuario.getId() + ", " + usuario.getNomeUsuario());
        }

        em.close();
    }

    public Usuario atualizarUsuario() {
        listarUsuarios();

        System.out.print("Digite o nome de usuário: ");
        String nomeUsuario = scanner.nextLine();

        System.out.print("Digite a senha: ");
        String senha = scanner.nextLine();

        EntityManager em = JPAUtil.getEntityManager();
        UsuarioDAO usuarioDAO = new UsuarioDAO(em);

        Usuario usuario = usuarioDAO.buscarPorNomeUsuario(nomeUsuario);
        if (usuario == null) {
            System.out.println("Usuário não encontrado. Não é possível atualizar.");
            em.close();
            return null;
        }

        if (!BCrypt.checkpw(senha, usuario.getSenha())) {
            System.out.println("Senha incorreta.");
            em.close();
            return null;
        }

        if (!usuario.isLogado()) {
            System.out.println("Usuário não está logado nesta conta. Não é possível atualizar.");
            em.close();
            return null;
        }

        System.out.print("Digite o novo nome de usuário (ou pressione Enter para manter o mesmo): ");
        String novoNomeUsuario = scanner.nextLine();
        if (novoNomeUsuario.isEmpty()) {
            novoNomeUsuario = usuario.getNomeUsuario(); // Mantém o valor existente
        } else {
            // Verifica se o novo nome de usuário já existe no banco
            Usuario usuarioExistente = usuarioDAO.buscarPorNomeUsuario(novoNomeUsuario);
            if (usuarioExistente != null) {
                System.out.println("O novo nome de usuário já está em uso. Não é possível atualizar.");
                em.close();
                return null;
            }
        }

        System.out.print("Digite a nova senha (ou pressione Enter para manter a mesma): ");
        String novaSenha = scanner.nextLine();
        if (novaSenha.isEmpty()) {
            novaSenha = usuario.getSenha(); // Mantém o valor existente
        } else {
            // Criptografa a nova senha
            novaSenha = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
        }

        usuario.setNomeUsuario(novoNomeUsuario);
        usuario.setSenha(novaSenha);
        usuarioDAO.atualizar(usuario);

        em.close();

        System.out.println("Usuário atualizado com sucesso!");
        return usuario;
    }

    public Usuario excluirUsuario() {
        System.out.print("Digite o nome de usuário: ");
        String nomeUsuario = scanner.nextLine();

        System.out.print("Digite a senha: ");
        String senha = scanner.nextLine();

        EntityManager em = JPAUtil.getEntityManager();
        UsuarioDAO usuarioDAO = new UsuarioDAO(em);

        Usuario usuario = usuarioDAO.buscarPorNomeUsuario(nomeUsuario);
        if (usuario == null) {
            System.out.println("\nUsuário não encontrado.");
            em.close();
            return null;
        }

        if (!BCrypt.checkpw(senha, usuario.getSenha())) {
            System.out.println("\nSenha incorreta.");
            em.close();
            return null;
        }

        usuarioDAO.excluir(usuario);
        em.close();

        System.out.println("Usuário excluído com sucesso!");
        logadoOk = false;
        return usuario;
    }

    // CRUD para a classe Tênis
    public void cadastrarTenis() {
        System.out.println("=== Cadastro de Tênis ===");

        System.out.print("Digite o nome do tênis: ");
        String nome = scanner.nextLine();

        System.out.print("Digite o tamanho do tênis: ");
        int tamanho = Integer.parseInt(scanner.nextLine());

        System.out.print("Digite o preço do tênis: ");
        int preco = Integer.parseInt(scanner.nextLine());

        System.out.print("Digite a marca do tênis: ");
        String marcaNome = scanner.nextLine();

        EntityManager em = JPAUtil.getEntityManager();
        TenisDAO tenisDAO = new TenisDAO(em);
        MarcaDAO marcaDAO = new MarcaDAO(em);

        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            Marca marca = marcaDAO.buscarPorNome(marcaNome);
            if (marca == null) {
                marca = new Marca(marcaNome);
                marcaDAO.cadastrar(marca);
            }

            Tenis tenis = new Tenis(nome, tamanho, preco, marca);
            tenisDAO.cadastrar(tenis);
            System.out.println("Tênis cadastrado com sucesso!");

            if (transaction.isActive()) {
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao cadastrar tênis: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public void buscarTodosTenis() {
        EntityManager em = JPAUtil.getEntityManager();
        TenisDAO tenisDAO = new TenisDAO(em);

        List<Tenis> tenisList = tenisDAO.buscarTodos();
        System.out.println("Tênis cadastrados:");

        for (Tenis tenis : tenisList) {
            System.out.println(tenis.getId() + " - " + tenis.getNome());
        }
    }

    public void buscarTenisPorNome() {
        System.out.print("Digite o nome do tênis que deseja buscar: ");
        String nomeTenis = scanner.nextLine();

        EntityManager em = JPAUtil.getEntityManager();
        TenisDAO tenisDAO = new TenisDAO(em);

        List<Tenis> tenisList = tenisDAO.buscarPorNome(nomeTenis);

        if (tenisList.isEmpty()) {
            System.out.println("Nenhum tênis encontrado com o nome informado.");
        } else {
            System.out.println("Detalhes do tênis:");
            for (Tenis tenis : tenisList) {
                System.out.println("ID: " + tenis.getId());
                System.out.println("Nome: " + tenis.getNome());
                System.out.println("Marca: " + tenis.getMarca());
                System.out.println("Tamanho: " + tenis.getTamanho());
                System.out.println("Preço: " + tenis.getPreco());
            }
        }
        em.close();
    }

    public Tenis atualizarTenis() {
        buscarTodosTenis();

        EntityManager em = JPAUtil.getEntityManager();
        TenisDAO tenisDAO = new TenisDAO(em);

        System.out.print("Digite o ID do tênis que deseja atualizar: ");
        Long id = Long.parseLong(scanner.nextLine());

        Tenis tenis = tenisDAO.buscarPorId(id);
        if (tenis == null) {
            System.out.println("Tênis não encontrado.");
            em.close();
            return null;
        }

        System.out.print("Digite o novo nome do tênis (ou enter para manter o atual): ");
        String novoNome = scanner.nextLine();
        if (!novoNome.isEmpty()) {
            tenis.setNome(novoNome);
        }

        System.out.print("Digite o novo tamanho do tênis (ou enter para manter o atual): ");
        String novoTamanhoStr = scanner.nextLine();
        if (!novoTamanhoStr.isEmpty()) {
            int novoTamanho = Integer.parseInt(novoTamanhoStr);
            tenis.setTamanho(novoTamanho);
        }

        System.out.print("Digite o novo preço do tênis (ou enter para manter o atual): ");
        String novoPrecoStr = scanner.nextLine();
        if (!novoPrecoStr.isEmpty()) {
            int novoPreco = Integer.parseInt(novoPrecoStr);
            tenis.setPreco(novoPreco);
        }

        System.out.print("Digite a nova marca do tênis (ou enter para manter a atual): ");
        String novaMarca = scanner.nextLine();
        if (!novaMarca.isEmpty()) {
            Marca marcaExistente = tenis.getMarca();
            if (marcaExistente == null || !marcaExistente.getNome().equals(novaMarca)) {
                MarcaDAO marcaDAO = new MarcaDAO(em);
                Marca marca = marcaDAO.buscarPorNome(novaMarca);
                if (marca == null) {
                    marca = new Marca(novaMarca);
                    marcaDAO.cadastrar(marca);
                }
                tenis.setMarca(marca);
            }
        }

        tenisDAO.atualizar(tenis);
        em.close();

        System.out.println("Tênis atualizado com sucesso!");

        return tenis;
    }

    public void excluirTenis() {
        System.out.println("Tênis disponíveis para exclusão:");
        buscarTodosTenis();

        EntityManager em = JPAUtil.getEntityManager();
        TenisDAO tenisDAO = new TenisDAO(em);

        System.out.print("Digite o ID do tênis que deseja excluir: ");
        Long id = Long.parseLong(scanner.nextLine());

        Tenis tenis = tenisDAO.buscarPorId(id);
        if (tenis == null) {
            System.out.println("Tênis não encontrado. Nenhuma exclusão realizada.");
            em.close();
            return;
        }

        tenisDAO.excluir(tenis);
        em.close();

        System.out.println("Tênis excluído com sucesso!");
    }

    // CRUD para a classe TenisEsportivo

    public void cadastrarTenisEsportivo() {
        System.out.println("=== Cadastro de Tênis Esportivo ===");

        System.out.print("Digite o nome do tênis: ");
        String nome = scanner.nextLine();

        System.out.print("Digite o tamanho do tênis: ");
        int tamanho = Integer.parseInt(scanner.nextLine());

        System.out.print("Digite o preço do tênis: ");
        int preco = Integer.parseInt(scanner.nextLine());

        System.out.print("Digite a marca do tênis: ");
        String nomeMarca = scanner.nextLine();

        System.out.print("Digite a modalidade do tênis: ");
        String modalidade = scanner.nextLine();

        EntityManager em = JPAUtil.getEntityManager();
        TenisEsportivoDAO tenisEsportivoDAO = new TenisEsportivoDAO(em);
        MarcaDAO marcaDAO = new MarcaDAO(em);

        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            Marca marca = marcaDAO.buscarPorNome(nomeMarca);
            if (marca == null) {
                marca = new Marca(nomeMarca);
                marcaDAO.cadastrar(marca);
            }

            TenisEsportivo tenisEsportivo = new TenisEsportivo(nome, tamanho, preco, marca, modalidade);
            tenisEsportivoDAO.cadastrar(tenisEsportivo);
            System.out.println("Tenis cadastrado com sucesso!");

            transaction.commit();

            if (transaction.isActive()) {
                transaction.rollback();
            }

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao cadastrar tenis: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public void buscarTodosTenisEsportivos() {
        EntityManager em = JPAUtil.getEntityManager();
        TenisEsportivoDAO tenisEsportivoDAO = new TenisEsportivoDAO(em);

        List<TenisEsportivo> tenisEsportivos = tenisEsportivoDAO.buscarTodos();
        System.out.println("Tênis esportivos cadastrados:");

        for (TenisEsportivo tenisEsportivo : tenisEsportivos) {
            System.out.println(tenisEsportivo.getId() + " - " + tenisEsportivo.getNome());
        }

        em.close();
    }

    public void buscarTenisEsportivoPorNome() {
        System.out.print("Digite o nome do tênis esportivo que deseja buscar: ");
        String nomeTenis = scanner.nextLine();

        EntityManager em = JPAUtil.getEntityManager();
        TenisEsportivoDAO tenisEsportivoDAO = new TenisEsportivoDAO(em);

        List<TenisEsportivo> tenisEsportivos = tenisEsportivoDAO.buscarPorNome(nomeTenis);

        if (tenisEsportivos.isEmpty()) {
            System.out.println("Nenhum tênis esportivo encontrado com o nome informado.");
        } else {
            System.out.println("Detalhes do tênis esportivo:");
            for (TenisEsportivo tenisEsportivo : tenisEsportivos) {
                System.out.println("ID: " + tenisEsportivo.getId());
                System.out.println("Nome: " + tenisEsportivo.getNome());
                System.out.println("Tamanho: " + tenisEsportivo.getTamanho());
                System.out.println("Preço: " + tenisEsportivo.getPreco());
                System.out.println("Marca: " + tenisEsportivo.getMarca().getNome());
                System.out.println("Modalidade: " + tenisEsportivo.getModalidade());
            }
        }

        em.close();
    }

    public TenisEsportivo atualizarTenisEsportivo() {
        buscarTodosTenisEsportivos();

        EntityManager em = JPAUtil.getEntityManager();
        TenisEsportivoDAO tenisEsportivoDAO = new TenisEsportivoDAO(em);

        System.out.print("Digite o ID do tênis esportivo que deseja atualizar: ");
        Long id = Long.parseLong(scanner.nextLine());

        TenisEsportivo tenisEsportivo = tenisEsportivoDAO.buscarPorId(id);
        if (tenisEsportivo == null) {
            System.out.println("Tênis esportivo não encontrado.");
            em.close();
            return null;
        }

        System.out.print("Digite o novo nome do tênis esportivo (ou enter para manter o atual): ");
        String novoNome = scanner.nextLine();
        if (!novoNome.isEmpty()) {
            tenisEsportivo.setNome(novoNome);
        }

        System.out.print("Digite o novo tamanho do tênis esportivo (ou enter para manter o atual): ");
        String novoTamanhoStr = scanner.nextLine();
        if (!novoTamanhoStr.isEmpty()) {
            int novoTamanho = Integer.parseInt(novoTamanhoStr);
            tenisEsportivo.setTamanho(novoTamanho);
        }

        System.out.print("Digite o novo preço do tênis esportivo (ou enter para manter o atual): ");
        String novoPrecoStr = scanner.nextLine();
        if (!novoPrecoStr.isEmpty()) {
            int novoPreco = Integer.parseInt(novoPrecoStr);
            tenisEsportivo.setPreco(novoPreco);
        }

        System.out.print("Digite a nova marca do tênis esportivo (ou enter para manter a atual): ");
        String novaMarca = scanner.nextLine();
        if (!novaMarca.isEmpty()) {
            Marca marcaExistente = tenisEsportivo.getMarca();
            if (marcaExistente == null || !marcaExistente.getNome().equals(novaMarca)) {
                MarcaDAO marcaDAO = new MarcaDAO(em);
                Marca marca = marcaDAO.buscarPorNome(novaMarca);
                if (marca == null) {
                    marca = new Marca(novaMarca);
                    marcaDAO.cadastrar(marca);
                }
                tenisEsportivo.setMarca(marca);
            }
        }

        System.out.print("Digite a nova modalidade do tênis esportivo (ou enter para manter a atual): ");
        String novaModalidade = scanner.nextLine();
        if (!novaModalidade.isEmpty()) {
            tenisEsportivo.setModalidade(novaModalidade);
        }

        tenisEsportivoDAO.atualizar(tenisEsportivo);
        em.close();

        System.out.println("Tênis esportivo atualizado com sucesso!");

        return tenisEsportivo;
    }

    public void excluirTenisEsportivo() {

        System.out.println("Tênis esportivos disponíveis para exclusão:");
        buscarTodosTenisEsportivos();

        EntityManager em = JPAUtil.getEntityManager();
        TenisEsportivoDAO tenisEsportivoDAO = new TenisEsportivoDAO(em);

        System.out.print("Digite o ID do tênis esportivo que deseja excluir: ");
        Long id = Long.parseLong(scanner.nextLine());

        TenisEsportivo tenisEsportivo = tenisEsportivoDAO.buscarPorId(id);
        if (tenisEsportivo == null) {
            System.out.println("Tênis esportivo não encontrado.");
            em.close();
            return;
        }

        tenisEsportivoDAO.excluir(tenisEsportivo);
        System.out.println("Tênis esportivo excluído com sucesso!");
        em.close();

    }

    // CRUD para a classe Pagamentos

    public void CadastrarPagamento() {
        System.out.print("Digite o valor do pagamento: ");
        double valor = Double.parseDouble(scanner.nextLine());

        System.out.print("Digite a data do pagamento (formato: dd/mm/aaaa): ");
        String dataStr = scanner.nextLine();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date data;

        try {
            data = new java.sql.Date(dateFormat.parse(dataStr).getTime());
        } catch (ParseException e) {
            System.out.println("Formato de data inválido. Não foi possível cadastrar o pagamento.");
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        PagamentoDAO pagamentoDAO = new PagamentoDAO(em);
        UsuarioDAO usuarioDAO = new UsuarioDAO(em);

        Usuario usuarioLogado = usuarioDAO.buscarUsuarioLogado();
        if (usuarioLogado == null) {
            System.out.println("Nenhum usuário está logado. Não é possível cadastrar o pagamento.");
            return;
        }

        try {
            transaction.begin();

            Long idUsuario = usuarioLogado.getId();

            Pagamento pagamento = new Pagamento(valor, data, idUsuario);
            pagamento.setUsuario(usuarioLogado);
            pagamentoDAO.cadastrar(pagamento);

            transaction.commit();

            System.out.println("Pagamento cadastrado");
        } catch (Exception e) {
            transaction.rollback();
            System.out.println("Erro ao cadastrar o pagamento. Transação foi rollbacked.");
        } finally {
            em.close();
        }
    }

    public void listarPagamentos() {
        EntityManager em = JPAUtil.getEntityManager();
        PagamentoDAO pagamentoDAO = new PagamentoDAO(em);

        List<Pagamento> pagamentos = pagamentoDAO.buscarTodos();

        if (pagamentos.isEmpty()) {
            System.out.println("Não há pagamentos cadastrados.");
        } else {
            System.out.println("Lista de Pagamentos:");
            for (Pagamento pagamento : pagamentos) {
                System.out.println("ID: " + pagamento.getId() + ", Data: " + pagamento.getData() + ", Valor: "
                        + pagamento.getValor() + ", Usuário: "
                        + pagamento.getUsuario().getNomeUsuario());
            }
        }

        em.close();
    }

    public void atualizarPagamento() {
        listarPagamentos();

        System.out.print("Digite o ID do pagamento que deseja atualizar: ");
        Long pagamentoId = Long.parseLong(scanner.nextLine());

        EntityManager em = JPAUtil.getEntityManager();
        PagamentoDAO pagamentoDAO = new PagamentoDAO(em);

        em.getTransaction().begin();

        Pagamento pagamento = pagamentoDAO.buscarPorId(pagamentoId);
        if (pagamento == null) {
            System.out.println("Pagamento não encontrado.");
        } else {
            System.out.println("Opções de atualização:");
            System.out.println("1. Atualizar preço");
            System.out.println("2. Atualizar data");
            System.out.println("3. Atualizar usuário");
            System.out.println("Pressione Enter para manter o valor anterior");

            System.out.print("Digite a opção desejada: ");
            String opcao = scanner.nextLine();

            if (opcao.equals("1")) {
                System.out.print("Digite o novo preço: ");
                String precoStr = scanner.nextLine();
                double novoPreco = precoStr.isEmpty() ? pagamento.getValor() : Double.parseDouble(precoStr);
                pagamento.setValor(novoPreco);
            } else if (opcao.equals("2")) {
                System.out.print("Digite a nova data (formato: dd/mm/aaaa): ");
                String dataStr = scanner.nextLine();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date novaData;
                try {
                    novaData = dataStr.isEmpty() ? pagamento.getData() : dateFormat.parse(dataStr);
                    java.sql.Date novaDataSql = new java.sql.Date(novaData.getTime());
                    pagamento.setData(novaDataSql);
                } catch (ParseException e) {
                    System.out.println("Formato de data inválido. Não foi possível atualizar o pagamento.");
                    em.getTransaction().rollback();
                    em.close();
                    return;
                }
            } else if (opcao.equals("3")) {
                UsuarioDAO usuarioDAO = new UsuarioDAO(em);
                listarUsuarios();
                System.out.print("Digite o ID do novo usuário: ");
                Long novoUsuarioId = Long.parseLong(scanner.nextLine());
                Usuario novoUsuario = usuarioDAO.buscarPorId(novoUsuarioId);
                if (novoUsuario == null) {
                    System.out.println("Usuário não encontrado. Não foi possível atualizar o pagamento.");
                    em.getTransaction().rollback();
                    em.close();
                    return;
                }
                pagamento.setUsuario(novoUsuario);
            }

            pagamentoDAO.atualizar(pagamento);
            em.getTransaction().commit();
            System.out.println("Pagamento atualizado com sucesso.");
        }

        em.close();
    }

    public void deletarPagamento() {
        listarPagamentos();

        System.out.print("Digite o ID do pagamento que deseja excluir: ");
        Long pagamentoId = Long.parseLong(scanner.nextLine());

        EntityManager em = JPAUtil.getEntityManager();
        PagamentoDAO pagamentoDAO = new PagamentoDAO(em);

        em.getTransaction().begin();

        Pagamento pagamento = pagamentoDAO.buscarPorId(pagamentoId);
        if (pagamento == null) {
            System.out.println("Pagamento não encontrado.");
        } else {
            pagamentoDAO.excluir(pagamento);
            em.getTransaction().commit();

            System.out.println("Pagamento excluído com sucesso.");
        }

        em.close();
    }

    public void finalizarPrograma() {
        EntityManager em = JPAUtil.getEntityManager();
        UsuarioDAO usuarioDAO = new UsuarioDAO(em);

        List<Usuario> usuarios = usuarioDAO.buscarTodos();
        for (Usuario usuario : usuarios) {
            usuario.setLogado(false);
            usuarioDAO.atualizar(usuario);
        }

        em.close();

        System.out.println("Programa finalizado.");
    }

    public static void main(String[] args) {
        App app = new App();
        boolean rodarPrograma = true;

        while (rodarPrograma) {
            while (!logadoOk && rodarPrograma) {
                System.out.println(
                        "Escolha uma opção: \n-1. Cadastrar Usuário\n-2. Listar Usuários \n-3. Logar Usuário\n-4. Deslogar Usuário\n-5. Listar Tênis Cadastrados \n-0. Parar o programa\n");
                String escolha = scanner.nextLine();

                if (escolha.equals("1")) {
                    app.cadastrarUsuario();
                } else if (escolha.equals("2")) {
                    app.listarUsuarios();
                } else if (escolha.equals("3")) {
                    app.logarUsuario();
                } else if (escolha.equals("4")) {
                    app.deslogarUsuario();
                } else if (escolha.equals("5")) {
                    app.buscarTodosTenis();
                } else if (escolha.equals("0")) {
                    app.finalizarPrograma();
                    rodarPrograma = false;
                }
            }

            while (logadoOk && rodarPrograma) {
                System.out.println(
                        "\nEscolha uma opção: \n-1. Cadastrar Tênis \n-2. Atualizar Tênis \n-3. Buscar Todos Tênis \n-4. Buscar Tênis por Nome \n-5. Excluir Tênis Cadastrado\n-6. cadastrar tenis Esportivo\n-7. Atualizar tenis Esportivo \n-8. buscar todos tenis Esportivo \n-9. buscar por nome tenis Esportivo \n-10. excluir tenis Esportivo \n-11. Atualizar Usuário \n-12. Excluir Usuário \n-13. Deslogar Usuário \n-14. Cadastrar Pagamento \n-15. Atualizar Pagamento \n-16. Buscar Todos Pagamentos \n-17. Excluir Pagamento \n-0. Para parar o programa");
                String escolha = scanner.nextLine();

                if (escolha.equals("1")) {
                    app.cadastrarTenis();
                } else if (escolha.equals("2")) {
                    app.atualizarTenis();
                } else if (escolha.equals("3")) {
                    app.buscarTodosTenis();
                } else if (escolha.equals("4")) {
                    app.buscarTenisPorNome();
                } else if (escolha.equals("5")) {
                    app.excluirTenis();
                } else if (escolha.equals("6")) {
                    app.cadastrarTenisEsportivo();
                } else if (escolha.equals("7")) {
                    app.atualizarTenisEsportivo();
                } else if (escolha.equals("8")) {
                    app.buscarTodosTenisEsportivos();
                } else if (escolha.equals("9")) {
                    app.buscarTenisEsportivoPorNome();
                } else if (escolha.equals("10")) {
                    app.excluirTenisEsportivo();
                } else if (escolha.equals("11")) {
                    app.atualizarUsuario();
                } else if (escolha.equals("12")) {
                    app.excluirUsuario();
                } else if (escolha.equals("13")) {
                    app.deslogarUsuario();
                } else if (escolha.equals("14")) {
                    app.CadastrarPagamento();
                } else if (escolha.equals("15")) {
                    app.atualizarPagamento();
                } else if (escolha.equals("16")) {
                    app.listarPagamentos();
                } else if (escolha.equals("17")) {
                    app.deletarPagamento();
                } else if (escolha.equals("0")) {
                    app.finalizarPrograma();
                    rodarPrograma = false;
                }
            }
        }
    }
}
