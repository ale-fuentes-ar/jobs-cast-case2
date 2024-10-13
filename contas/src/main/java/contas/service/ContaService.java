package contas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import contas.model.Conta;
import contas.repitory.ContaRepository;
import jakarta.transaction.Transactional;

@Service
public class ContaService {

	static final String MSG_CONTA_NAO_EXISTE = "A conta nao existe";
	static final String MSG_SALDO_INSUFICIENTE = "Saldo insuficiente!";
	
	@Autowired
	private ContaRepository contaRepository;

	@Transactional
	public Conta criaConta(String nome) {
		Conta conta = new Conta();
		conta.setTitular(nome);
		conta.setSaldo(0.0);
		return contaRepository.save(conta);
	}
	
	@Transactional
	public Conta visualizarConta(Long id) {
	    return contaRepository.findById(id).orElseThrow(() -> new RuntimeException(String.format("%s: %d", MSG_CONTA_NAO_EXISTE, id)));
	}

	@Transactional
	public void creditar(Long id, double valor) {
		   Conta conta = contaRepository.findByIdWithLock(id)
		            .orElseThrow(() -> new RuntimeException(String.format("%s: %d", MSG_CONTA_NAO_EXISTE, id)));
		    conta.setSaldo(conta.getSaldo() + valor);
		    contaRepository.save(conta);
	}

	@Transactional
	public void debitar(Long id, double valor) {
	    Conta conta = contaRepository.findByIdWithLock(id)
	            .orElseThrow(() -> new RuntimeException(String.format("%s: %d", MSG_CONTA_NAO_EXISTE, id)));
	    if (conta.getSaldo() < valor) {
	        throw new IllegalArgumentException(MSG_SALDO_INSUFICIENTE);
	    }
	    conta.setSaldo(conta.getSaldo() - valor);
	    contaRepository.save(conta);
	}

	@Transactional
	public void transferir(Long idOrigem, Long idDestino, double valor) {
		
        Conta contaOrigem = contaRepository.findByIdWithLock(idOrigem)
                .orElseThrow(() -> new RuntimeException(String.format("%s: %d", MSG_CONTA_NAO_EXISTE, idOrigem)));

        Conta contaDestino = contaRepository.findByIdWithLock(idDestino)
                .orElseThrow(() -> new RuntimeException(String.format("%s: %d", MSG_CONTA_NAO_EXISTE, idDestino)));

        if (contaOrigem.getSaldo() < valor) {
            throw new RuntimeException(String.format("%s: %d", MSG_SALDO_INSUFICIENTE, idOrigem));
        }

        debitar(contaOrigem.getId(), valor);
        creditar(contaDestino.getId(), valor);
	}

}
